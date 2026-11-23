package co.edu.unicauca.review.infrastructure.jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import co.edu.unicauca.review.domain.model.ReviewAssignment;
import co.edu.unicauca.review.domain.repository.ReviewAssignmentRepository;
import co.edu.unicauca.review.infrastructure.jpa.mapper.ReviewAssignmentMapper;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({
        ReviewRepositoryAdapter.class,
        ReviewAssignmentMapper.class
})
class ReviewNewRoundIntegrationTest {

    @Autowired
    private ReviewAssignmentRepository repository;

    @Test
    void shouldPreservePreviousReviewsAndListQuestionAsPendingAgain() {
        ReviewAssignment assignment = new ReviewAssignment(100L, 10L);
        assignment.assignReviewer(20L);
        assignment.getReviews().get(0).reject("Falta fuente");
        repository.save(assignment);

        ReviewAssignment stored = repository.findByQuestionId(100L).orElseThrow();
        assertTrue(stored.startNewRound());
        repository.save(stored);

        ReviewAssignment reloaded = repository.findByQuestionId(100L).orElseThrow();

        assertTrue(reloaded.getReviews().isEmpty());
        assertEquals(1, reloaded.getHistory().size());
        assertEquals(1, repository.findWithoutReviewers().size());
        assertTrue(repository.findByReviewerId(20L).isEmpty());
    }
}
