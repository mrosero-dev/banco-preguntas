package co.edu.unicauca.review.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewAssignmentNewRoundTest {

    @Test
    void shouldPreservePreviousReviewsWhenRoundIsCompleted() {
        ReviewAssignment assignment = new ReviewAssignment(100L, 10L);
        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);
        assignment.getReviews().get(0).reject("Falta fuente");
        assignment.getReviews().get(1).approve();

        assertTrue(assignment.startNewRound());

        assertTrue(assignment.getReviews().isEmpty());
        assertEquals(2, assignment.getHistory().size());
        assertEquals(1, assignment.getHistory().get(0).getRoundNumber());
        assertEquals(2, assignment.getCurrentRound());
        assertEquals(ReviewStatus.PENDING, assignment.getConsolidatedStatus());
    }

    @Test
    void shouldAllowAssigningTheSameReviewersAgain() {
        ReviewAssignment assignment = new ReviewAssignment(100L, 10L);
        assignment.assignReviewer(20L);
        assignment.getReviews().get(0).approve();
        assignment.startNewRound();

        assignment.assignReviewer(20L);

        assertEquals(1, assignment.getReviews().size());
        assertEquals(ReviewStatus.PENDING,
                assignment.getReviews().get(0).getStatus());
    }

    @Test
    void shouldKeepReviewsWhenSomeReviewerHasNotEvaluated() {
        ReviewAssignment assignment = new ReviewAssignment(100L, 10L);
        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);
        assignment.getReviews().get(0).approve();

        assertFalse(assignment.startNewRound());

        assertEquals(2, assignment.getReviews().size());
    }

    @Test
    void shouldDoNothingWhenThereAreNoReviews() {
        assertFalse(new ReviewAssignment(100L, 10L).startNewRound());
    }
}
