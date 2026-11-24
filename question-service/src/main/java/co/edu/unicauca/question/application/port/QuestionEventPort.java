package co.edu.unicauca.question.application.port;

import co.edu.unicauca.question.application.event.QuestionSubmittedEvent;

/**
 * Output port: how the application layer announces question events.
 * Infrastructure (RabbitMQ) provides the implementation.
 */
public interface QuestionEventPort {

    void publishQuestionSubmitted(QuestionSubmittedEvent event);
}
