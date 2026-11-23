package co.edu.unicauca.question.domain.state;

import co.edu.unicauca.question.domain.model.QuestionStatus;

/**
 * Represents an approved question.
 */
public class ApprovedState extends AbstractQuestionState {

    @Override
    public QuestionStatus getStatus() {
        return QuestionStatus.APPROVED;
    }
}