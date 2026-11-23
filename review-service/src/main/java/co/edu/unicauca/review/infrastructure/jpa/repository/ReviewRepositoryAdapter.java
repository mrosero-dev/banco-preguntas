package co.edu.unicauca.review.infrastructure.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import co.edu.unicauca.review.domain.model.ReviewAssignment;
import co.edu.unicauca.review.domain.repository.ReviewAssignmentRepository;
import co.edu.unicauca.review.infrastructure.jpa.mapper.ReviewAssignmentMapper;
import co.edu.unicauca.review.infrastructure.jpa.record.ReviewAssignmentRecord;

@Repository
public class ReviewRepositoryAdapter
        implements ReviewAssignmentRepository {

    private final ReviewJpaRepository jpaRepository;
    private final ReviewAssignmentMapper mapper;

    public ReviewRepositoryAdapter(
            ReviewJpaRepository jpaRepository,
            ReviewAssignmentMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public ReviewAssignment save(ReviewAssignment assignment) {

        ReviewAssignmentRecord record;

        if (assignment.getId() == null) {

            record = mapper.toRecord(assignment);

        } else {

            record = jpaRepository.findById(assignment.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Review assignment not found: "
                                    + assignment.getId()
                    ));

            record = mapper.updateRecord(assignment, record);
        }

        ReviewAssignmentRecord savedRecord =
                jpaRepository.save(record);

        return mapper.toDomain(savedRecord);
    }

    @Override
    public Optional<ReviewAssignment> findByQuestionId(Long questionId) {

        return jpaRepository.findByQuestionId(questionId)
                .map(mapper::toDomain);
    }

    @Override
    public List<ReviewAssignment> findByReviewerId(Long reviewerId) {

        return jpaRepository.findByReviewerId(reviewerId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ReviewAssignment> findWithoutReviewers() {

        return jpaRepository.findWithoutReviewers()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
