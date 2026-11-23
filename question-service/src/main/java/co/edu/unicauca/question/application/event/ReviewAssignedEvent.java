package co.edu.unicauca.question.application.event;

import java.util.List;

public class ReviewAssignedEvent {

    private Long questionId;
    private Long authorId;
    private List<Long> reviewerIds;

    public ReviewAssignedEvent() {
    }

    public ReviewAssignedEvent(
            Long questionId,
            Long authorId,
            List<Long> reviewerIds) {

        this.questionId = questionId;
        this.authorId = authorId;
        this.reviewerIds = reviewerIds;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public List<Long> getReviewerIds() {
        return reviewerIds;
    }
}