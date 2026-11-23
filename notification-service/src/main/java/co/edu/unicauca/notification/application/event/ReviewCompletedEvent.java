package co.edu.unicauca.notification.application.event;

import java.util.List;

public class ReviewCompletedEvent {

    private Long questionId;
    private Long authorId;
    private String result;
    private List<String> observations = List.of();

    public ReviewCompletedEvent() {
    }

    public ReviewCompletedEvent(
            Long questionId,
            Long authorId,
            String result,
            List<String> observations) {

        this.questionId = questionId;
        this.authorId = authorId;
        this.result = result;
        this.observations = observations == null ? List.of() : observations;
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

    public List<String> getObservations() {
        return observations;
    }
}
