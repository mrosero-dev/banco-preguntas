package co.edu.unicauca.question.domain.model;

/**
 * Represents the possible lifecycle statuses of a question.
 */
public enum QuestionStatus {
    DRAFT,
    PENDING_REVIEW,
    IN_REVIEW,
    APPROVED,
    REJECTED
}
