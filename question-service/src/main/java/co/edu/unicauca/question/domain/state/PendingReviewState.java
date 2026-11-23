package co.edu.unicauca.question.domain.state;

import co.edu.unicauca.question.domain.model.QuestionStatus;

/**
 * Represents a question waiting for review.
 */
public class PendingReviewState extends AbstractQuestionState {

    @Override
    public QuestionState startReview() {
        return new InReviewState();
    }

    @Override
    public QuestionStatus getStatus() {
        return QuestionStatus.PENDING_REVIEW;
    }
}