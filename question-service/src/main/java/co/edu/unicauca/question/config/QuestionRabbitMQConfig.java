package co.edu.unicauca.question.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuestionRabbitMQConfig {

    public static final String QUESTION_SUBMITTED_QUEUE =
            "question.submitted";

    public static final String REVIEW_ASSIGNED_QUEUE =
            "question.review.assigned";

    public static final String REVIEW_COMPLETED_QUESTION_QUEUE =
        "question.review.completed.question";

    @Bean
    public Queue questionSubmittedQueue() {
        return QueueBuilder
                .durable(QUESTION_SUBMITTED_QUEUE)
                .build();
    }

    @Bean
    public Queue reviewAssignedQueue() {
        return QueueBuilder
                .durable(REVIEW_ASSIGNED_QUEUE)
                .build();
    }

    @Bean
    public MessageConverter questionMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter questionMessageConverter) {

        RabbitTemplate rabbitTemplate =
                new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(questionMessageConverter);

        return rabbitTemplate;
    }

    @Bean
    public Queue reviewCompletedQuestionQueue() {
        return QueueBuilder
                .durable(REVIEW_COMPLETED_QUESTION_QUEUE)
                .build();
    }

    
}