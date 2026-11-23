package co.edu.unicauca.review.api.dto;

import java.time.LocalDateTime;

import co.edu.unicauca.review.domain.model.Review;
import co.edu.unicauca.review.domain.model.ReviewStatus;

public class ReviewResponse {

    private Long reviewerId;
    private ReviewStatus status;
    private String observations;
    private LocalDateTime reviewedAt;
    private int roundNumber;

    public ReviewResponse(
            Long reviewerId,
            ReviewStatus status,
            String observations,
            LocalDateTime reviewedAt) {
        this(reviewerId, status, observations, reviewedAt, 1);
    }

    public ReviewResponse(Long reviewerId, ReviewStatus status, String observations, LocalDateTime reviewedAt, int roundNumber) {

        this.reviewerId = reviewerId;
        this.status = status;
        this.observations = observations;
        this.reviewedAt = reviewedAt;
        this.roundNumber = roundNumber;
    }

    public static ReviewResponse fromDomain(Review review) {
        return new ReviewResponse(
                review.getReviewerId(),
                review.getStatus(),
                review.getObservations(),
                review.getReviewedAt(),
                review.getRoundNumber()
        );
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

    public int getRoundNumber() { return roundNumber; }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
}