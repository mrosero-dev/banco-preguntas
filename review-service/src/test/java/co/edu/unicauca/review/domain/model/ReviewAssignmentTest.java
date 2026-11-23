
package co.edu.unicauca.review.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewAssignmentTest {

    @Test
    void shouldAssignReviewersSuccessfully() {

        ReviewAssignment assignment =
                new ReviewAssignment(1L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);

        assertEquals(2, assignment.getReviews().size());
    }

    @Test
    void shouldNotAllowMoreThanThreeReviewers() {

        ReviewAssignment assignment =
                new ReviewAssignment(1L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);
        assignment.assignReviewer(40L);

        assertThrows(
                IllegalStateException.class,
                () -> assignment.assignReviewer(50L)
        );
    }

    @Test
    void shouldNotAllowDuplicateReviewers() {

        ReviewAssignment assignment =
                new ReviewAssignment(1L, 10L);

        assignment.assignReviewer(20L);

        assertThrows(
                IllegalArgumentException.class,
                () -> assignment.assignReviewer(20L)
        );
    }

    @Test
    void shouldNotAllowAuthorToReviewOwnQuestion() {

        ReviewAssignment assignment =
                new ReviewAssignment(1L, 10L);

        assertThrows(
                IllegalArgumentException.class,
                () -> assignment.assignReviewer(10L)
        );
    }

    @Test
    void shouldRemainPendingWhenSomeReviewsArePending() {

        ReviewAssignment assignment =
                new ReviewAssignment(1L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);

        assignment.getReviews().get(0).approve();

        assertFalse(assignment.isCompleted());
        assertEquals(
                ReviewStatus.PENDING,
                assignment.getConsolidatedStatus()
        );
    }

    @Test
    void shouldApproveWhenAllReviewersApprove() {

        ReviewAssignment assignment =
                new ReviewAssignment(1L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);

        assignment.getReviews().forEach(Review::approve);

        assertTrue(assignment.isCompleted());
        assertEquals(
                ReviewStatus.APPROVED,
                assignment.getConsolidatedStatus()
        );
    }

    @Test
    void shouldRejectWhenAtLeastOneReviewerRejects() {

        ReviewAssignment assignment =
                new ReviewAssignment(1L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);

        assignment.getReviews().get(0).approve();
        assignment.getReviews().get(1).reject(
                "La pregunta necesita correcciones"
        );

        assertTrue(assignment.isCompleted());
        assertEquals(
                ReviewStatus.REJECTED,
                assignment.getConsolidatedStatus()
        );
    }

    @Test
    void shouldNotAllowNullReviewer() {

        ReviewAssignment assignment =
                new ReviewAssignment(1L, 10L);

        assertThrows(
                IllegalArgumentException.class,
                () -> assignment.assignReviewer(null)
        );
        }

        @Test
        void shouldNotBeCompletedWhenThereAreNoReviewers() {

        ReviewAssignment assignment =
                new ReviewAssignment(1L, 10L);

        assertFalse(assignment.isCompleted());

        assertEquals(
                ReviewStatus.PENDING,
                assignment.getConsolidatedStatus()
        );
    }
}
