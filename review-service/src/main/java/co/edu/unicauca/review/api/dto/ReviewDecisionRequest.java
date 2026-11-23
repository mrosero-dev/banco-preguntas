package co.edu.unicauca.review.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewDecisionRequest {

    @NotNull(message = "Reviewer ID is required")
    private Long reviewerId;

    @NotNull(message = "Decision is required")
    private Boolean approved;

    @Size(
        max = 3000,
        message = "Observations cannot exceed 3000 characters"
    )
    private String observations;

    public ReviewDecisionRequest() {
    }

    public Long getReviewerId() {
        return reviewerId;
    }

    public void setReviewerId(Long reviewerId) {
        this.reviewerId = reviewerId;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }
}