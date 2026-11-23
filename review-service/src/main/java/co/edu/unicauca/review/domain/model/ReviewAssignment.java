
package co.edu.unicauca.review.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the assignment of reviewers to a question.
 */
public class ReviewAssignment {

    private Long id;
    private Long questionId;
    private Long authorId;

    private final List<Review> reviews = new ArrayList<>();
    private final List<Review> history = new ArrayList<>();
    private int currentRound = 1;

    public ReviewAssignment(Long questionId, Long authorId) {

        if (questionId == null || authorId == null) {
            throw new IllegalArgumentException(
                    "Question ID and author ID cannot be null"
            );
        }

        this.questionId = questionId;
        this.authorId = authorId;
    }

    public void assignReviewer(Long reviewerId) {

        if (reviewerId == null) {
            throw new IllegalArgumentException(
                    "Reviewer ID cannot be null"
            );
        }

        if (reviews.size() >= 3) {
            throw new IllegalStateException(
                    "A question cannot have more than three reviewers"
            );
        }

        if (reviews.stream().anyMatch(
                review -> review.getReviewerId().equals(reviewerId))) {
            throw new IllegalArgumentException(
                    "Reviewer is already assigned to this question"
            );
        }

        if (reviewerId.equals(authorId)) {
            throw new IllegalArgumentException(
                    "The author cannot review their own question"
            );
        }

        Review review = new Review(questionId, reviewerId);
        review.setRoundNumber(currentRound);
        reviews.add(review);
    }

    /**
     * Opens a new review round after the author corrected a rejected
     * question. Completed evaluations are archived in history and retained.
     * It only applies when every assigned reviewer already evaluated.
     *
     * @return true if a new round was opened
     */
    public boolean startNewRound() {

        if (reviews.isEmpty() || !isCompleted()) {
            return false;
        }

        history.addAll(reviews);
        reviews.clear();
        currentRound++;

        return true;
    }

    public boolean isCompleted() {

    return !reviews.isEmpty()
            && reviews.stream().allMatch(
                    review -> review.getStatus() != ReviewStatus.PENDING
            );
}

    public ReviewStatus getConsolidatedStatus() {

        if (!isCompleted()) {
            return ReviewStatus.PENDING;
        }

        boolean hasRejectedReview = reviews.stream()
                .anyMatch(
                        review -> review.getStatus() == ReviewStatus.REJECTED
                );

        return hasRejectedReview
                ? ReviewStatus.REJECTED
                : ReviewStatus.APPROVED;
    }

    public List<Review> getReviews() {
        return Collections.unmodifiableList(reviews);
    }

    public List<Review> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public List<Review> getAllReviews() {
        List<Review> all = new ArrayList<>(history);
        all.addAll(reviews);
        return Collections.unmodifiableList(all);
    }

    public int getCurrentRound() { return currentRound; }

    public void restoreHistoricalReview(Review review) { history.add(review); }

    public Long getId() {
        return id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    /**
     * Restores an existing review without assigning a new reviewer.
     */
    public void restoreReview(Review review) {

        if (review == null) {
            throw new IllegalArgumentException(
                    "Review cannot be null"
            );
        }

        if (!questionId.equals(review.getQuestionId())) {
            throw new IllegalArgumentException(
                    "Review belongs to a different question"
            );
        }

        if (reviews.size() >= 3) {
            throw new IllegalStateException(
                    "A question cannot have more than three reviewers"
            );
        }

        if (review.getReviewerId().equals(authorId)) {
            throw new IllegalArgumentException(
                    "The author cannot review their own question"
            );
        }

        if (reviews.stream().anyMatch(
                existing -> existing.getReviewerId()
                        .equals(review.getReviewerId()))) {
            throw new IllegalArgumentException(
                    "Reviewer is already assigned to this question"
            );
        }

        reviews.add(review);
    }

    /**
     * Restores an assignment with its persisted identifier.
     */
    public static ReviewAssignment restore(
            Long id,
            Long questionId,
            Long authorId) {
        return restore(id, questionId, authorId, 1);
    }

    public static ReviewAssignment restore(
            Long id, Long questionId, Long authorId, int currentRound) {

        ReviewAssignment assignment =
                new ReviewAssignment(questionId, authorId);

        assignment.id = id;
        assignment.currentRound = Math.max(1, currentRound);

        return assignment;
    }
}
