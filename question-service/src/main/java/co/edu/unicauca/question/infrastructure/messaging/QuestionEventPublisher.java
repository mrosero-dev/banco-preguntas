package co.edu.unicauca.question.infrastructure.messaging;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import co.edu.unicauca.question.application.event.QuestionSubmittedEvent;
import co.edu.unicauca.question.application.port.QuestionEventPort;
import co.edu.unicauca.question.config.QuestionRabbitMQConfig;

@Component
public class QuestionEventPublisher implements QuestionEventPort {

    private final RabbitTemplate rabbitTemplate;

    public QuestionEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public void publishQuestionSubmitted(QuestionSubmittedEvent event) {
        rabbitTemplate.convertAndSend(
                QuestionRabbitMQConfig.QUESTION_SUBMITTED_QUEUE,
                event
        );
    }
}