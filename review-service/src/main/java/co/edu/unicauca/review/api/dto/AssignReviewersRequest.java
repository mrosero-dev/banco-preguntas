package co.edu.unicauca.review.api.dto;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AssignReviewersRequest {

    @NotNull(message = "Question ID is required")
    private Long questionId;

    @NotNull(message = "Author ID is required")
    private Long authorId;

    @NotEmpty(message = "At least one reviewer is required")
    @Size(
        max = 3,
        message = "A question cannot have more than three reviewers"
    )
    private List<@NotNull(message = "Reviewer ID cannot be null") Long> reviewerIds;

    public AssignReviewersRequest() {
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public List<Long> getReviewerIds() {
        return reviewerIds;
    }

    public void setReviewerIds(List<Long> reviewerIds) {
        this.reviewerIds = reviewerIds;
    }
}