package co.edu.unicauca.review.domain.exception;

public class ReviewConflictException extends RuntimeException {

    public ReviewConflictException(String message) {
        super(message);
    }
}