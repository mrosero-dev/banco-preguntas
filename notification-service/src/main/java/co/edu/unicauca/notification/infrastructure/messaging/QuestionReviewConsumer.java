package co.edu.unicauca.notification.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import co.edu.unicauca.notification.application.NotificationService;
import co.edu.unicauca.notification.application.event.ReviewCompletedEvent;
import co.edu.unicauca.notification.config.RabbitMQConfig;

/**
 * Consumes question review events from RabbitMQ.
 */
@Component
public class QuestionReviewConsumer {

    private final NotificationService notificationService;

    public QuestionReviewConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(
            queues = RabbitMQConfig.REVIEW_COMPLETED_QUEUE
    )
    public void receiveReviewEvent(ReviewCompletedEvent event) {

        notificationService.notifyReviewCompleted(event);
    }
}
