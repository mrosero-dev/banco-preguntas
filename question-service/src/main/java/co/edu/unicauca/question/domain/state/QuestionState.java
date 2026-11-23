package co.edu.unicauca.question.domain.state;

import co.edu.unicauca.question.domain.model.QuestionStatus;

/**
 * Defines the operations available for each question state.
 */
public interface QuestionState {

    QuestionState submitForReview();

    QuestionState startReview();

    QuestionState approve();

    QuestionState reject();

    QuestionState returnToDraft();

    QuestionStatus getStatus();
}