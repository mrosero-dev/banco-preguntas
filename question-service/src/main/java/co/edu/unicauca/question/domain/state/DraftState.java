package co.edu.unicauca.question.domain.state;

import co.edu.unicauca.question.domain.model.QuestionStatus;

/**
 * Represents a question in draft status.
 */
public class DraftState extends AbstractQuestionState {

    @Override
    public QuestionState submitForReview() {
        return new PendingReviewState();
    }

    @Override
    public QuestionStatus getStatus() {
        return QuestionStatus.DRAFT;
    }
}