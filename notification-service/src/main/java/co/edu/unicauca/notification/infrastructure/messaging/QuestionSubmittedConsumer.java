package co.edu.unicauca.notification.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import co.edu.unicauca.notification.application.NotificationService;
import co.edu.unicauca.notification.application.event.QuestionSubmittedEvent;
import co.edu.unicauca.notification.config.RabbitMQConfig;

/**
 * Consumes the event published when a question is sent to review for the
 * first time, so the administrators are told about it.
 */
@Component
public class QuestionSubmittedConsumer {

    private final NotificationService notificationService;

    public QuestionSubmittedConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUESTION_SUBMITTED_NOTIFICATION_QUEUE)
    public void receiveQuestionSubmitted(QuestionSubmittedEvent event) {

        notificationService.notifyQuestionSubmitted(event);
    }
}
