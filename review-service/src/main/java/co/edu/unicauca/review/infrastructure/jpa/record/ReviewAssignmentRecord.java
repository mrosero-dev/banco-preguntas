package co.edu.unicauca.review.infrastructure.jpa.record;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "review_assignments")
public class ReviewAssignmentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long questionId;

    private Long authorId;

    private int currentRound = 1;

    @OneToMany(
            mappedBy = "assignment",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ReviewRecord> reviews = new ArrayList<>();

    public ReviewAssignmentRecord() {
    }

    public Long getId() {
        return id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public int getCurrentRound() { return currentRound; }

    public void setCurrentRound(int currentRound) { this.currentRound = currentRound; }

    public List<ReviewRecord> getReviews() {
        return reviews;
    }

    public void addReview(ReviewRecord review) {
        reviews.add(review);
        review.setAssignment(this);
    }
}