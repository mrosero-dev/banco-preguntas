package co.edu.unicauca.review.domain.exception;

import co.edu.unicauca.review.domain.model.ReviewStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReviewExceptionsTest {

    @Test
    void conflictExceptionKeepsMessage() {
        ReviewConflictException ex = new ReviewConflictException("conflict");
        assertEquals("conflict", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void notFoundExceptionKeepsMessage() {
        ReviewAssignmentNotFoundException ex = new ReviewAssignmentNotFoundException("missing");
        assertEquals("missing", ex.getMessage());
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void reviewStatusValues() {
        assertArrayEquals(
                new ReviewStatus[]{ReviewStatus.PENDING, ReviewStatus.APPROVED, ReviewStatus.REJECTED},
                ReviewStatus.values());
    }
}
