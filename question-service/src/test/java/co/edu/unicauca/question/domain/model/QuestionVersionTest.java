package co.edu.unicauca.question.domain.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;
import co.edu.unicauca.question.domain.state.RejectedState;

class QuestionVersionTest {

    private Question question() {
        Question question = new Question(
                10L, "Statement", "Context", "FACIL", "MATEMATICAS",
                "ARITMETICA", "SUMA", "RAZONAMIENTO", "Justification",
                "Source");
        question.addOption(new QuestionOption("One", true));
        question.addOption(new QuestionOption("Two", false));
        question.addOption(new QuestionOption("Three", false));
        question.addOption(new QuestionOption("Four", false));
        question.addOption(new QuestionOption("Five", false));
        return question;
    }

    private Question rejected() {
        Question question = question();
        question.submitForReview();
        question.startReview();
        question.reject();
        return question;
    }

    @Test
    void newQuestionStartsAtVersionOneWithoutHistory() {
        Question question = question();

        assertEquals(1, question.getVersion());
        assertTrue(question.getVersionHistory().isEmpty());
    }

    @Test
    void returningRejectedQuestionToDraftStoresSnapshotAndIncrementsVersion() {
        Question question = rejected();

        question.returnToDraft();

        assertEquals(QuestionStatus.DRAFT, question.getStatus());
        assertEquals(2, question.getVersion());
        assertEquals(1, question.getVersionHistory().size());

        QuestionVersion snapshot = question.getVersionHistory().get(0);
        assertEquals(1, snapshot.getVersionNumber());
        assertEquals("Statement", snapshot.getStatement());
        assertEquals("Context", snapshot.getContext());
        assertEquals(5, snapshot.getOptions().size());
        assertTrue(snapshot.getOptions().get(0).correct());
    }

    @Test
    void snapshotIsNotAffectedByLaterEdits() {
        Question question = rejected();
        question.returnToDraft();

        question.updateDetails(
                "New statement", "New context", "MEDIA", "MATEMATICAS",
                "ARITMETICA", "RESTA", "RAZONAMIENTO", "New justification",
                "New source",
                List.of(new QuestionOption("A", false),
                        new QuestionOption("B", true),
                        new QuestionOption("C", false),
                        new QuestionOption("D", false),
                        new QuestionOption("E", false)));

        QuestionVersion snapshot = question.getVersionHistory().get(0);
        assertEquals("Statement", snapshot.getStatement());
        assertEquals("One", snapshot.getOptions().get(0).text());
        assertEquals("New statement", question.getStatement());
    }

    @Test
    void secondRejectionAddsSecondSnapshot() {
        Question question = rejected();
        question.returnToDraft();
        question.submitForReview();
        question.startReview();
        question.reject();

        question.returnToDraft();

        assertEquals(3, question.getVersion());
        assertEquals(2, question.getVersionHistory().size());
        assertEquals(2, question.getVersionHistory().get(1).getVersionNumber());
    }

    @Test
    void improvedVersionIsNotAllowedWhenQuestionIsNotRejected() {
        Question question = question();

        assertThrows(QuestionStateConflictException.class,
                question::returnToDraft);
        assertEquals(1, question.getVersion());
        assertTrue(question.getVersionHistory().isEmpty());
    }

    @Test
    void versionHistoryCannotBeModifiedFromOutside() {
        Question question = rejected();
        question.returnToDraft();

        assertThrows(UnsupportedOperationException.class,
                () -> question.getVersionHistory().clear());
    }

    @Test
    void restoreRecoversVersionAndHistory() {
        QuestionVersion previous = new QuestionVersion(
                1, "S", "C", "FACIL", "M", "T", "ST", "R", "J", "B",
                List.of(new QuestionVersion.Option("One", true)),
                Instant.now());

        Question restored = Question.restore(
                5L, 10L, "S2", "C2", "FACIL", "M", "T", "ST", "R", "J",
                "B", new RejectedState(), List.of(), 2, List.of(previous));

        assertEquals(2, restored.getVersion());
        assertEquals(1, restored.getVersionHistory().size());
    }

    @Test
    void versionRejectsInvalidData() {
        assertThrows(IllegalArgumentException.class,
                () -> new QuestionVersion(0, "S", "C", "F", "M", "T", "ST",
                        "R", "J", "B", List.of(), Instant.now()));
        assertThrows(IllegalArgumentException.class,
                () -> new QuestionVersion(1, "S", "C", "F", "M", "T", "ST",
                        "R", "J", "B", null, Instant.now()));
    }
}
