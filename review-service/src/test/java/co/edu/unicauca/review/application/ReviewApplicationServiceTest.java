package co.edu.unicauca.review.application;

import co.edu.unicauca.review.domain.model.ReviewAssignment;
import co.edu.unicauca.review.domain.model.ReviewStatus;
import co.edu.unicauca.review.domain.repository.ReviewAssignmentRepository;
import co.edu.unicauca.review.domain.exception.ReviewAssignmentNotFoundException;
import co.edu.unicauca.review.application.port.ReviewEventPort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewApplicationServiceTest {

    @Mock
    private ReviewAssignmentRepository repository;

    @Mock
    private ReviewEventPort eventPublisher;

    @InjectMocks
    private ReviewApplicationService service;

    @Test
    void shouldAssignReviewersSuccessfully() {

        // Arrange: the question was already sent to review (pending)
        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.of(new ReviewAssignment(100L, 10L)));
        when(repository.save(any(ReviewAssignment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ReviewAssignment result = service.assignReviewers(
                100L,
                10L,
                List.of(20L, 30L)
        );

        // Assert
        assertEquals(100L, result.getQuestionId());
        assertEquals(10L, result.getAuthorId());
        assertEquals(2, result.getReviews().size());

        verify(repository).save(any(ReviewAssignment.class));
    }

    @Test
    void shouldApproveReviewSuccessfully() {

        // Arrange
        ReviewAssignment assignment =
                new ReviewAssignment(100L, 10L);

        assignment.assignReviewer(20L);

        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.of(assignment));

        when(repository.save(assignment))
                .thenReturn(assignment);

        // Act
        ReviewAssignment result =
                service.approveReview(100L, 20L);

        // Assert
        assertEquals(
                ReviewStatus.APPROVED,
                result.getReviews().get(0).getStatus()
        );

        verify(repository).save(assignment);
    }

    @Test
    void shouldNotPublishEventWhenReviewsArePending() {

    // Arrange
        ReviewAssignment assignment = new ReviewAssignment(100L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);

        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.of(assignment));

        when(repository.save(assignment))
                .thenReturn(assignment);

        // Act
        service.approveReview(100L, 20L);

        // Assert
        verify(eventPublisher, never())
                .publishReviewCompleted(any());

        assertEquals(
                ReviewStatus.PENDING,
                assignment.getReviews().get(1).getStatus()
        );
    }

    @Test
    void shouldPublishApprovedEventWhenAllReviewsAreCompleted() {

        // Arrange
        ReviewAssignment assignment =
                new ReviewAssignment(100L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);

        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.of(assignment));

        when(repository.save(assignment))
                .thenReturn(assignment);

        // Act
        service.approveReview(100L, 20L);
        service.approveReview(100L, 30L);

        // Assert
        verify(eventPublisher, times(1))
                .publishReviewCompleted(any());

        assertEquals(
                ReviewStatus.APPROVED,
                assignment.getConsolidatedStatus()
        );
}

    @Test
    void shouldRejectReviewSuccessfully() {

        // Arrange
        ReviewAssignment assignment =
                new ReviewAssignment(100L, 10L);

        assignment.assignReviewer(20L);

        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.of(assignment));

        when(repository.save(assignment))
                .thenReturn(assignment);

        // Act
        ReviewAssignment result = service.rejectReview(
                100L,
                20L,
                "The justification is incomplete"
        );

        // Assert
        assertEquals(
                ReviewStatus.REJECTED,
                result.getReviews().get(0).getStatus()
        );

        assertEquals(
                "The justification is incomplete",
                result.getReviews().get(0).getObservations()
        );

        verify(repository).save(assignment);
    }

    @Test
    void shouldReturnConsolidatedStatus() {

        // Arrange
        ReviewAssignment assignment =
                new ReviewAssignment(100L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);

        assignment.getReviews().get(0).approve();
        assignment.getReviews().get(1).approve();

        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.of(assignment));

        // Act
        ReviewStatus result =
                service.getConsolidatedStatus(100L);

        // Assert
        assertEquals(ReviewStatus.APPROVED, result);
    }

    @Test
    void shouldThrowExceptionWhenAssignmentDoesNotExist() {

        // Arrange
        when(repository.findByQuestionId(999L))
                .thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(
                ReviewAssignmentNotFoundException.class,
                () -> service.getAssignment(999L)
        );
    }

    @Test
    void shouldPublishRejectedEventWhenAnyReviewerRejects() {

        // Arrange
        ReviewAssignment assignment =
                new ReviewAssignment(100L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);

        when(repository.findByQuestionId(100L))
                .thenReturn(Optional.of(assignment));

        when(repository.save(assignment))
                .thenReturn(assignment);

        // Act
        service.approveReview(100L, 20L);

        service.rejectReview(
                100L,
                30L,
                "The justification is incomplete"
        );

        // Assert
        org.mockito.ArgumentCaptor<co.edu.unicauca.review.application.event.ReviewCompletedEvent>
                eventCaptor =
                org.mockito.ArgumentCaptor.forClass(
                        co.edu.unicauca.review.application.event.ReviewCompletedEvent.class
                );

        verify(eventPublisher, times(1))
                .publishReviewCompleted(eventCaptor.capture());

        assertEquals(
                ReviewStatus.REJECTED,
                eventCaptor.getValue().getResult()
        );

        assertEquals(100L, eventCaptor.getValue().getQuestionId());
        assertEquals(10L, eventCaptor.getValue().getAuthorId());
    }
}