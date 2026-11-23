
package co.edu.unicauca.question.domain.state;

import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionStatus;
import co.edu.unicauca.question.domain.model.QuestionOption;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuestionStateTest {

    private Question createQuestion() {
        Question question = new Question(
                1L,
                "What is software engineering?",
                "Context of the question",
                "MEDIUM",
                "Engineering",
                "Software",
                "Requirements",
                "Problem solving",
                "Because of the engineering principles",
                "Reference source"
        );

        question.addOption(new QuestionOption("Option A", true));
        question.addOption(new QuestionOption("Option B", false));
        question.addOption(new QuestionOption("Option C", false));
        question.addOption(new QuestionOption("Option D", false));
        question.addOption(new QuestionOption("Option E", false));

        return question;
    }

    @Test
    void shouldStartInDraftState() {
        Question question = createQuestion();

        assertEquals(QuestionStatus.DRAFT, question.getStatus());
    }

    @Test
    void shouldSubmitDraftQuestionForReview() {
        Question question = createQuestion();

        question.submitForReview();

        assertEquals(QuestionStatus.PENDING_REVIEW, question.getStatus());
    }

    @Test
    void shouldStartReviewFromPendingReview() {
        Question question = createQuestion();

        question.submitForReview();
        question.startReview();

        assertEquals(QuestionStatus.IN_REVIEW, question.getStatus());
    }

    @Test
    void shouldApproveQuestionAfterReview() {
        Question question = createQuestion();

        question.submitForReview();
        question.startReview();
        question.approve();

        assertEquals(QuestionStatus.APPROVED, question.getStatus());
    }

    @Test
    void shouldRejectQuestionDuringReview() {
        Question question = createQuestion();

        question.submitForReview();
        question.startReview();
        question.reject();

        assertEquals(QuestionStatus.REJECTED, question.getStatus());
    }

    @Test
    void shouldReturnRejectedQuestionToDraft() {
        Question question = createQuestion();

        question.submitForReview();
        question.startReview();
        question.reject();
        question.returnToDraft();

        assertEquals(QuestionStatus.DRAFT, question.getStatus());
    }

    @Test
    void shouldNotApproveQuestionDirectlyFromDraft() {
        Question question = createQuestion();

        assertThrows(
                QuestionStateConflictException.class,
                question::approve
        );

        assertEquals(QuestionStatus.DRAFT, question.getStatus());
    }

    @Test
    void shouldNotRejectQuestionDirectlyFromDraft() {
        Question question = createQuestion();

        assertThrows(
                QuestionStateConflictException.class,
                question::reject
        );

        assertEquals(QuestionStatus.DRAFT, question.getStatus());
    }

    @Test
    void shouldNotStartReviewDirectlyFromDraft() {
        Question question = createQuestion();

        assertThrows(
                QuestionStateConflictException.class,
                question::startReview
        );

        assertEquals(QuestionStatus.DRAFT, question.getStatus());
    }

    @Test
    void shouldNotApproveQuestionFromPendingReview() {
        Question question = createQuestion();

        question.submitForReview();

        assertThrows(
                QuestionStateConflictException.class,
                question::approve
        );

        assertEquals(QuestionStatus.PENDING_REVIEW, question.getStatus());
    }

    @Test
    void shouldNotSubmitApprovedQuestionAgain() {
        Question question = createQuestion();

        question.submitForReview();
        question.startReview();
        question.approve();

        assertThrows(
                QuestionStateConflictException.class,
                question::submitForReview
        );

        assertEquals(QuestionStatus.APPROVED, question.getStatus());
    }

    @Test
    void shouldNotReturnApprovedQuestionToDraft() {
        Question question = createQuestion();

        question.submitForReview();
        question.startReview();
        question.approve();

        assertThrows(
                QuestionStateConflictException.class,
                question::returnToDraft
        );

        assertEquals(QuestionStatus.APPROVED, question.getStatus());
    }
}
