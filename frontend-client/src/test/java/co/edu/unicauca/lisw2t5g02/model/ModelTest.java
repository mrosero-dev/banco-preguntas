package co.edu.unicauca.lisw2t5g02.model;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    private static Question question(String statement, List<QuestionOption> options) {
        return new Question(5L, 1, statement, "c", "Media", "cat", "t", "s",
                "comp", "j", "src", QuestionStatus.DRAFT, options);
    }

    @Test
    void shouldBuildShortTitleForLongStatements() {
        Question q = question("x".repeat(100), List.of());

        assertTrue(q.title().startsWith("#5 - "));
        assertTrue(q.title().endsWith("..."));
        assertTrue(q.title().length() < 70);
    }

    @Test
    void shouldKeepShortStatementsAndFlattenLineBreaks() {
        assertEquals("#5 - a b", question("a\nb", List.of()).title());
    }

    @Test
    void shouldReturnCorrectOptionText() {
        Question q = question("s", List.of(
                new QuestionOption("A", false), new QuestionOption("B", true)));

        assertEquals("B", q.correctText());
        assertEquals("", question("s", List.of()).correctText());
    }

    @Test
    void shouldExposeSpanishLabels() {
        assertEquals("Pendiente de revisión",
                QuestionStatus.PENDING_REVIEW.toString());
        assertEquals("Rechazada", ReviewStatus.REJECTED.getLabel());
        assertEquals("Autor de preguntas", Role.AUTHOR.getLabel());
    }

    @Test
    void shouldFindReviewOfAssignedReviewerOnly() {
        ReviewAssignment assignment = new ReviewAssignment(1, 5, 1,
                List.of(new Review(4, ReviewStatus.PENDING, "", "")),
                ReviewStatus.PENDING);

        assertTrue(assignment.reviewOf(4).isPresent());
        assertTrue(assignment.reviewOf(7).isEmpty());
    }
}
