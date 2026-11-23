
package co.edu.unicauca.question.domain.model;

import co.edu.unicauca.question.domain.exception.InvalidQuestionException;
import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionValidationTest {

    private Question createQuestion() {
        return new Question(
                1L,
                "What is software engineering?",
                "Context of the question",
                "MEDIUM",
                "Engineering",
                "Software",
                "Requirements",
                "Problem solving",
                "Because of the engineering principles",
                "Reference source"
        );
    }

    private Question createValidQuestion() {
        Question question = createQuestion();

        question.addOption(new QuestionOption("Option A", true));
        question.addOption(new QuestionOption("Option B", false));
        question.addOption(new QuestionOption("Option C", false));
        question.addOption(new QuestionOption("Option D", false));
        question.addOption(new QuestionOption("Option E", false));

        return question;
    }

    @Test
    void shouldNotSubmitQuestionWithoutOptions() {
        Question question = createQuestion();

        assertThrows(
                InvalidQuestionException.class,
                question::submitForReview
        );

        assertEquals(QuestionStatus.DRAFT, question.getStatus());
        
    }

    @Test
    void shouldNotSubmitQuestionWithFourOptions() {
        Question question = createQuestion();

        question.addOption(new QuestionOption("A", true));
        question.addOption(new QuestionOption("B", false));
        question.addOption(new QuestionOption("C", false));
        question.addOption(new QuestionOption("D", false));

        assertThrows(
                InvalidQuestionException.class,
                question::submitForReview
        );

        assertEquals(QuestionStatus.DRAFT, question.getStatus());
        assertEquals(QuestionStatus.DRAFT, question.getStatus());
    }

    @Test
    void shouldNotSubmitQuestionWithoutCorrectAnswer() {
        Question question = createQuestion();

        question.addOption(new QuestionOption("A", false));
        question.addOption(new QuestionOption("B", false));
        question.addOption(new QuestionOption("C", false));
        question.addOption(new QuestionOption("D", false));
        question.addOption(new QuestionOption("E", false));

        assertThrows(
                InvalidQuestionException.class,
                question::submitForReview
        );
    }

    @Test
    void shouldNotSubmitQuestionWithMultipleCorrectAnswers() {
        Question question = createQuestion();

        question.addOption(new QuestionOption("A", true));
        question.addOption(new QuestionOption("B", true));
        question.addOption(new QuestionOption("C", false));
        question.addOption(new QuestionOption("D", false));
        question.addOption(new QuestionOption("E", false));

        assertThrows(InvalidQuestionException.class,question::submitForReview);

        assertEquals(QuestionStatus.DRAFT, question.getStatus());
    }

    @Test
    void shouldNotAddMoreThanFiveOptions() {
        Question question = createValidQuestion();

        assertThrows(
                InvalidQuestionException.class,
                () -> question.addOption(
                        new QuestionOption("Option F", false)
                )
        );

        assertEquals(5, question.getOptions().size());
    }

    @Test
    void shouldNotAddNullOption() {
        Question question = createQuestion();

        assertThrows(
                IllegalArgumentException.class,
                () -> question.addOption(null)
        );
    }

    @Test
    void shouldSubmitQuestionWithExactlyOneCorrectAnswer() {
        Question question = createValidQuestion();

        question.submitForReview();

        assertEquals(
                QuestionStatus.PENDING_REVIEW,
                question.getStatus()
        );
    }

    @Test
    void shouldNotAddOptionAfterSubmittingQuestion() {
        Question question = createValidQuestion();
        question.submitForReview();

        assertThrows(
                QuestionStateConflictException.class,
                () -> question.addOption(
                        new QuestionOption("Option F", false)
                )
        );
    }

    @Test
    void shouldNotUpdateOptionTextAfterSubmittingQuestion() {
        Question question = createValidQuestion();
        QuestionOption option = question.getOptions().get(0);

        question.submitForReview();

        assertThrows(
                QuestionStateConflictException.class,
                () -> question.updateOptionText(option, "Modified option")
        );
    }

    @Test
    void shouldNotChangeCorrectOptionAfterSubmittingQuestion() {
        Question question = createValidQuestion();
        QuestionOption option = question.getOptions().get(1);

        question.submitForReview();

        assertThrows(
                QuestionStateConflictException.class,
                () -> question.setCorrectOption(option)
        );
    }

    @Test
    void shouldUpdateOptionTextWhileQuestionIsDraft() {
        Question question = createValidQuestion();
        QuestionOption option = question.getOptions().get(0);

        question.updateOptionText(option, "Updated option");

        assertEquals("Updated option", option.getText());
    }

    @Test
    void shouldChangeCorrectOptionWhileQuestionIsDraft() {
        Question question = createValidQuestion();
        QuestionOption newCorrectOption = question.getOptions().get(1);

        question.setCorrectOption(newCorrectOption);

        assertFalse(question.getOptions().get(0).isCorrect());
        assertTrue(newCorrectOption.isCorrect());
    }
}
