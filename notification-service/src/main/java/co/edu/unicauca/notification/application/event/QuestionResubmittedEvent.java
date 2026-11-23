package co.edu.unicauca.notification.application.event;

import java.util.List;

/**
 * An improved version of a rejected question was sent back to review.
 */
public class QuestionResubmittedEvent {

    private Long questionId;
    private Long authorId;
    private int version;
    private List<Long> previousReviewerIds = List.of();

    public QuestionResubmittedEvent() {
    }

    public QuestionResubmittedEvent(
            Long questionId,
            Long authorId,
            int version,
            List<Long> previousReviewerIds) {

        this.questionId = questionId;
        this.authorId = authorId;
        this.version = version;
        this.previousReviewerIds = previousReviewerIds == null
                ? List.of() : previousReviewerIds;
    }

    public Long getQuestionId() { return questionId; }
    public Long getAuthorId() { return authorId; }
    public int getVersion() { return version; }
    public List<Long> getPreviousReviewerIds() { return previousReviewerIds; }
}
