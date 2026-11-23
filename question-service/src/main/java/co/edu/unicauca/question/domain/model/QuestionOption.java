package co.edu.unicauca.question.domain.model;

/**
 * Represents an answer option belonging to a question.
 */
public class QuestionOption {

    private Long id;
    private String text;
    private boolean correct;
    private static final int MAX_TEXT_LENGTH = 250;

    public QuestionOption(String text, boolean correct) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Option text cannot be empty"
            );
        }
        if (text.length() > MAX_TEXT_LENGTH) {
            throw new IllegalArgumentException(
                    "Option text cannot exceed " + MAX_TEXT_LENGTH + " characters"
            );
        }
        this.text = text;
        this.correct = correct;
    }

    /**
     * Restores the identifier of an existing question option.
     *
     * @param id persisted option identifier
     */
    public void restoreId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException(
                    "Option ID cannot be null"
            );
        }

        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public boolean isCorrect() {
        return correct;
    }

    void updateText(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Option text cannot be empty"
            );
        }
        if (text.length() > MAX_TEXT_LENGTH) {
            throw new IllegalArgumentException(
                    "Option text cannot exceed " + MAX_TEXT_LENGTH + " characters"
            );
        }
        this.text = text;
    }

    void markAsCorrect() {
        this.correct = true;
    }

    void markAsIncorrect() {
        this.correct = false;
    }
}
