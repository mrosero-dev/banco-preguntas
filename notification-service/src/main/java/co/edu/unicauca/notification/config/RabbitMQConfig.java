package co.edu.unicauca.notification.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String REVIEW_COMPLETED_QUEUE =
            "question.review.completed";

    public static final String REVIEW_ASSIGNED_NOTIFICATION_QUEUE =
            "question.review.assigned.notification";

    public static final String QUESTION_RESUBMITTED_QUEUE =
            "question.resubmitted.notification";

    public static final String QUESTION_SUBMITTED_NOTIFICATION_QUEUE =
            "question.submitted.notification";

    @Bean
    public Queue questionSubmittedNotificationQueue() {
        return QueueBuilder
                .durable(QUESTION_SUBMITTED_NOTIFICATION_QUEUE)
                .build();
    }

    @Bean
    public Queue reviewCompletedQueue() {
        return QueueBuilder
                .durable(REVIEW_COMPLETED_QUEUE)
                .build();
    }

    @Bean
    public Queue reviewAssignedNotificationQueue() {
        return QueueBuilder
                .durable(REVIEW_ASSIGNED_NOTIFICATION_QUEUE)
                .build();
    }

    @Bean
    public Queue questionResubmittedQueue() {
        return QueueBuilder
                .durable(QUESTION_RESUBMITTED_QUEUE)
                .build();
    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter messageConverter) {

        RabbitTemplate rabbitTemplate =
                new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(messageConverter);

        return rabbitTemplate;
    }
}