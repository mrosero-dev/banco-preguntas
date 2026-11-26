package co.edu.unicauca.question.domain.state;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;
import co.edu.unicauca.question.domain.model.QuestionStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests of RejectedState in isolation (no Question involved).
 */
class RejectedStateTest {

    private final RejectedState state = new RejectedState();

    @Test
    void shouldReportStatusRejected() {
        assertEquals(QuestionStatus.REJECTED, state.getStatus());
    }

    @Test
    void returnToDraftShouldMoveToDraftState() {
        QuestionState next = state.returnToDraft();

        assertInstanceOf(DraftState.class, next);
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
    void approveShouldBeRejected() {
        assertThrows(QuestionStateConflictException.class, state::approve);
    }

    @Test
    void rejectShouldBeRejected() {
        assertThrows(QuestionStateConflictException.class, state::reject);
    }
}
