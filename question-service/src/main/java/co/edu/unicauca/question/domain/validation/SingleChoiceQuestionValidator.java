package co.edu.unicauca.question.domain.validation;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import co.edu.unicauca.question.domain.exception.InvalidQuestionException;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;

/**
 * Validates single-choice questions.
 */
public class SingleChoiceQuestionValidator
        extends QuestionValidationTemplate {

    @Override
    protected void validateSpecificRules(Question question) {
        question.validateOptions();
        validateDuplicateOptions(question);
        validateProhibitedOptions(question);
    }

    private void validateDuplicateOptions(Question question) {
        Set<String> uniqueOptions = new HashSet<>();

        for (QuestionOption option : question.getOptions()) {
            String normalizedText = option.getText()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            if (!uniqueOptions.add(normalizedText)) {
                throw new InvalidQuestionException(
                        "Question cannot contain duplicate options"
                );
            }
        }
    }

    private void validateProhibitedOptions(Question question) {
        for (QuestionOption option : question.getOptions()) {
            String normalizedText = option.getText()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            if (normalizedText.equals("todas las anteriores")
                    || normalizedText.equals("ninguna de las anteriores")
                    || normalizedText.equals("todas las opciones anteriores")
                    || normalizedText.equals("ninguna de las opciones anteriores")) {

                throw new InvalidQuestionException(
                        "Question cannot contain prohibited options"
                );
            }
        }
    }
}