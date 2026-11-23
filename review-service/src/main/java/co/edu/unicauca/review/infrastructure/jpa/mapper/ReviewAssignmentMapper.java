package co.edu.unicauca.review.infrastructure.jpa.mapper;

import org.springframework.stereotype.Component;
import co.edu.unicauca.review.domain.model.Review;
import co.edu.unicauca.review.domain.model.ReviewAssignment;
import co.edu.unicauca.review.infrastructure.jpa.record.ReviewAssignmentRecord;
import co.edu.unicauca.review.infrastructure.jpa.record.ReviewRecord;

@Component
public class ReviewAssignmentMapper {
    public ReviewAssignmentRecord toRecord(ReviewAssignment assignment) {
        ReviewAssignmentRecord record = new ReviewAssignmentRecord();
        record.setQuestionId(assignment.getQuestionId());
        record.setAuthorId(assignment.getAuthorId());
        record.setCurrentRound(assignment.getCurrentRound());
        for (Review review : assignment.getAllReviews()) record.addReview(toReviewRecord(review));
        return record;
    }

    public ReviewAssignmentRecord updateRecord(ReviewAssignment assignment, ReviewAssignmentRecord record) {
        record.setQuestionId(assignment.getQuestionId());
        record.setAuthorId(assignment.getAuthorId());
        record.setCurrentRound(assignment.getCurrentRound());
        // Preserve completed rounds: only update/insert the matching reviewer + round.
        for (Review review : assignment.getAllReviews()) {
            ReviewRecord existing = record.getReviews().stream()
                    .filter(row -> row.getReviewerId().equals(review.getReviewerId())
                            && Math.max(1, row.getRoundNumber()) == review.getRoundNumber())
                    .findFirst().orElse(null);
            if (existing == null) record.addReview(toReviewRecord(review));
            else {
                existing.setQuestionId(review.getQuestionId());
                existing.setRoundNumber(review.getRoundNumber());
                existing.setStatus(review.getStatus());
                existing.setObservations(review.getObservations());
                existing.setReviewedAt(review.getReviewedAt());
            }
        }
        return record;
    }

    private ReviewRecord toReviewRecord(Review review) {
        ReviewRecord row = new ReviewRecord();
        row.setQuestionId(review.getQuestionId());
        row.setReviewerId(review.getReviewerId());
        row.setStatus(review.getStatus());
        row.setObservations(review.getObservations());
        row.setReviewedAt(review.getReviewedAt());
        row.setRoundNumber(review.getRoundNumber());
        return row;
    }

    public ReviewAssignment toDomain(ReviewAssignmentRecord record) {
        ReviewAssignment assignment = ReviewAssignment.restore(record.getId(), record.getQuestionId(), record.getAuthorId(), record.getCurrentRound());
        for (ReviewRecord row : record.getReviews()) {
            Review review = Review.restore(row.getQuestionId(), row.getReviewerId(), row.getStatus(), row.getObservations(), row.getReviewedAt(), Math.max(1, row.getRoundNumber()));
            if (review.getRoundNumber() == assignment.getCurrentRound()) assignment.restoreReview(review);
            else assignment.restoreHistoricalReview(review);
        }
        return assignment;
    }
}
