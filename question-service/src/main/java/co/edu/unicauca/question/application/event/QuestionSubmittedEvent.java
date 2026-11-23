package co.edu.unicauca.question.application.event;

public class QuestionSubmittedEvent {

    private Long questionId;
    private Long authorId;

    public QuestionSubmittedEvent() {
    }

    public QuestionSubmittedEvent(Long questionId, Long authorId) {
        this.questionId = questionId;
        this.authorId = authorId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getAuthorId() {
        return authorId;
    }
}