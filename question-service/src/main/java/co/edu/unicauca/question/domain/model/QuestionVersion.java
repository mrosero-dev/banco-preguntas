package co.edu.unicauca.question.domain.model;

import java.time.Instant;
import java.util.List;

/**
 * Immutable snapshot of the content of a question as it was when a review
 * round finished with a rejection. Version N corresponds to review round N.
 */
public final class QuestionVersion {

    /** Snapshot of one answer option. */
    public record Option(String text, boolean correct) {
    }

    private final int versionNumber;
    private final String statement;
    private final String context;
    private final String difficulty;
    private final String category;
    private final String topic;
    private final String subtopic;
    private final String competence;
    private final String justification;
    private final String source;
    private final List<Option> options;
    private final Instant createdAt;

    public QuestionVersion(
            int versionNumber,
            String statement,
            String context,
            String difficulty,
            String category,
            String topic,
            String subtopic,
            String competence,
            String justification,
            String source,
            List<Option> options,
            Instant createdAt) {

        if (versionNumber < 1) {
            throw new IllegalArgumentException(
                    "Version number must be greater than zero"
            );
        }

        if (options == null || createdAt == null) {
            throw new IllegalArgumentException(
                    "Version options and creation date cannot be null"
            );
        }

        this.versionNumber = versionNumber;
        this.statement = statement;
        this.context = context;
        this.difficulty = difficulty;
        this.category = category;
        this.topic = topic;
        this.subtopic = subtopic;
        this.competence = competence;
        this.justification = justification;
        this.source = source;
        this.options = List.copyOf(options);
        this.createdAt = createdAt;
    }

    /** Takes an immutable snapshot of the current content of a question. */
    public static QuestionVersion of(Question question, int versionNumber) {
        return new QuestionVersion(
                versionNumber,
                question.getStatement(),
                question.getContext(),
                question.getDifficulty(),
                question.getCategory(),
                question.getTopic(),
                question.getSubtopic(),
                question.getCompetence(),
                question.getJustification(),
                question.getSource(),
                question.getOptions().stream()
                        .map(option -> new Option(
                                option.getText(), option.isCorrect()))
                        .toList(),
                Instant.now()
        );
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public String getStatement() {
        return statement;
    }

    public String getContext() {
        return context;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getCategory() {
        return category;
    }

    public String getTopic() {
        return topic;
    }

    public String getSubtopic() {
        return subtopic;
    }

    public String getCompetence() {
        return competence;
    }

    public String getJustification() {
        return justification;
    }

    public String getSource() {
        return source;
    }

    public List<Option> getOptions() {
        return options;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
