package co.edu.unicauca.question.domain.state;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;
import co.edu.unicauca.question.domain.model.QuestionStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests of PendingReviewState in isolation (no Question involved).
 */
class PendingReviewStateTest {

    private final PendingReviewState state = new PendingReviewState();

    @Test
    void shouldReportStatusPendingReview() {
        assertEquals(QuestionStatus.PENDING_REVIEW, state.getStatus());
    }

    @Test
    void startReviewShouldMoveToInReviewState() {
        QuestionState next = state.startReview();

        assertInstanceOf(InReviewState.class, next);
    }

    @Test
    void submitForReviewShouldBeRejected() {
        assertThrows(QuestionStateConflictException.class, state::submitForReview);
    }

    @Test
    void approveShouldBeRejected() {
        assertThrows(QuestionStateConflictException.class, state::approve);
    }

    @Test
    void rejectShouldBeRejected() {
        assertThrows(QuestionStateConflictException.class, state::reject);
    }

    @Test
    void returnToDraftShouldBeRejected() {
        assertThrows(QuestionStateConflictException.class, state::returnToDraft);
    }
}
