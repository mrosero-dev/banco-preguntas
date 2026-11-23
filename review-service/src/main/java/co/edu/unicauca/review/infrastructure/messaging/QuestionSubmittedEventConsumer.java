package co.edu.unicauca.review.infrastructure.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import co.edu.unicauca.review.application.ReviewApplicationService;
import co.edu.unicauca.review.application.event.QuestionSubmittedEvent;

@Component
public class QuestionSubmittedEventConsumer {

    private final ReviewApplicationService reviewApplicationService;

    public QuestionSubmittedEventConsumer(
            ReviewApplicationService reviewApplicationService) {

        this.reviewApplicationService = reviewApplicationService;
    }

    @RabbitListener(queues = "question.submitted")
    public void receiveQuestionSubmitted(
            QuestionSubmittedEvent event) {

        reviewApplicationService.registerPendingQuestion(
                event.getQuestionId(),
                event.getAuthorId()
        );

        System.out.println(
                "Question registered for reviewer assignment: "
                        + event.getQuestionId()
        );
    }
}