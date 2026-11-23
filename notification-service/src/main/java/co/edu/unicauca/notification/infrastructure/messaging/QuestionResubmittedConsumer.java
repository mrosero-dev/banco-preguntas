package co.edu.unicauca.notification.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import co.edu.unicauca.notification.application.NotificationService;
import co.edu.unicauca.notification.application.event.QuestionResubmittedEvent;
import co.edu.unicauca.notification.config.RabbitMQConfig;

/**
 * Consumes the events published when an improved version is sent to review.
 */
@Component
public class QuestionResubmittedConsumer {

    private final NotificationService notificationService;

    public QuestionResubmittedConsumer(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @RabbitListener(queues = RabbitMQConfig.QUESTION_RESUBMITTED_QUEUE)
    public void receiveQuestionResubmitted(QuestionResubmittedEvent event) {

        notificationService.notifyQuestionResubmitted(event);
    }
}
