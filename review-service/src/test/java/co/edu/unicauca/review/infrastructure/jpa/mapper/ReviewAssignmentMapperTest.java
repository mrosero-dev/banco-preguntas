
package co.edu.unicauca.review.infrastructure.jpa.mapper;

import co.edu.unicauca.review.domain.model.Review;
import co.edu.unicauca.review.domain.model.ReviewAssignment;
import co.edu.unicauca.review.domain.model.ReviewStatus;
import co.edu.unicauca.review.infrastructure.jpa.record.ReviewAssignmentRecord;
import co.edu.unicauca.review.infrastructure.jpa.record.ReviewRecord;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewAssignmentMapperTest {

    private final ReviewAssignmentMapper mapper =
            new ReviewAssignmentMapper();

    @Test
    void shouldConvertAssignmentToRecord() {
        ReviewAssignment assignment =
                new ReviewAssignment(10L, 1L);

        assignment.assignReviewer(2L);
        assignment.assignReviewer(3L);

        ReviewAssignmentRecord record =
                mapper.toRecord(assignment);

        assertEquals(10L, record.getQuestionId());
        assertEquals(1L, record.getAuthorId());
        assertEquals(2, record.getReviews().size());

        assertEquals(
                ReviewStatus.PENDING,
                record.getReviews().get(0).getStatus()
        );
    }

    @Test
    void shouldRestoreAssignmentFromRecord() {
        ReviewAssignment assignment =
                new ReviewAssignment(10L, 1L);

        assignment.assignReviewer(2L);
        assignment.assignReviewer(3L);

        assignment.getReviews().get(0).approve();
        assignment.getReviews().get(1).reject(
                "The justification is incomplete"
        );

        ReviewAssignmentRecord record =
                mapper.toRecord(assignment);

        ReviewAssignment restored =
                mapper.toDomain(record);

        assertEquals(10L, restored.getQuestionId());
        assertEquals(1L, restored.getAuthorId());
        assertEquals(2, restored.getReviews().size());

        assertEquals(
                ReviewStatus.APPROVED,
                restored.getReviews().get(0).getStatus()
        );

        assertEquals(
                ReviewStatus.REJECTED,
                restored.getReviews().get(1).getStatus()
        );

        assertEquals(
                "The justification is incomplete",
                restored.getReviews().get(1).getObservations()
        );
    }

    @Test
    void shouldPreservePendingStatusWhenRestoring() {
        ReviewAssignment assignment =
                new ReviewAssignment(10L, 1L);

        assignment.assignReviewer(2L);

        ReviewAssignmentRecord record =
                mapper.toRecord(assignment);

        ReviewAssignment restored =
                mapper.toDomain(record);

        assertEquals(
                ReviewStatus.PENDING,
                restored.getReviews().get(0).getStatus()
        );
    }

    @Test
    void shouldConvertAssignmentWithoutReviews() {
        ReviewAssignment assignment =
                new ReviewAssignment(10L, 1L);

        ReviewAssignmentRecord record =
                mapper.toRecord(assignment);

        ReviewAssignment restored =
                mapper.toDomain(record);

        assertEquals(10L, restored.getQuestionId());
        assertEquals(1L, restored.getAuthorId());
        assertTrue(restored.getReviews().isEmpty());
    }

    @Test
    void shouldPreserveReviewDatesAndObservations() {
        ReviewAssignment assignment =
                new ReviewAssignment(10L, 1L);

        assignment.assignReviewer(2L);

        LocalDateTime reviewedAt = LocalDateTime.now();
        String observations = "The question needs correction.";

        Review review = assignment.getReviews().get(0);

        ReviewAssignmentRecord record =
                mapper.toRecord(assignment);

        ReviewRecord reviewRecord = record.getReviews().get(0);
        reviewRecord.setStatus(ReviewStatus.REJECTED);
        reviewRecord.setObservations(observations);
        reviewRecord.setReviewedAt(reviewedAt);

        ReviewAssignment restored =
                mapper.toDomain(record);

        assertEquals(
                observations,
                restored.getReviews().get(0).getObservations()
        );

        assertEquals(
                reviewedAt,
                restored.getReviews().get(0).getReviewedAt()
        );
    }

    @Test
    void shouldUpdateExistingRecordWithoutDuplicatingReviewers() {
        ReviewAssignment assignment =
                new ReviewAssignment(10L, 1L);

        assignment.assignReviewer(2L);

        ReviewAssignmentRecord record =
                mapper.toRecord(assignment);

        assignment.getReviews().get(0).approve();

        mapper.updateRecord(assignment, record);

        assertEquals(1, record.getReviews().size());
        assertEquals(
                ReviewStatus.APPROVED,
                record.getReviews().get(0).getStatus()
        );
    }

    @Test
    void shouldAddNewReviewersWhenUpdatingRecord() {
        ReviewAssignment assignment =
                new ReviewAssignment(10L, 1L);

        assignment.assignReviewer(2L);

        ReviewAssignmentRecord record =
                mapper.toRecord(assignment);

        assignment.assignReviewer(3L);

        mapper.updateRecord(assignment, record);

        assertEquals(2, record.getReviews().size());
    }
}
