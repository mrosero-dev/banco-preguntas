package co.edu.unicauca.question.domain.exception;

public class QuestionStateConflictException extends RuntimeException {

    public QuestionStateConflictException(String message) {
        super(message);
    }
}