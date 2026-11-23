package co.edu.unicauca.lisw2t5g02.model;

/**
 * Read-only copy of a question as it was when its version was reviewed.
 * The version number equals the review round that evaluated it.
 */
public record VersionedQuestion(int version, String createdAt, Question content) {
}
