
package co.edu.unicauca.question.infrastructure.jpa.record;

import jakarta.persistence.*;

/**
 * JPA representation of a question option.
 */
@Entity
@Table(name = "question_options")
public class QuestionOptionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private boolean correct;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private QuestionRecord question;

    public QuestionOptionRecord() {
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

    public QuestionRecord getQuestion() {
        return question;
    }

    public void setQuestion(QuestionRecord question) {
        this.question = question;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}
