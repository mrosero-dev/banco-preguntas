package co.edu.unicauca.question.domain.state;

import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;
import co.edu.unicauca.question.domain.model.QuestionStatus;

/**
 * Provides default behavior for unsupported question state transitions.
 */
public abstract class AbstractQuestionState implements QuestionState {

    protected void rejectTransition(String operation) {
        throw new QuestionStateConflictException(
            "Operation '" + operation
            + "' is not allowed in the current question state"
        );
    }

    @Override
    public QuestionState submitForReview() {
        rejectTransition("submitForReview");
        return this;
    }

    @Override
    public QuestionState startReview() {
        rejectTransition("startReview");
        return this;
    }

    @Override
    public QuestionState approve() {
        rejectTransition("approve");
        return this;
    }

    @Override
    public QuestionState reject() {
        rejectTransition("reject");
        return this;
    }

    @Override
    public QuestionState returnToDraft() {
        rejectTransition("returnToDraft");
        return this;
    }

    @Override
    public QuestionStatus getStatus() {
        throw new UnsupportedOperationException(
                "Each question state must define its status"
        );
    }
}