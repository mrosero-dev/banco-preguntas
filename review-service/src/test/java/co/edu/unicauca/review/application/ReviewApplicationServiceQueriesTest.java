package co.edu.unicauca.review.application;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unicauca.review.application.event.ReviewAssignedEvent;
import co.edu.unicauca.review.application.event.ReviewCompletedEvent;
import co.edu.unicauca.review.domain.exception.ReviewConflictException;
import co.edu.unicauca.review.domain.model.ReviewAssignment;
import co.edu.unicauca.review.domain.model.ReviewStatus;
import co.edu.unicauca.review.domain.repository.ReviewAssignmentRepository;
import co.edu.unicauca.review.application.port.ReviewEventPort;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewApplicationServiceQueriesTest {

    @Mock
    private ReviewAssignmentRepository repository;

    @Mock
    private ReviewEventPort eventPublisher;

    @InjectMocks
    private ReviewApplicationService service;

    @Test
    void shouldListAssignmentsOfReviewer() {
        ReviewAssignment assignment = new ReviewAssignment(100L, 10L);
        when(repository.findByReviewerId(20L))
                .thenReturn(List.of(assignment));

        assertEquals(List.of(assignment),
                service.findAssignmentsByReviewer(20L));
    }

    @Test
    void shouldReturnEmptyListWhenReviewerHasNoAssignments() {
        when(repository.findByReviewerId(20L)).thenReturn(List.of());

        assertTrue(service.findAssignmentsByReviewer(20L).isEmpty());
    }

    @Test
    void shouldListQuestionsWaitingForReviewers() {
        ReviewAssignment assignment = new ReviewAssignment(100L, 10L);
        when(repository.findWithoutReviewers())
                .thenReturn(List.of(assignment));

        assertEquals(1, service.findPendingAssignments().size());
    }

    @Test
    void shouldRejectAssigningReviewersToQuestionAlreadyInReview() {
        ReviewAssignment assignment = new ReviewAssignment(100L, 10L);
        assignment.assignReviewer(20L);
        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.of(assignment));

        assertThrows(ReviewConflictException.class,
                () -> service.assignReviewers(100L, 10L, List.of(30L)));

        verify(repository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldRejectAssigningReviewersToQuestionNotPendingReview() {
        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.empty());

        assertThrows(ReviewConflictException.class,
                () -> service.assignReviewers(100L, 10L, List.of(20L)));

        verify(repository, never()).save(any());
        verifyNoInteractions(eventPublisher);
    }

    @Test
    void shouldRejectMoreThanThreeReviewers() {
        assertThrows(IllegalArgumentException.class,
                () -> service.assignReviewers(
                        100L, 10L, List.of(1L, 2L, 3L, 4L)));
    }

    @Test
    void shouldRejectAuthorAsReviewer() {
        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.of(new ReviewAssignment(100L, 10L)));

        assertThrows(IllegalArgumentException.class,
                () -> service.assignReviewers(100L, 10L, List.of(10L)));
    }

    @Test
    void shouldPublishAssignedEventWithReviewers() {
        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.of(new ReviewAssignment(100L, 10L)));
        when(repository.save(any(ReviewAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.assignReviewers(100L, 10L, List.of(20L, 30L));

        ArgumentCaptor<ReviewAssignedEvent> captor =
                ArgumentCaptor.forClass(ReviewAssignedEvent.class);
        verify(eventPublisher).publishReviewAssigned(captor.capture());

        assertEquals(List.of(20L, 30L), captor.getValue().getReviewerIds());
    }

    @Test
    void shouldIncludeRejectionObservationsInCompletedEvent() {
        ReviewAssignment assignment = new ReviewAssignment(100L, 10L);
        assignment.assignReviewer(20L);
        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.of(assignment));
        when(repository.save(any(ReviewAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.rejectReview(100L, 20L, "Falta justificacion");

        ArgumentCaptor<ReviewCompletedEvent> captor =
                ArgumentCaptor.forClass(ReviewCompletedEvent.class);
        verify(eventPublisher).publishReviewCompleted(captor.capture());

        assertEquals(ReviewStatus.REJECTED, captor.getValue().getResult());
        assertEquals(List.of("Falta justificacion"),
                captor.getValue().getObservations());
    }
}
