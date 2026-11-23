package co.edu.unicauca.question.application.event;

public class ReviewCompletedEvent {

    private Long questionId;
    private Long authorId;
    private String result;

    public ReviewCompletedEvent() {
    }

    public ReviewCompletedEvent(
            Long questionId,
            Long authorId,
            String result) {

        this.questionId = questionId;
        this.authorId = authorId;
        this.result = result;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public String getResult() {
        return result;
    }
}