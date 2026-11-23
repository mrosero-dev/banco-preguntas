package co.edu.unicauca.review.domain.exception;

public class ReviewAssignmentNotFoundException extends RuntimeException {

    public ReviewAssignmentNotFoundException(String message) {
        super(message);
    }
}