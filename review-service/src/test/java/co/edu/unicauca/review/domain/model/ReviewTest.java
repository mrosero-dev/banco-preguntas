
package co.edu.unicauca.review.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.review.domain.exception.ReviewConflictException;

class ReviewTest {

    @Test
    void shouldCreateReviewWithPendingStatus() {
        Review review = new Review(1L, 20L);

        assertEquals(1L, review.getQuestionId());
        assertEquals(20L, review.getReviewerId());
        assertEquals(ReviewStatus.PENDING, review.getStatus());
        assertNull(review.getObservations());
        assertNull(review.getReviewedAt());
    }

    @Test
    void shouldNotCreateReviewWithNullQuestionId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Review(null, 20L)
        );
    }

    @Test
    void shouldNotCreateReviewWithNullReviewerId() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Review(1L, null)
        );
    }

    @Test
    void shouldApprovePendingReview() {
        Review review = new Review(1L, 20L);

        review.approve();

        assertEquals(ReviewStatus.APPROVED, review.getStatus());
        assertNotNull(review.getReviewedAt());
        assertNull(review.getObservations());
    }

    @Test
    void shouldNotApproveReviewTwice() {
        Review review = new Review(1L, 20L);
        review.approve();

        assertThrows(
                ReviewConflictException.class,
                review::approve
        );
    }

    @Test
    void shouldRejectReviewWithValidObservations() {
        Review review = new Review(1L, 20L);
        String observations = "The question has an incorrect answer.";

        review.reject(observations);

        assertEquals(ReviewStatus.REJECTED, review.getStatus());
        assertEquals(observations, review.getObservations());
        assertNotNull(review.getReviewedAt());
    }

    @Test
    void shouldNotRejectReviewWithNullObservations() {
        Review review = new Review(1L, 20L);

        assertThrows(
                IllegalArgumentException.class,
                () -> review.reject(null)
        );

        assertEquals(ReviewStatus.PENDING, review.getStatus());
        assertNull(review.getReviewedAt());
    }

    @Test
    void shouldNotRejectReviewWithBlankObservations() {
        Review review = new Review(1L, 20L);

        assertThrows(
                IllegalArgumentException.class,
                () -> review.reject("   ")
        );

        assertEquals(ReviewStatus.PENDING, review.getStatus());
        assertNull(review.getReviewedAt());
    }

    @Test
    void shouldNotRejectReviewTwice() {
        Review review = new Review(1L, 20L);
        review.reject("Incorrect answer.");

        assertThrows(
                ReviewConflictException.class,
                () -> review.reject("Another observation.")
        );
    }

    @Test
    void shouldNotApproveRejectedReview() {
        Review review = new Review(1L, 20L);
        review.reject("Incorrect answer.");

        assertThrows(
                ReviewConflictException.class,
                review::approve
        );
    }

    @Test
    void shouldRestoreApprovedReview() {
        LocalDateTime reviewedAt = LocalDateTime.now();

        Review review = Review.restore(
                1L,
                20L,
                ReviewStatus.APPROVED,
                null,
                reviewedAt
        );

        assertEquals(ReviewStatus.APPROVED, review.getStatus());
        assertEquals(reviewedAt, review.getReviewedAt());
        assertNull(review.getObservations());
    }

    @Test
    void shouldRestoreRejectedReview() {
        LocalDateTime reviewedAt = LocalDateTime.now();
        String observations = "The justification is insufficient.";

        Review review = Review.restore(
                1L,
                20L,
                ReviewStatus.REJECTED,
                observations,
                reviewedAt
        );

        assertEquals(ReviewStatus.REJECTED, review.getStatus());
        assertEquals(observations, review.getObservations());
        assertEquals(reviewedAt, review.getReviewedAt());
    }

    @Test
    void shouldRestorePendingReview() {
        Review review = Review.restore(
                1L,
                20L,
                ReviewStatus.PENDING,
                null,
                null
        );

        assertEquals(ReviewStatus.PENDING, review.getStatus());
        assertNull(review.getObservations());
        assertNull(review.getReviewedAt());
    }

    @Test
    void shouldNotRestoreReviewWithNullStatus() {
        assertThrows(
                IllegalArgumentException.class,
                () -> Review.restore(
                        1L,
                        20L,
                        null,
                        null,
                        null
                )
        );
    }
}
