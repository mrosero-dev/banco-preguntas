package co.edu.unicauca.question.domain.state;

import co.edu.unicauca.question.domain.model.QuestionStatus;

/**
 * Represents a rejected question.
 */
public class RejectedState extends AbstractQuestionState {

    @Override
    public QuestionState returnToDraft() {
        return new DraftState();
    }

    @Override
    
    public QuestionStatus getStatus() {
        return QuestionStatus.REJECTED;
    }
}