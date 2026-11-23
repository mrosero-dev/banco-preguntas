
package co.edu.unicauca.question.api.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UpdateQuestionRequest {

    @NotBlank
    private String statement;

    @NotBlank
    private String context;

    @NotBlank
    private String difficulty;

    @NotBlank
    private String category;

    @NotBlank
    private String topic;

    @NotBlank
    private String subtopic;

    @NotBlank
    private String competence;

    @NotBlank
    private String justification;

    @NotBlank
    private String source;

    @NotNull
    @Valid
    @Size(min = 5, max = 5)
    private List<QuestionOptionRequest> options;

    public UpdateQuestionRequest() {
    }

    public String getStatement() {
        return statement;
    }

    public void setStatement(String statement) {
        this.statement = statement;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getSubtopic() {
        return subtopic;
    }

    public void setSubtopic(String subtopic) {
        this.subtopic = subtopic;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

    public String getJustification() {
        return justification;
    }

    public void setJustification(String justification) {
        this.justification = justification;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public List<QuestionOptionRequest> getOptions() {
        return options;
    }

    public void setOptions(List<QuestionOptionRequest> options) {
        this.options = options;
    }
}
