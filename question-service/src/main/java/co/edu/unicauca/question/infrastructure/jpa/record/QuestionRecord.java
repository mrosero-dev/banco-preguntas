
package co.edu.unicauca.question.infrastructure.jpa.record;

import co.edu.unicauca.question.domain.model.QuestionStatus;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA representation of a question.
 */
@Entity
@Table(name = "questions")
public class QuestionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long authorId;

    @Column(nullable = false, length = 2000)
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<QuestionOptionRecord> options = new ArrayList<>();

    @Column(name = "version_number", nullable = false,
            columnDefinition = "integer default 1")
    private int version = 1;

    @OneToMany(
            mappedBy = "question",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @OrderBy("versionNumber ASC")
    private List<QuestionVersionRecord> versionSnapshots = new ArrayList<>();

    public QuestionRecord() {
    }

    public Long getId() {
        return id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
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

    public QuestionStatus getStatus() {
        return status;
    }

    public void setStatus(QuestionStatus status) {
        this.status = status;
    }

    public List<QuestionOptionRecord> getOptions() {
        return options;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<QuestionVersionRecord> getVersionSnapshots() {
        return versionSnapshots;
    }

    public void addVersionSnapshot(QuestionVersionRecord snapshot) {
        versionSnapshots.add(snapshot);
        snapshot.setQuestion(this);
    }

    public void addOption(QuestionOptionRecord option) {
        options.add(option);
        option.setQuestion(this);
    }
}
