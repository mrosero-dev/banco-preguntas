
package co.edu.unicauca.review.infrastructure.jpa.repository;

import co.edu.unicauca.review.domain.model.ReviewAssignment;
import co.edu.unicauca.review.domain.model.ReviewStatus;
import co.edu.unicauca.review.domain.repository.ReviewAssignmentRepository;
import co.edu.unicauca.review.infrastructure.jpa.mapper.ReviewAssignmentMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({
        ReviewRepositoryAdapter.class,
        ReviewAssignmentMapper.class
})
class ReviewRepositoryIntegrationTest {

    @Autowired
    private ReviewAssignmentRepository repository;

    @Test
    void shouldPersistAndRetrieveAssignmentWithReviews() {

        ReviewAssignment assignment =
                new ReviewAssignment(100L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);

        assignment.getReviews().get(0).approve();

        assignment.getReviews().get(1).reject(
                "The justification is incomplete"
        );

        repository.save(assignment);

        Optional<ReviewAssignment> result =
                repository.findByQuestionId(100L);

        assertTrue(result.isPresent());

        ReviewAssignment recovered = result.get();

        assertNotNull(recovered.getId());
        assertEquals(100L, recovered.getQuestionId());
        assertEquals(10L, recovered.getAuthorId());
        assertEquals(2, recovered.getReviews().size());

        assertEquals(
                ReviewStatus.APPROVED,
                recovered.getReviews().get(0).getStatus()
        );

        assertEquals(
                ReviewStatus.REJECTED,
                recovered.getReviews().get(1).getStatus()
        );

        assertEquals(
                "The justification is incomplete",
                recovered.getReviews().get(1).getObservations()
        );

        assertTrue(recovered.isCompleted());

        assertEquals(
                ReviewStatus.REJECTED,
                recovered.getConsolidatedStatus()
        );
    }

    @Test
    void shouldReturnEmptyWhenAssignmentDoesNotExist() {

        Optional<ReviewAssignment> result =
                repository.findByQuestionId(999L);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldUpdateExistingAssignmentWithoutDuplicatingReviews() {

        ReviewAssignment assignment =
                new ReviewAssignment(200L, 10L);

        assignment.assignReviewer(20L);
        assignment.assignReviewer(30L);

        ReviewAssignment saved =
                repository.save(assignment);

        Long originalId = saved.getId();

        saved.getReviews().get(0).approve();

        ReviewAssignment updated =
                repository.save(saved);

        ReviewAssignment recovered =
                repository.findByQuestionId(200L).orElseThrow();

        assertEquals(originalId, updated.getId());
        assertEquals(originalId, recovered.getId());

        assertEquals(2, recovered.getReviews().size());

        assertEquals(
                ReviewStatus.APPROVED,
                recovered.getReviews().get(0).getStatus()
        );

        assertEquals(
                ReviewStatus.PENDING,
                recovered.getReviews().get(1).getStatus()
        );
    }

    @Test
    void shouldAddNewReviewerWhenUpdatingExistingAssignment() {

        ReviewAssignment assignment =
                new ReviewAssignment(300L, 10L);

        assignment.assignReviewer(20L);

        ReviewAssignment saved =
                repository.save(assignment);

        Long originalId = saved.getId();

        saved.assignReviewer(30L);

        ReviewAssignment updated =
                repository.save(saved);

        ReviewAssignment recovered =
                repository.findByQuestionId(300L).orElseThrow();

        assertEquals(originalId, updated.getId());
        assertEquals(2, recovered.getReviews().size());

        assertTrue(
                recovered.getReviews().stream()
                        .anyMatch(review ->
                                review.getReviewerId().equals(20L))
        );

        assertTrue(
                recovered.getReviews().stream()
                        .anyMatch(review ->
                                review.getReviewerId().equals(30L))
        );
    }

    @Test
    void shouldPersistAssignmentWithoutReviews() {

        ReviewAssignment assignment =
                new ReviewAssignment(400L, 10L);

        ReviewAssignment saved =
                repository.save(assignment);

        ReviewAssignment recovered =
                repository.findByQuestionId(400L).orElseThrow();

        assertNotNull(saved.getId());
        assertEquals(400L, recovered.getQuestionId());
        assertEquals(10L, recovered.getAuthorId());
        assertTrue(recovered.getReviews().isEmpty());
        assertFalse(recovered.isCompleted());
        assertEquals(
                ReviewStatus.PENDING,
                recovered.getConsolidatedStatus()
        );
    }
}
