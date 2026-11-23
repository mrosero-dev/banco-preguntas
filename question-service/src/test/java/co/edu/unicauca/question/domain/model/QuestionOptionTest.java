
package co.edu.unicauca.question.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionOptionTest {

    @Test
    void shouldCreateOptionWithValidText() {
        QuestionOption option = new QuestionOption("Option A", true);

        assertEquals("Option A", option.getText());
        assertTrue(option.isCorrect());
    }

    @Test
    void shouldNotCreateOptionWithNullText() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new QuestionOption(null, false)
        );
    }

    @Test
    void shouldNotCreateOptionWithEmptyText() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new QuestionOption("   ", false)
        );
    }

    @Test
    void shouldUpdateOptionText() {
        QuestionOption option = new QuestionOption("Old text", false);

        option.updateText("New text");

        assertEquals("New text", option.getText());
    }

    @Test
    void shouldNotUpdateOptionWithEmptyText() {
        QuestionOption option = new QuestionOption("Option A", false);

        assertThrows(
                IllegalArgumentException.class,
                () -> option.updateText("")
        );
    }

    @Test
    void shouldMarkOptionAsCorrect() {
        QuestionOption option = new QuestionOption("Option A", false);

        option.markAsCorrect();

        assertTrue(option.isCorrect());
    }

    @Test
    void shouldMarkOptionAsIncorrect() {
        QuestionOption option = new QuestionOption("Option A", true);

        option.markAsIncorrect();

        assertFalse(option.isCorrect());
    }
}
