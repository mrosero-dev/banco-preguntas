package co.edu.unicauca.question.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import co.edu.unicauca.question.application.QuestionService;
import co.edu.unicauca.question.application.event.ReviewAssignedEvent;
import co.edu.unicauca.question.config.QuestionRabbitMQConfig;

@Component
public class ReviewAssignedConsumer {

    private final QuestionService questionService;

    public ReviewAssignedConsumer(QuestionService questionService) {
        this.questionService = questionService;
    }

    @RabbitListener(
            queues = QuestionRabbitMQConfig.REVIEW_ASSIGNED_QUEUE
    )
    public void receiveReviewAssignedEvent(
            ReviewAssignedEvent event) {

        questionService.startReview(
                event.getQuestionId(),
                event.getAuthorId()
        );

        System.out.println(
                "Question review started: "
                        + event.getQuestionId()
        );
    }
}