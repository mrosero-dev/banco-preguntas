package co.edu.unicauca.question.domain.state;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;
import co.edu.unicauca.question.domain.model.QuestionStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests of InReviewState in isolation (no Question involved).
 */
class InReviewStateTest {

    private final InReviewState state = new InReviewState();

    @Test
    void shouldReportStatusInReview() {
        assertEquals(QuestionStatus.IN_REVIEW, state.getStatus());
    }

    @Test
    void approveShouldMoveToApprovedState() {
        QuestionState next = state.approve();

        assertInstanceOf(ApprovedState.class, next);
    }

    @Test
    void rejectShouldMoveToRejectedState() {
        QuestionState next = state.reject();

        assertInstanceOf(RejectedState.class, next);
    }

    @Test
    void submitForReviewShouldBeRejected() {
        assertThrows(QuestionStateConflictException.class, state::submitForReview);
    }

    @Test
    void startReviewShouldBeRejected() {
        assertThrows(QuestionStateConflictException.class, state::startReview);
    }

    @Test
    void returnToDraftShouldBeRejected() {
        assertThrows(QuestionStateConflictException.class, state::returnToDraft);
    }
}
