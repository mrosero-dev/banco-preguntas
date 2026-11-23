package co.edu.unicauca.review.domain.repository;

import java.util.List;
import java.util.Optional;

import co.edu.unicauca.review.domain.model.ReviewAssignment;

/**
 * Defines the contract for storing and retrieving review assignments.
 */
public interface ReviewAssignmentRepository {

    /**
     * Saves a review assignment.
     *
     * @param assignment assignment to save
     * @return saved assignment
     */
    ReviewAssignment save(ReviewAssignment assignment);

    /**
     * Finds a review assignment by its question identifier.
     *
     * @param questionId question identifier
     * @return the assignment if it exists
     */
    Optional<ReviewAssignment> findByQuestionId(Long questionId);

    /**
     * Finds the assignments in which the given reviewer participates.
     *
     * @param reviewerId reviewer identifier
     * @return assignments of the reviewer, newest first
     */
    List<ReviewAssignment> findByReviewerId(Long reviewerId);

    /**
     * Finds the registered questions that still have no reviewers.
     *
     * @return assignments waiting for reviewers
     */
    List<ReviewAssignment> findWithoutReviewers();
}