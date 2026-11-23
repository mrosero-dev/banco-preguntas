package co.edu.unicauca.lisw2t5g02.model;

/** One reviewer decision, including the review round. */
public record Review(long reviewerId, ReviewStatus status, String observations, String reviewedAt, int roundNumber) {
    public Review(long reviewerId, ReviewStatus status, String observations, String reviewedAt) {
        this(reviewerId, status, observations, reviewedAt, 1);
    }
}
