
package co.edu.unicauca.question.domain.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import co.edu.unicauca.question.domain.exception.InvalidQuestionException;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;

class SingleChoiceQuestionValidatorTest {

    private SingleChoiceQuestionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SingleChoiceQuestionValidator();
    }

    private Question createValidQuestion() {
        Question question = new Question(
                1L,
                "What is software engineering?",
                "Context",
                "Medium",
                "Computing",
                "Software Engineering",
                "Requirements",
                "Problem solving",
                "Justification",
                "Source"
        );

        question.addOption(new QuestionOption("Option A", true));
        question.addOption(new QuestionOption("Option B", false));
        question.addOption(new QuestionOption("Option C", false));
        question.addOption(new QuestionOption("Option D", false));
        question.addOption(new QuestionOption("Option E", false));

        return question;
    }

    @Test
    void shouldValidateQuestionWithFiveOptionsAndOneCorrectAnswer() {
        Question question = createValidQuestion();

        assertDoesNotThrow(() -> validator.validate(question));
    }

    @Test
    void shouldRejectNullQuestion() {
        assertThrows(
                IllegalArgumentException.class,
                () -> validator.validate(null)
        );
    }

    @Test
    void shouldRejectQuestionWithIncorrectNumberOfOptions() {
        Question question = new Question(
                1L,
                "What is software engineering?",
                "Context",
                "Medium",
                "Computing",
                "Software Engineering",
                "Requirements",
                "Problem solving",
                "Justification",
                "Source"
        );

        question.addOption(new QuestionOption("Option A", true));
        question.addOption(new QuestionOption("Option B", false));

        assertThrows(
                InvalidQuestionException.class,
                () -> validator.validate(question)
        );
    }

    @Test
    void shouldRejectQuestionWithDuplicateOptions() {
        Question question = createValidQuestion();

        question.updateOptionText(
                question.getOptions().get(1),
                "Option A"
        );

        assertThrows(
                InvalidQuestionException.class,
                () -> validator.validate(question)
        );
    }

    @Test
    void shouldRejectOptionsWithDifferentCaseAndSpaces() {
        Question question = createValidQuestion();

        question.updateOptionText(
                question.getOptions().get(1),
                " option a "
        );

        assertThrows(
                InvalidQuestionException.class,
                () -> validator.validate(question)
        );
    }

    @Test
    void shouldRejectAllPreviousOptionsExpression() {
        Question question = createValidQuestion();

        question.updateOptionText(
                question.getOptions().get(1),
                "Todas las anteriores"
        );

        assertThrows(
                InvalidQuestionException.class,
                () -> validator.validate(question)
        );
    }

    @Test
    void shouldRejectMoreThanOneDirectQuestion() {
        Question question = new Question(
                1L,
                "What is software engineering? And what is a requirement?",
                "Context",
                "Medium",
                "Computing",
                "Software Engineering",
                "Requirements",
                "Problem solving",
                "Justification",
                "Source"
        );
        question.addOption(new QuestionOption("Option A", true));
        question.addOption(new QuestionOption("Option B", false));
        question.addOption(new QuestionOption("Option C", false));
        question.addOption(new QuestionOption("Option D", false));
        question.addOption(new QuestionOption("Option E", false));

        assertThrows(
                InvalidQuestionException.class,
                () -> validator.validate(question)
        );
    }

    @Test
    void shouldRejectProhibitedExpressionWithPunctuationAndAccents() {
        Question question = createValidQuestion();

        question.updateOptionText(
                question.getOptions().get(1),
                "Todas las anteriores son correctas."
        );

        assertThrows(
                InvalidQuestionException.class,
                () -> validator.validate(question)
        );
    }

    @Test
    void shouldRejectNoneOfPreviousOptionsExpression() {
        Question question = createValidQuestion();

        question.updateOptionText(
                question.getOptions().get(1),
                "Ninguna de las opciones anteriores"
        );

        assertThrows(
                InvalidQuestionException.class,
                () -> validator.validate(question)
        );
    }
}
