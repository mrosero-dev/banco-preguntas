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
class ReviewRepositoryQueriesIntegrationTest {

    @Autowired
    private ReviewAssignmentRepository repository;

    @Test
    void shouldFindAssignmentsOfReviewer() {
        ReviewAssignment first = new ReviewAssignment(100L, 10L);
        first.assignReviewer(20L);
        first.assignReviewer(30L);
        repository.save(first);

        ReviewAssignment second = new ReviewAssignment(101L, 10L);
        second.assignReviewer(30L);
        repository.save(second);

        var forTwenty = repository.findByReviewerId(20L);
        var forThirty = repository.findByReviewerId(30L);

        assertEquals(1, forTwenty.size());
        assertEquals(2, forTwenty.get(0).getReviews().size());
        assertEquals(2, forThirty.size());
        assertEquals(101L, forThirty.get(0).getQuestionId());
        assertTrue(repository.findByReviewerId(99L).isEmpty());
    }

    @Test
    void shouldFindOnlyQuestionsWithoutReviewers() {
        repository.save(new ReviewAssignment(100L, 10L));

        ReviewAssignment assigned = new ReviewAssignment(101L, 10L);
        assigned.assignReviewer(20L);
        repository.save(assigned);

        var pending = repository.findWithoutReviewers();

        assertEquals(1, pending.size());
        assertEquals(100L, pending.get(0).getQuestionId());
    }
}
