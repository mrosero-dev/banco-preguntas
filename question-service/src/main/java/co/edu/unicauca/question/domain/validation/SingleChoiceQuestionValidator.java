package co.edu.unicauca.question.domain.validation;

import java.text.Normalizer;
import java.util.HashSet;
import java.util.List;
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

    private static final List<String> PROHIBITED_EXPRESSIONS = List.of(
            "todas las anteriores",
            "ninguna de las anteriores",
            "todas las opciones anteriores",
            "ninguna de las opciones anteriores");

    @Override
    protected void validateSpecificRules(Question question) {
        validateSingleDirectQuestion(question);
        question.validateOptions();
        validateDuplicateOptions(question);
        validateProhibitedOptions(question);
    }

    /** HU-1.1 scenario 4: only one direct question (one question mark). */
    private void validateSingleDirectQuestion(Question question) {
        long questionMarks = question.getStatement().chars()
                .filter(character -> character == '?')
                .count();

        if (questionMarks > 1) {
            throw new InvalidQuestionException(
                    "Debe existir una única pregunta directa"
            );
        }
    }

    private void validateDuplicateOptions(Question question) {
        Set<String> uniqueOptions = new HashSet<>();

        for (QuestionOption option : question.getOptions()) {
            String normalizedText = option.getText()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            if (!uniqueOptions.add(normalizedText)) {
                throw new InvalidQuestionException(
                        "Distractor repetido, debe corregirse: \""
                                + option.getText() + "\""
                );
            }
        }
    }

    private void validateProhibitedOptions(Question question) {
        for (QuestionOption option : question.getOptions()) {
            String normalizedText = Normalizer
                    .normalize(option.getText(), Normalizer.Form.NFD)
                    .replaceAll("\\p{M}", "")
                    .toLowerCase(Locale.ROOT);

            if (PROHIBITED_EXPRESSIONS.stream()
                    .anyMatch(normalizedText::contains)) {

                throw new InvalidQuestionException(
                        "Las expresiones «Todas las anteriores» y «Ninguna "
                                + "de las anteriores» no están permitidas: \""
                                + option.getText() + "\""
                );
            }
        }
    }
}
