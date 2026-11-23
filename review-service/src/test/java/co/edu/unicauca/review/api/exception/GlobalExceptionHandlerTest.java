
package co.edu.unicauca.review.api.exception;

import co.edu.unicauca.review.domain.exception.ReviewAssignmentNotFoundException;
import co.edu.unicauca.review.domain.exception.ReviewConflictException;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler =
            new GlobalExceptionHandler();

    @Test
    void shouldReturnBadRequestForIllegalArgumentException() {

        ResponseEntity<String> response =
                handler.handleIllegalArgument(
                        new IllegalArgumentException("Invalid data")
                );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid data", response.getBody());
    }

    @Test
    void shouldReturnNotFoundForMissingAssignment() {

        ResponseEntity<String> response =
                handler.handleAssignmentNotFound(
                        new ReviewAssignmentNotFoundException(
                                "Assignment not found"
                        )
                );

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Assignment not found", response.getBody());
    }

    @Test
    void shouldReturnConflictForCompletedReview() {

        ResponseEntity<String> response =
                handler.handleReviewConflict(
                        new ReviewConflictException(
                                "Review already completed"
                        )
                );

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Review already completed", response.getBody());
    }

    @Test
    void shouldReturnBadRequestForInvalidArguments() throws Exception {

        Object target = new Object();

        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(target, "request");

        bindingResult.addError(
                new FieldError(
                        "request",
                        "reviewerId",
                        "Reviewer ID is required"
                )
        );

        Method method = TestController.class.getDeclaredMethod("testMethod");

        MethodParameter parameter = new MethodParameter(method, -1);

        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(
                        parameter,
                        bindingResult
                );

        ResponseEntity<String> response =
                handler.handleMethodArgumentNotValid(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(
                "reviewerId: Reviewer ID is required",
                response.getBody()
        );
    }

    private static class TestController {
        public void testMethod() {
        }
    }
}
