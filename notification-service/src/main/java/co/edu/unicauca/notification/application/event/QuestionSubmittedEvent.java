package co.edu.unicauca.notification.application.event;

/**
 * An author sent a question to review for the first time.
 */
public class QuestionSubmittedEvent {

    private Long questionId;
    private Long authorId;

    public QuestionSubmittedEvent() {
    }

    public QuestionSubmittedEvent(Long questionId, Long authorId) {
        this.questionId = questionId;
        this.authorId = authorId;
    }

    public Long getQuestionId() { return questionId; }
    public Long getAuthorId() { return authorId; }
}
