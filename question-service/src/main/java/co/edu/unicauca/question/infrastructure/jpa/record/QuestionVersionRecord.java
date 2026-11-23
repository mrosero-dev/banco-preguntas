package co.edu.unicauca.question.infrastructure.jpa.record;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

/**
 * JPA representation of an immutable snapshot of a question version.
 */
@Entity
@Table(name = "question_versions")
public class QuestionVersionRecord {

    /** Snapshot of one answer option. */
    @Embeddable
    public static class OptionSnapshot {

        @Column(name = "option_text", nullable = false)
        private String text;

        @Column(name = "is_correct", nullable = false)
        private boolean correct;

        public OptionSnapshot() {
        }

        public OptionSnapshot(String text, boolean correct) {
            this.text = text;
            this.correct = correct;
        }

        public String getText() {
            return text;
        }

        public boolean isCorrect() {
            return correct;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionRecord question;

    @Column(nullable = false)
    private int versionNumber;

    @Column(length = 2000)
    private String statement;

    @Column(length = 5000)
    private String context;

    private String difficulty;
    private String category;
    private String topic;
    private String subtopic;
    private String competence;

    @Column(length = 3000)
    private String justification;

    private String source;

    @Column(nullable = false)
    private Instant createdAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "question_version_options",
            joinColumns = @JoinColumn(name = "version_id"))
    @OrderColumn(name = "position")
    private List<OptionSnapshot> options = new ArrayList<>();

    public QuestionVersionRecord() {
    }

    public Long getId() { return id; }
    public QuestionRecord getQuestion() { return question; }
    public void setQuestion(QuestionRecord question) { this.question = question; }
    public int getVersionNumber() { return versionNumber; }
    public void setVersionNumber(int versionNumber) { this.versionNumber = versionNumber; }
    public String getStatement() { return statement; }
    public void setStatement(String statement) { this.statement = statement; }
    public String getContext() { return context; }
    public void setContext(String context) { this.context = context; }
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }
    public String getSubtopic() { return subtopic; }
    public void setSubtopic(String subtopic) { this.subtopic = subtopic; }
    public String getCompetence() { return competence; }
    public void setCompetence(String competence) { this.competence = competence; }
    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public List<OptionSnapshot> getOptions() { return options; }
}
