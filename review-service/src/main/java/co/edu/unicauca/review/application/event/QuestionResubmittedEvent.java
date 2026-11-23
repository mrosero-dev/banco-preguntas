package co.edu.unicauca.review.application.event;

import java.util.List;

/**
 * Published when the author sends an improved version of a rejected question
 * back to review (HU-2.3). The version number equals the new review round.
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
