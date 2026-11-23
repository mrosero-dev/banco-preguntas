package co.edu.unicauca.question.domain.exception;

import co.edu.unicauca.question.domain.model.QuestionStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionExceptionsTest {

    @Test
    void invalidQuestionExceptionKeepsMessage() {
        InvalidQuestionException ex = new InvalidQuestionException("invalid");
        assertEquals("invalid", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void stateConflictExceptionKeepsMessage() {
        QuestionStateConflictException ex = new QuestionStateConflictException("conflict");
        assertEquals("conflict", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void questionStatusDefinesFullLifecycle() {
        assertArrayEquals(
                new QuestionStatus[]{QuestionStatus.DRAFT, QuestionStatus.PENDING_REVIEW,
                        QuestionStatus.IN_REVIEW, QuestionStatus.APPROVED, QuestionStatus.REJECTED},
                QuestionStatus.values());
        assertEquals(QuestionStatus.APPROVED, QuestionStatus.valueOf("APPROVED"));
    }
}
