package co.edu.unicauca.review.application.event;

import java.util.List;

import co.edu.unicauca.review.domain.model.ReviewStatus;

public class ReviewCompletedEvent {

    private Long questionId;
    private Long authorId;
    private ReviewStatus result;
    private List<String> observations = List.of();

    public ReviewCompletedEvent() {
    }

    public ReviewCompletedEvent(Long questionId, Long authorId, ReviewStatus result) {
        this.questionId = questionId;
        this.authorId = authorId;
        this.result = result;
    }

    public ReviewCompletedEvent(
            Long questionId,
            Long authorId,
            ReviewStatus result,
            List<String> observations) {

        this(questionId, authorId, result);
        this.observations = observations == null ? List.of() : observations;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public ReviewStatus getResult() {
        return result;
    }

    public List<String> getObservations() {
        return observations;
    }
}
