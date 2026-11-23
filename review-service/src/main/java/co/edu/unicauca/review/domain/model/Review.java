
package co.edu.unicauca.review.domain.model;

import java.time.LocalDateTime;

import co.edu.unicauca.review.domain.exception.ReviewConflictException;
/**
 * Represents an individual evaluation of a question.
 */
public class Review {

    private Long id;
    private Long questionId;
    private Long reviewerId;
    private ReviewStatus status;
    private String observations;
    private LocalDateTime reviewedAt;
    private int roundNumber = 1;

    public Review(Long questionId, Long reviewerId) {

        if (questionId == null || reviewerId == null) {
            throw new IllegalArgumentException(
                    "Question ID and reviewer ID cannot be null"
            );
        }

        this.questionId = questionId;
        this.reviewerId = reviewerId;
        this.status = ReviewStatus.PENDING;
    }

    public int getRoundNumber() { return roundNumber; }

    public void setRoundNumber(int roundNumber) { this.roundNumber = roundNumber; }

    public void approve() {
        ensurePending();

        this.status = ReviewStatus.APPROVED;
        this.reviewedAt = LocalDateTime.now();
    }

    public void reject(String observations) {
        ensurePending();

        if (observations == null || observations.isBlank()) {
            throw new IllegalArgumentException(
                    "Observations are required when rejecting a question"
            );
        }

        this.status = ReviewStatus.REJECTED;
        this.observations = observations;
        this.reviewedAt = LocalDateTime.now();
    }

    private void ensurePending() {
        if (status != ReviewStatus.PENDING) {
            throw new ReviewConflictException(
                    "This review has already been completed"
            );
        }
    }

    public Long getId() {
        return id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public ReviewStatus getStatus() {
        return status;
    }

    public String getObservations() {
        return observations;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    /**
     * Restores a review from previously persisted information.
     */
    public static Review restore(
            Long questionId,
            Long reviewerId,
            ReviewStatus status,
            String observations,
            LocalDateTime reviewedAt) {
        return restore(questionId, reviewerId, status, observations, reviewedAt, 1);
    }

    public static Review restore(
            Long questionId, Long reviewerId, ReviewStatus status,
            String observations, LocalDateTime reviewedAt, int roundNumber) {

        Review review = new Review(questionId, reviewerId);

        if (status == null) {
            throw new IllegalArgumentException(
                    "Review status cannot be null"
            );
        }

        review.status = status;
        review.observations = observations;
        review.reviewedAt = reviewedAt;
        review.roundNumber = roundNumber;

        return review;
    }
}
