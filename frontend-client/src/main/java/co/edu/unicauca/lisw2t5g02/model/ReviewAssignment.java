package co.edu.unicauca.lisw2t5g02.model;

import java.util.List;
import java.util.Optional;

/** Current review round plus the immutable history of previous rounds. */
public record ReviewAssignment(long id, long questionId, long authorId, List<Review> reviews, ReviewStatus consolidatedStatus, List<Review> history) {
    public ReviewAssignment(long id, long questionId, long authorId, List<Review> reviews, ReviewStatus consolidatedStatus) {
        this(id, questionId, authorId, reviews, consolidatedStatus, List.of());
    }

    public Optional<Review> reviewOf(long reviewerId) {
        return reviews.stream().filter(review -> review.reviewerId() == reviewerId).findFirst();
    }
}
