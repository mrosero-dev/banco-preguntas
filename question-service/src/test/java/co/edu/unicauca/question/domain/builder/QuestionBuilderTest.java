
package co.edu.unicauca.question.domain.builder;

import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;
import co.edu.unicauca.question.domain.model.QuestionStatus;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionBuilderTest {

    @Test
    void shouldBuildSingleChoiceQuestionSuccessfully() {

        // Arrange
        SingleChoiceQuestionBuilder builder =
                new SingleChoiceQuestionBuilder();

        QuestionDirector director = new QuestionDirector(builder);

        List<QuestionOption> options = List.of(
                new QuestionOption("Option A", true),
                new QuestionOption("Option B", false),
                new QuestionOption("Option C", false),
                new QuestionOption("Option D", false),
                new QuestionOption("Option E", false)
        );

        // Act
        Question question = director.constructQuestion(
                1L,
                "What is object-oriented programming?",
                "Programming concepts",
                "MEDIUM",
                "COMPUTING",
                "Programming",
                "Object-oriented programming",
                "Software development",
                "It is a programming paradigm based on objects.",
                "Software Engineering textbook",
                options
        );

        // Assert
        assertNotNull(question);
        assertEquals(1L, question.getAuthorId());
        assertEquals(
                "What is object-oriented programming?",
                question.getStatement()
        );
        assertEquals(5, question.getOptions().size());

        assertEquals(1, question.getOptions().stream()
                .filter(QuestionOption::isCorrect)
                .count());

        assertEquals(QuestionStatus.DRAFT, question.getStatus());
    }

    @Test
    void shouldResetBuilderAfterBuildingQuestion() {

        SingleChoiceQuestionBuilder builder =
                new SingleChoiceQuestionBuilder();

        QuestionDirector director = new QuestionDirector(builder);

        List<QuestionOption> firstOptions = List.of(
                new QuestionOption("A", true),
                new QuestionOption("B", false),
                new QuestionOption("C", false),
                new QuestionOption("D", false),
                new QuestionOption("E", false)
        );

        Question firstQuestion = director.constructQuestion(
                1L,
                "First question",
                "First context",
                "EASY",
                "COMPUTING",
                "Programming",
                "Java",
                "Development",
                "First justification",
                "First source",
                firstOptions
        );

        List<QuestionOption> secondOptions = List.of(
                new QuestionOption("F", false),
                new QuestionOption("G", true),
                new QuestionOption("H", false),
                new QuestionOption("I", false),
                new QuestionOption("J", false)
        );

        Question secondQuestion = director.constructQuestion(
                2L,
                "Second question",
                "Second context",
                "MEDIUM",
                "COMPUTING",
                "Programming",
                "Python",
                "Development",
                "Second justification",
                "Second source",
                secondOptions
        );

        assertEquals("First question", firstQuestion.getStatement());
        assertEquals("Second question", secondQuestion.getStatement());

        assertEquals(5, firstQuestion.getOptions().size());
        assertEquals(5, secondQuestion.getOptions().size());

        assertEquals(
                "A",
                firstQuestion.getOptions().get(0).getText()
        );

        assertEquals(
                "F",
                secondQuestion.getOptions().get(0).getText()
        );

        assertEquals(1, firstQuestion.getOptions().stream()
                .filter(QuestionOption::isCorrect)
                .count());

        assertEquals(1, secondQuestion.getOptions().stream()
                .filter(QuestionOption::isCorrect)
                .count());
    }

    @Test
    void shouldNotCreateDirectorWithNullBuilder() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new QuestionDirector(null)
        );
    }

    @Test
    void shouldNotBuildQuestionWithNullOptions() {

        SingleChoiceQuestionBuilder builder =
                new SingleChoiceQuestionBuilder();

        QuestionDirector director = new QuestionDirector(builder);

        assertThrows(
                IllegalArgumentException.class,
                () -> director.constructQuestion(
                        1L,
                        "Question statement",
                        "Context",
                        "MEDIUM",
                        "COMPUTING",
                        "Programming",
                        "Java",
                        "Development",
                        "Justification",
                        "Source",
                        null
                )
        );
    }
}
