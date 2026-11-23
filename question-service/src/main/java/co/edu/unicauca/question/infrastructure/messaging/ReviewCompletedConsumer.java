package co.edu.unicauca.question.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import co.edu.unicauca.question.application.QuestionService;
import co.edu.unicauca.question.application.event.ReviewCompletedEvent;
import co.edu.unicauca.question.config.QuestionRabbitMQConfig;

@Component
public class ReviewCompletedConsumer {

    private final QuestionService questionService;

    public ReviewCompletedConsumer(QuestionService questionService) {
        this.questionService = questionService;
    }

    @RabbitListener(
            queues = QuestionRabbitMQConfig.REVIEW_COMPLETED_QUESTION_QUEUE
    )
    public void receiveReviewCompletedEvent(
            ReviewCompletedEvent event) {

        questionService.completeReview(
                event.getQuestionId(),
                event.getAuthorId(),
                event.getResult()
        );

        System.out.println(
                "Question review completed: "
                        + "Question ID = " + event.getQuestionId()
                        + ", Result = " + event.getResult()
        );
    }
}