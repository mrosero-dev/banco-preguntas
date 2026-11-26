package co.edu.unicauca.question.domain.state;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;
import co.edu.unicauca.question.domain.model.QuestionStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests of DraftState in isolation (no Question involved).
 */
class DraftStateTest {

    private final DraftState state = new DraftState();

    @Test
    void shouldReportStatusDraft() {
        assertEquals(QuestionStatus.DRAFT, state.getStatus());
    }

    @Test
    void submitForReviewShouldMoveToPendingReviewState() {
        QuestionState next = state.submitForReview();

        assertInstanceOf(PendingReviewState.class, next);
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

    @Test
    void returnToDraftShouldBeRejected() {
        assertThrows(QuestionStateConflictException.class, state::returnToDraft);
    }
}
