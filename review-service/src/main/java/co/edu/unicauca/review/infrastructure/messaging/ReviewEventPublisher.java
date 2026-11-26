package co.edu.unicauca.review.infrastructure.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import co.edu.unicauca.review.application.event.QuestionResubmittedEvent;
import co.edu.unicauca.review.application.event.QuestionSubmittedEvent;

import co.edu.unicauca.review.application.event.ReviewAssignedEvent;
import co.edu.unicauca.review.application.event.ReviewCompletedEvent;
import co.edu.unicauca.review.application.port.ReviewEventPort;
import co.edu.unicauca.review.config.ReviewRabbitMQConfig;

@Component
public class ReviewEventPublisher implements ReviewEventPort {

    private final RabbitTemplate rabbitTemplate;

    public ReviewEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishReviewAssigned(ReviewAssignedEvent event) {

        // Notify Question Service (moves the question to EN REVISION).
        rabbitTemplate.convertAndSend(
                ReviewRabbitMQConfig.REVIEW_ASSIGNED_QUEUE,
                event
        );

        // Notify Notification Service (emails the reviewers).
        rabbitTemplate.convertAndSend(
                ReviewRabbitMQConfig.REVIEW_ASSIGNED_NOTIFICATION_QUEUE,
                event
        );
    }

    @Override
    public void publishReviewCompleted(ReviewCompletedEvent event) {

    // Notify Notification Service.
        rabbitTemplate.convertAndSend(
                ReviewRabbitMQConfig.REVIEW_COMPLETED_QUEUE,
                event
        );

        // Notify Question Service.
        rabbitTemplate.convertAndSend(
                ReviewRabbitMQConfig.REVIEW_COMPLETED_QUESTION_QUEUE,
                event
        );
    }

    @Override
    public void publishQuestionSubmitted(QuestionSubmittedEvent event) {

        // Notify Notification Service (administrators).
        rabbitTemplate.convertAndSend(
                ReviewRabbitMQConfig.QUESTION_SUBMITTED_NOTIFICATION_QUEUE,
                event
        );
    }

    @Override
    public void publishQuestionResubmitted(QuestionResubmittedEvent event) {

        // Notify Notification Service (administrators and previous reviewers).
        rabbitTemplate.convertAndSend(
                ReviewRabbitMQConfig.QUESTION_RESUBMITTED_QUEUE,
                event
        );
    }
}
