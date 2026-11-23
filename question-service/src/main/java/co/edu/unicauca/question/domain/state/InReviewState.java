package co.edu.unicauca.question.domain.state;

import co.edu.unicauca.question.domain.model.QuestionStatus;

/**
 * Represents a question currently under review.
 */
public class InReviewState extends AbstractQuestionState {

    @Override
    public QuestionState approve() {
        return new ApprovedState();
    }

    @Override
    public QuestionState reject() {
        return new RejectedState();
    }

    @Override
    public QuestionStatus getStatus() {
        return QuestionStatus.IN_REVIEW;
    }
}