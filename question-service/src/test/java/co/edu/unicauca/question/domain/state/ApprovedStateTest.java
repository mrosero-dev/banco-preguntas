package co.edu.unicauca.question.domain.state;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;
import co.edu.unicauca.question.domain.model.QuestionStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests of ApprovedState in isolation (no Question involved).
 */
class ApprovedStateTest {

    private final ApprovedState state = new ApprovedState();

    @Test
    void shouldReportStatusApproved() {
        assertEquals(QuestionStatus.APPROVED, state.getStatus());
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

    @Test
    void returnToDraftShouldBeRejected() {
        assertThrows(QuestionStateConflictException.class, state::returnToDraft);
    }
}
