
package co.edu.unicauca.question.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.question.domain.exception.InvalidQuestionException;
import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;

class QuestionEditingTest {

    private Question createQuestion() {
        Question question = new Question(
                10L,
                "Original statement",
                "Original context",
                "FACIL",
                "MATEMATICAS",
                "ARITMETICA",
                "SUMA",
                "RAZONAMIENTO",
                "Original justification",
                "Original source"
        );

        question.addOption(new QuestionOption("One", true));
        question.addOption(new QuestionOption("Two", false));
        question.addOption(new QuestionOption("Three", false));
        question.addOption(new QuestionOption("Four", false));
        question.addOption(new QuestionOption("Five", false));

        return question;
    }

    private List<QuestionOption> updatedOptions() {
        return List.of(
                new QuestionOption("Six", false),
                new QuestionOption("Seven", false),
                new QuestionOption("Eight", true),
                new QuestionOption("Nine", false),
                new QuestionOption("Ten", false)
        );
    }

    private void update(Question question, List<QuestionOption> options) {
        question.updateDetails(
                "Updated statement",
                "Updated context",
                "MEDIA",
                "MATEMATICAS",
                "ARITMETICA",
                "MULTIPLICACION",
                "RAZONAMIENTO",
                "Updated justification",
                "Updated source",
                options
        );
    }

    @Test
    void shouldUpdateQuestionWhileItIsDraft() {
        Question question = createQuestion();

        update(question, updatedOptions());

        assertEquals("Updated statement", question.getStatement());
        assertEquals("Updated context", question.getContext());
        assertEquals("MEDIA", question.getDifficulty());
        assertEquals("MULTIPLICACION", question.getSubtopic());
        assertEquals("Updated justification", question.getJustification());
        assertEquals("Updated source", question.getSource());
        assertEquals(10L, question.getAuthorId());
        assertEquals(5, question.getOptions().size());
        assertEquals("Six", question.getOptions().get(0).getText());

        assertEquals(
                1,
                question.getOptions().stream()
                        .filter(QuestionOption::isCorrect)
                        .count()
        );
    }

    @Test
    void shouldNotUpdateQuestionOutsideDraft() {
        Question question = createQuestion();
        question.submitForReview();

        assertThrows(
                QuestionStateConflictException.class,
                () -> update(question, updatedOptions())
        );
    }

    @Test
    void shouldRejectUpdateWithIncorrectNumberOfOptions() {
        Question question = createQuestion();

        List<QuestionOption> invalidOptions = List.of(
                new QuestionOption("One", true),
                new QuestionOption("Two", false)
        );

        assertThrows(
                InvalidQuestionException.class,
                () -> update(question, invalidOptions)
        );

        assertEquals("Original statement", question.getStatement());
    }

    @Test
    void shouldRejectUpdateWithoutExactlyOneCorrectAnswer() {
        Question question = createQuestion();

        List<QuestionOption> invalidOptions = List.of(
                new QuestionOption("One", false),
                new QuestionOption("Two", false),
                new QuestionOption("Three", false),
                new QuestionOption("Four", false),
                new QuestionOption("Five", false)
        );

        assertThrows(
                InvalidQuestionException.class,
                () -> update(question, invalidOptions)
        );

        assertEquals("Original statement", question.getStatement());
    }

    @Test
    void shouldRejectNullOptions() {
        Question question = createQuestion();

        assertThrows(
                IllegalArgumentException.class,
                () -> update(question, null)
        );
    }
}
