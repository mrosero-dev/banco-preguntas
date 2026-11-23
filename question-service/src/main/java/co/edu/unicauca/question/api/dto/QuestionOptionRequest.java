package co.edu.unicauca.question.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Represents an option received through the REST API.
 */
public class QuestionOptionRequest {

    @NotBlank
    private String text;

    private boolean correct;

    public QuestionOptionRequest() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}