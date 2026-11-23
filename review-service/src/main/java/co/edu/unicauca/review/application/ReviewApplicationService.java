package co.edu.unicauca.review.application;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicauca.review.domain.model.Review;
import co.edu.unicauca.review.domain.model.ReviewAssignment;
import co.edu.unicauca.review.domain.model.ReviewStatus;
import co.edu.unicauca.review.domain.repository.ReviewAssignmentRepository;
import co.edu.unicauca.review.domain.exception.ReviewAssignmentNotFoundException;
import co.edu.unicauca.review.domain.exception.ReviewConflictException;
import co.edu.unicauca.review.application.event.ReviewCompletedEvent;
import co.edu.unicauca.review.infrastructure.messaging.ReviewEventPublisher;
import co.edu.unicauca.review.application.event.ReviewAssignedEvent;
import co.edu.unicauca.review.application.event.QuestionResubmittedEvent;

@Service
public class ReviewApplicationService {

    private final ReviewAssignmentRepository repository;
    private final ReviewEventPublisher eventPublisher;

    public ReviewApplicationService(
            ReviewAssignmentRepository repository,
            ReviewEventPublisher eventPublisher) {

        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Registers a submitted question as pending reviewer assignment.
     * Does not assign reviewers automatically.
     */
    @Transactional
    public ReviewAssignment registerPendingQuestion(
            Long questionId,
            Long authorId) {

        Optional<ReviewAssignment> existing =
                repository.findByQuestionId(questionId);

        if (existing.isEmpty()) {
            return repository.save(new ReviewAssignment(questionId, authorId));
        }

        // The question was corrected and sent again: start a new round.
        ReviewAssignment assignment = existing.get();

        List<Long> previousReviewerIds = assignment.getReviews().stream()
                .map(Review::getReviewerId)
                .toList();

        if (!assignment.startNewRound()) {
            return assignment;
        }

        ReviewAssignment saved = repository.save(assignment);

        // The version of the question equals the new review round.
        eventPublisher.publishQuestionResubmitted(
                new QuestionResubmittedEvent(
                        questionId,
                        saved.getAuthorId(),
                        saved.getCurrentRound(),
                        previousReviewerIds
                )
        );

        return saved;
    }

    /**
     * Assigns reviewers to an existing pending question.
     */
    @Transactional
    public ReviewAssignment assignReviewers(
                Long questionId,
                Long authorId,
                List<Long> reviewerIds) {

        if (reviewerIds == null
                || reviewerIds.isEmpty()
                || reviewerIds.size() > 3) {

                throw new IllegalArgumentException(
                        "A question must have between one and three reviewers"
                );
        }

        ReviewAssignment assignment =
                repository.findByQuestionId(questionId)
                        .orElseGet(() ->
                                new ReviewAssignment(questionId, authorId)
                        );

        if (!assignment.getReviews().isEmpty()) {
                throw new ReviewConflictException(
                        "Reviewers can only be assigned to questions "
                                + "pending review"
                );
        }

        if (!assignment.getAuthorId().equals(authorId)) {
                throw new IllegalArgumentException(
                        "The author does not match the registered question"
                );
        }

        for (Long reviewerId : reviewerIds) {
                assignment.assignReviewer(reviewerId);
        }

        ReviewAssignment savedAssignment =
                repository.save(assignment);

        ReviewAssignedEvent event = new ReviewAssignedEvent(
                savedAssignment.getQuestionId(),
                savedAssignment.getAuthorId(),
                reviewerIds
        );

        eventPublisher.publishReviewAssigned(event);

        return savedAssignment;
   }
    @Transactional
    public ReviewAssignment approveReview(
            Long questionId,
            Long reviewerId) {

        ReviewAssignment assignment = findAssignment(questionId);

        Review review = findReviewerReview(assignment, reviewerId);

        review.approve();

        ReviewAssignment updatedAssignment =
                repository.save(assignment);

        publishEventIfCompleted(updatedAssignment);

        return updatedAssignment;
    }

    @Transactional
    public ReviewAssignment rejectReview(
            Long questionId,
            Long reviewerId,
            String observations) {

        ReviewAssignment assignment = findAssignment(questionId);

        Review review = findReviewerReview(assignment, reviewerId);

        review.reject(observations);

        ReviewAssignment updatedAssignment =
                repository.save(assignment);

        publishEventIfCompleted(updatedAssignment);

        return updatedAssignment;
    }

    @Transactional(readOnly = true)
    public ReviewAssignment getAssignment(Long questionId) {
        return findAssignment(questionId);
    }

    @Transactional(readOnly = true)
    public List<ReviewAssignment> findAssignmentsByReviewer(
            Long reviewerId) {

        return repository.findByReviewerId(reviewerId);
    }

    @Transactional(readOnly = true)
    public List<ReviewAssignment> findPendingAssignments() {
        return repository.findWithoutReviewers();
    }

    @Transactional(readOnly = true)
    public ReviewStatus getConsolidatedStatus(Long questionId) {
        return findAssignment(questionId).getConsolidatedStatus();
    }

    private ReviewAssignment findAssignment(Long questionId) {
        return repository.findByQuestionId(questionId)
                .orElseThrow(() ->
                        new ReviewAssignmentNotFoundException(
                                "Review assignment not found for question: "
                                        + questionId
                        )
                );
    }

    private Review findReviewerReview(
            ReviewAssignment assignment,
            Long reviewerId) {

        return assignment.getReviews().stream()
                .filter(review ->
                        review.getReviewerId().equals(reviewerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Reviewer is not assigned to this question"
                ));
    }

    private void publishEventIfCompleted(
            ReviewAssignment assignment) {

        if (assignment.isCompleted()) {

            ReviewCompletedEvent event =
                    new ReviewCompletedEvent(
                            assignment.getQuestionId(),
                            assignment.getAuthorId(),
                            assignment.getConsolidatedStatus(),
                            assignment.getReviews().stream()
                                    .map(Review::getObservations)
                                    .filter(text -> text != null
                                            && !text.isBlank())
                                    .toList()
                    );

            eventPublisher.publishReviewCompleted(event);
        }
    }
}
