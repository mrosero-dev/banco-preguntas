package co.edu.unicauca.review.application.port;

import co.edu.unicauca.review.application.event.QuestionResubmittedEvent;
import co.edu.unicauca.review.application.event.QuestionSubmittedEvent;
import co.edu.unicauca.review.application.event.ReviewAssignedEvent;
import co.edu.unicauca.review.application.event.ReviewCompletedEvent;

/**
 * Output port: how the application layer announces review events.
 * Infrastructure (RabbitMQ) provides the implementation.
 */
public interface ReviewEventPort {

    void publishReviewAssigned(ReviewAssignedEvent event);

    void publishReviewCompleted(ReviewCompletedEvent event);

    void publishQuestionResubmitted(QuestionResubmittedEvent event);

    /** A question reached review for the first time (notifies administrators). */
    void publishQuestionSubmitted(QuestionSubmittedEvent event);
}
