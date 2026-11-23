package co.edu.unicauca.notification.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import co.edu.unicauca.notification.application.NotificationService;
import co.edu.unicauca.notification.application.event.ReviewAssignedEvent;
import co.edu.unicauca.notification.config.RabbitMQConfig;

/**
 * Consumes the reviewers-assigned events published by Review Service.
 */
@Component
public class ReviewAssignedEventConsumer {

    private final NotificationService notificationService;

    public ReviewAssignedEventConsumer(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @RabbitListener(
            queues = RabbitMQConfig.REVIEW_ASSIGNED_NOTIFICATION_QUEUE
    )
    public void receiveReviewAssignedEvent(ReviewAssignedEvent event) {

        notificationService.notifyReviewersAssigned(event);
    }
}
