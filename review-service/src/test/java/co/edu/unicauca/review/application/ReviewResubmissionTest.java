package co.edu.unicauca.review.application;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unicauca.review.application.event.QuestionResubmittedEvent;
import co.edu.unicauca.review.domain.model.ReviewAssignment;
import co.edu.unicauca.review.domain.repository.ReviewAssignmentRepository;
import co.edu.unicauca.review.application.port.ReviewEventPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewResubmissionTest {

    @Mock
    private ReviewAssignmentRepository repository;

    @Mock
    private ReviewEventPort eventPublisher;

    @InjectMocks
    private ReviewApplicationService service;

    @Test
    void shouldRegisterNewQuestionAsPending() {
        when(repository.findByQuestionId(100L)).thenReturn(Optional.empty());
        when(repository.save(any(ReviewAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReviewAssignment result = service.registerPendingQuestion(100L, 10L);

        assertTrue(result.getReviews().isEmpty());
        verify(repository).save(any(ReviewAssignment.class));
    }

    @Test
    void shouldStartNewRoundWhenRejectedQuestionIsSentAgain() {
        ReviewAssignment old = new ReviewAssignment(100L, 10L);
        old.assignReviewer(20L);
        old.getReviews().get(0).reject("Falta fuente");
        when(repository.findByQuestionId(100L)).thenReturn(Optional.of(old));
        when(repository.save(any(ReviewAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ReviewAssignment result = service.registerPendingQuestion(100L, 10L);

        assertTrue(result.getReviews().isEmpty());
        verify(repository).save(old);
    }

    @Test
    void shouldPublishResubmissionEventWithNewVersionAndPreviousReviewers() {
        ReviewAssignment old = new ReviewAssignment(100L, 10L);
        old.assignReviewer(20L);
        old.getReviews().get(0).reject("Falta fuente");
        when(repository.findByQuestionId(100L)).thenReturn(Optional.of(old));
        when(repository.save(any(ReviewAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.registerPendingQuestion(100L, 10L);

        org.mockito.ArgumentCaptor<QuestionResubmittedEvent> captor =
                org.mockito.ArgumentCaptor.forClass(QuestionResubmittedEvent.class);
        verify(eventPublisher).publishQuestionResubmitted(captor.capture());
        assertEquals(100L, captor.getValue().getQuestionId());
        assertEquals(2, captor.getValue().getVersion());
        assertEquals(java.util.List.of(20L), captor.getValue().getPreviousReviewerIds());
    }

    @Test
    void shouldIgnoreDuplicatedEventWhileReviewIsInProgress() {
        ReviewAssignment inProgress = new ReviewAssignment(100L, 10L);
        inProgress.assignReviewer(20L);
        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.of(inProgress));

        ReviewAssignment result = service.registerPendingQuestion(100L, 10L);

        assertEquals(1, result.getReviews().size());
        verify(repository, never()).save(any());
        verify(eventPublisher, never()).publishQuestionResubmitted(any());
    }
}
