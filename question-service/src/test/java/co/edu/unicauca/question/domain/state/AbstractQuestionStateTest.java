package co.edu.unicauca.question.domain.state;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;
import co.edu.unicauca.question.domain.model.QuestionStatus;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests of the default behavior shared by every state: any transition
 * that a concrete state does not override is a conflict.
 */
class AbstractQuestionStateTest {

    /** Concrete state that overrides nothing, so every default applies. */
    private static class NoTransitionsState extends AbstractQuestionState {

        @Override
        public QuestionStatus getStatus() {
            return QuestionStatus.DRAFT;
        }
    }

    private final QuestionState state = new NoTransitionsState();

    @Test
    void submitForReviewShouldBeRejectedByDefault() {
        assertThrows(QuestionStateConflictException.class, state::submitForReview);
    }

    @Test
    void startReviewShouldBeRejectedByDefault() {
        assertThrows(QuestionStateConflictException.class, state::startReview);
    }

    @Test
    void approveShouldBeRejectedByDefault() {
        assertThrows(QuestionStateConflictException.class, state::approve);
    }

    @Test
    void rejectShouldBeRejectedByDefault() {
        assertThrows(QuestionStateConflictException.class, state::reject);
    }

    @Test
    void returnToDraftShouldBeRejectedByDefault() {
        assertThrows(QuestionStateConflictException.class, state::returnToDraft);
    }

    @Test
    void conflictMessageShouldNameTheOperation() {
        QuestionStateConflictException error = assertThrows(
                QuestionStateConflictException.class, state::approve);

        assertTrue(error.getMessage().contains("approve"));
    }

    @Test
    void stateWithoutStatusShouldFailToReportIt() {
        QuestionState noStatus = new AbstractQuestionState() { };

        assertThrows(UnsupportedOperationException.class, noStatus::getStatus);
    }
}
