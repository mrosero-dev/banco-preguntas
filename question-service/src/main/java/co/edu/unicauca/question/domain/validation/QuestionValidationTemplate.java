
package co.edu.unicauca.question.domain.validation;

import co.edu.unicauca.question.domain.exception.InvalidQuestionException;
import co.edu.unicauca.question.domain.model.Question;

/**
 * Defines the general algorithm for validating a question.
 * Subclasses provide the validation rules for each question type.
 */
public abstract class QuestionValidationTemplate {

    /**
     * Executes the question validation algorithm.
     *
     * @param question question to validate
     */
    public final void validate(Question question) {
        validateQuestionExists(question);
        validateCommonRules(question);
        validateSpecificRules(question);
    }

    /**
     * Checks that the question exists.
     *
     * @param question question to validate
     */
    private void validateQuestionExists(Question question) {
        if (question == null) {
            throw new IllegalArgumentException(
                    "Question cannot be null"
            );
        }
    }

    /**
     * Validates rules shared by all question types.
     *
     * @param question question to validate
     */
    protected void validateCommonRules(Question question) {

        if (question.getAuthorId() == null) {
            throw new InvalidQuestionException(
                    "Question must have an author"
            );
        }

        if (isBlank(question.getStatement())) {
            throw new InvalidQuestionException(
                    "Question statement cannot be empty"
            );
        }

        if (isBlank(question.getContext())) {
            throw new InvalidQuestionException(
                    "Question context cannot be empty"
            );
        }

        if (isBlank(question.getDifficulty())) {
            throw new InvalidQuestionException(
                    "Question difficulty cannot be empty"
            );
        }

        if (isBlank(question.getCategory())) {
            throw new InvalidQuestionException(
                    "Question category cannot be empty"
            );
        }

        if (isBlank(question.getTopic())) {
            throw new InvalidQuestionException(
                    "Question topic cannot be empty"
            );
        }

        if (isBlank(question.getSubtopic())) {
            throw new InvalidQuestionException(
                    "Question subtopic cannot be empty"
            );
        }

        if (isBlank(question.getCompetence())) {
            throw new InvalidQuestionException(
                    "Question competence cannot be empty"
            );
        }

        if (isBlank(question.getJustification())) {
            throw new InvalidQuestionException(
                    "Question justification cannot be empty"
            );
        }

        if (isBlank(question.getSource())) {
            throw new InvalidQuestionException(
                    "Question source cannot be empty"
            );
        }
    }

    /**
     * Checks whether a text value is null, empty or blank.
     *
     * @param value text to validate
     * @return true if the value is blank
     */
    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Validates rules specific to a question type.
     *
     * @param question question to validate
     */
    protected abstract void validateSpecificRules(Question question);
}
