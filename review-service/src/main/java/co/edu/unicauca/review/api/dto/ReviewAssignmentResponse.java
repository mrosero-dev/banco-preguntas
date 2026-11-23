package co.edu.unicauca.review.api.dto;

import java.util.List;

import co.edu.unicauca.review.domain.model.ReviewAssignment;
import co.edu.unicauca.review.domain.model.Review;
import co.edu.unicauca.review.domain.model.ReviewStatus;

public class ReviewAssignmentResponse {

    private Long id;
    private Long questionId;
    private Long authorId;
    private List<ReviewResponse> reviews;
    private ReviewStatus consolidatedStatus;
    private List<ReviewResponse> history;

    public ReviewAssignmentResponse(
            Long id,
            Long questionId,
            Long authorId,
            List<ReviewResponse> reviews,
            ReviewStatus consolidatedStatus) {
        this(id, questionId, authorId, reviews, consolidatedStatus, List.of());
    }

    public ReviewAssignmentResponse(Long id, Long questionId, Long authorId, List<ReviewResponse> reviews, ReviewStatus consolidatedStatus, List<ReviewResponse> history) {

        this.id = id;
        this.questionId = questionId;
        this.authorId = authorId;
        this.reviews = reviews;
        this.consolidatedStatus = consolidatedStatus;
        this.history = history;
    }

    public static ReviewAssignmentResponse fromDomain(
            ReviewAssignment assignment) {

        List<ReviewResponse> reviews =
                assignment.getReviews()
                        .stream()
                        .map(ReviewResponse::fromDomain)
                        .toList();

        return new ReviewAssignmentResponse(
                assignment.getId(),
                assignment.getQuestionId(),
                assignment.getAuthorId(),
                reviews,
                assignment.getConsolidatedStatus(),
                assignment.getAllReviews().stream()
                        .filter(review -> review.getStatus() != ReviewStatus.PENDING)
                        .map(ReviewResponse::fromDomain).toList()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public List<ReviewResponse> getReviews() {
        return reviews;
    }

    public List<ReviewResponse> getHistory() { return history; }

    public ReviewStatus getConsolidatedStatus() {
        return consolidatedStatus;
    }
}