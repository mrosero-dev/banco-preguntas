
package co.edu.unicauca.question.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.edu.unicauca.question.domain.exception.InvalidQuestionException;
import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;
import co.edu.unicauca.question.domain.state.DraftState;
import co.edu.unicauca.question.domain.state.QuestionState;

public class Question {

    private Long id;
    private Long authorId;
    private String statement;
    private String context;
    private String difficulty;
    private String category;
    private String topic;
    private String subtopic;
    private String competence;
    private String justification;
    private String source;
    private QuestionState state = new DraftState();
    private int version = 1;

    /** Snapshots of the previous versions, oldest first. */
    private final List<QuestionVersion> versionHistory = new ArrayList<>();

    private final List<QuestionOption> options = new ArrayList<>();

    public Question(
            Long authorId,
            String statement,
            String context,
            String difficulty,
            String category,
            String topic,
            String subtopic,
            String competence,
            String justification,
            String source
    ) {
        if (authorId == null) {
            throw new IllegalArgumentException("Author ID cannot be null");
        }

        if (statement == null || statement.isBlank()) {
            throw new IllegalArgumentException(
                    "Question statement cannot be empty"
            );
        }

        this.authorId = authorId;
        this.statement = statement;
        this.context = context;
        this.difficulty = difficulty;
        this.category = category;
        this.topic = topic;
        this.subtopic = subtopic;
        this.competence = competence;
        this.justification = justification;
        this.source = source;
    }

    public static Question restore(
            Long id,
            Long authorId,
            String statement,
            String context,
            String difficulty,
            String category,
            String topic,
            String subtopic,
            String competence,
            String justification,
            String source,
            QuestionState restoredState,
            List<QuestionOption> restoredOptions
    ) {
        return restore(id, authorId, statement, context, difficulty,
                category, topic, subtopic, competence, justification,
                source, restoredState, restoredOptions, 1, List.of());
    }

    public static Question restore(
            Long id,
            Long authorId,
            String statement,
            String context,
            String difficulty,
            String category,
            String topic,
            String subtopic,
            String competence,
            String justification,
            String source,
            QuestionState restoredState,
            List<QuestionOption> restoredOptions,
            int restoredVersion,
            List<QuestionVersion> restoredHistory
    ) {
        if (restoredVersion < 1 || restoredHistory == null) {
            throw new IllegalArgumentException(
                    "Question version data is not valid"
            );
        }

        if (id == null || restoredState == null || restoredOptions == null) {
            throw new IllegalArgumentException(
                    "Question restoration data cannot be null"
            );
        }

        Question question = new Question(
                authorId,
                statement,
                context,
                difficulty,
                category,
                topic,
                subtopic,
                competence,
                justification,
                source
        );

        question.id = id;
        question.options.addAll(restoredOptions);
        question.state = restoredState;
        question.version = restoredVersion;
        question.versionHistory.addAll(restoredHistory);

        return question;
    }

    public void submitForReview() {
        validateOptions();
        state = state.submitForReview();
    }

    public void startReview() {
        state = state.startReview();
    }

    public void approve() {
        state = state.approve();
    }

    public void reject() {
        state = state.reject();
    }

    /**
     * Creates an improved version of a rejected question: keeps an immutable
     * copy of the content that was reviewed, increments the version number
     * and moves the question back to DRAFT. Only valid from REJECTED.
     */
    public void returnToDraft() {
        QuestionState nextState = state.returnToDraft();

        versionHistory.add(QuestionVersion.of(this, version));
        version++;
        state = nextState;
    }

    public int getVersion() {
        return version;
    }

    /** Previous versions of this question, oldest first. */
    public List<QuestionVersion> getVersionHistory() {
        return Collections.unmodifiableList(versionHistory);
    }

    public QuestionStatus getStatus() {
        return state.getStatus();
    }

    private void ensureDraft() {
        if (getStatus() != QuestionStatus.DRAFT) {
            throw new QuestionStateConflictException(
                    "La pregunta solo se puede modificar en estado BORRADOR"
            );
        }
    }

    /**
     * Updates all editable question information.
     * The author and question identifier cannot be changed.
     */
    public void updateDetails(
            String statement,
            String context,
            String difficulty,
            String category,
            String topic,
            String subtopic,
            String competence,
            String justification,
            String source,
            List<QuestionOption> updatedOptions
    ) {
        ensureDraft();

        if (updatedOptions == null) {
            throw new IllegalArgumentException(
                    "Question options cannot be null"
            );
        }

        if (updatedOptions.stream().anyMatch(option -> option == null)) {
            throw new IllegalArgumentException(
                    "Question options cannot contain null values"
            );
        }

        if (updatedOptions.size() != 5) {
            throw new InvalidQuestionException(
                    "A question must have exactly five options"
            );
        }

        long correctAnswers = updatedOptions.stream()
                .filter(QuestionOption::isCorrect)
                .count();

        if (correctAnswers != 1) {
            throw new InvalidQuestionException(
                    "A question must have exactly one correct answer"
            );
        }

        this.statement = statement;
        this.context = context;
        this.difficulty = difficulty;
        this.category = category;
        this.topic = topic;
        this.subtopic = subtopic;
        this.competence = competence;
        this.justification = justification;
        this.source = source;

        this.options.clear();
        this.options.addAll(updatedOptions);
    }

    public Long getId() {
        return id;
    }

    public Long getAuthorId() {
        return authorId;
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

    public List<QuestionOption> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public void addOption(QuestionOption option) {
        ensureDraft();

        if (option == null) {
            throw new IllegalArgumentException(
                    "Question option cannot be null"
            );
        }

        if (options.size() >= 5) {
            throw new InvalidQuestionException(
                    "A question cannot have more than five options"
            );
        }

        options.add(option);
    }

    public void updateOptionText(QuestionOption option, String text) {
        ensureDraft();

        if (option == null || !options.contains(option)) {
            throw new IllegalArgumentException(
                    "Option does not belong to this question"
            );
        }

        option.updateText(text);
    }

    public void setCorrectOption(QuestionOption correctOption) {
        ensureDraft();

        if (correctOption == null || !options.contains(correctOption)) {
            throw new IllegalArgumentException(
                    "Option does not belong to this question"
            );
        }

        for (QuestionOption option : options) {
            if (option == correctOption) {
                option.markAsCorrect();
            } else {
                option.markAsIncorrect();
            }
        }
    }

    public void validateOptions() {
        if (options.size() != 5) {
            throw new InvalidQuestionException(
                    "A question must have exactly five options"
            );
        }

        long correctAnswers = options.stream()
                .filter(QuestionOption::isCorrect)
                .count();

        if (correctAnswers != 1) {
            throw new InvalidQuestionException(
                    "A question must have exactly one correct answer"
            );
        }
    }
}
