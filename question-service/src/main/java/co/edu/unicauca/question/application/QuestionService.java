package co.edu.unicauca.question.application;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import co.edu.unicauca.question.application.event.QuestionSubmittedEvent;
import co.edu.unicauca.question.application.port.QuestionEventPort;
import co.edu.unicauca.question.domain.builder.QuestionDirector;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;
import co.edu.unicauca.question.domain.model.QuestionStatus;
import co.edu.unicauca.question.domain.repository.QuestionPage;
import co.edu.unicauca.question.domain.repository.QuestionRepository;
import co.edu.unicauca.question.domain.validation.QuestionValidationTemplate;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionValidationTemplate questionValidator;
    private final QuestionDirector questionDirector;
    private final QuestionEventPort questionEventPublisher;

    public QuestionService(
            QuestionRepository questionRepository,
            QuestionValidationTemplate questionValidator,
            QuestionDirector questionDirector,
            QuestionEventPort questionEventPublisher) {

        this.questionRepository = questionRepository;
        this.questionValidator = questionValidator;
        this.questionDirector = questionDirector;
        this.questionEventPublisher = questionEventPublisher;
    }

    public Question createQuestion(
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
            List<QuestionOption> options) {

        Question question = questionDirector.constructQuestion(
                authorId,
                statement,
                context,
                difficulty,
                category,
                topic,
                subtopic,
                competence,
                justification,
                source,
                options
        );

        questionValidator.validate(question);

        return questionRepository.save(question);
    }

    public Question updateQuestion(
            Long questionId,
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
            List<QuestionOption> options) {

        if (questionId == null || authorId == null) {
            throw new IllegalArgumentException(
                    "Question ID and author ID cannot be null"
            );
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Question not found: " + questionId
                ));

        if (!question.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException(
                    "The author does not match the registered question"
            );
        }

        question.updateDetails(
                statement,
                context,
                difficulty,
                category,
                topic,
                subtopic,
                competence,
                justification,
                source,
                options
        );

        questionValidator.validate(question);

        return questionRepository.save(question);
    }

    public Question submitQuestionForReview(Question question) {

        questionValidator.validate(question);
        question.submitForReview();

        Question savedQuestion = questionRepository.save(question);

        QuestionSubmittedEvent event = new QuestionSubmittedEvent(
                savedQuestion.getId(),
                savedQuestion.getAuthorId()
        );

        questionEventPublisher.publishQuestionSubmitted(event);

        return savedQuestion;
    }

    public Question startReview(Long questionId, Long authorId) {

        if (questionId == null || authorId == null) {
            throw new IllegalArgumentException(
                    "Question ID and author ID cannot be null"
            );
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Question not found: " + questionId
                ));

        if (!question.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException(
                    "The author does not match the registered question"
            );
        }

        question.startReview();

        return questionRepository.save(question);
    }

    public Question completeReview(
            Long questionId,
            Long authorId,
            String result) {

        if (questionId == null || authorId == null || result == null) {
            throw new IllegalArgumentException(
                    "Question ID, author ID and result cannot be null"
            );
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Question not found: " + questionId
                ));

        if (!question.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException(
                    "The author does not match the registered question"
            );
        }

        switch (result) {
            case "APPROVED" -> question.approve();
            case "REJECTED" -> question.reject();
            default -> throw new IllegalArgumentException(
                    "Invalid review result: " + result
            );
        }

        return questionRepository.save(question);
    }

    public Question returnRejectedQuestionToDraft(
            Long questionId,
            Long authorId) {

        if (questionId == null || authorId == null) {
            throw new IllegalArgumentException(
                    "Question ID and author ID cannot be null"
            );
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Question not found: " + questionId
                ));

        if (!question.getAuthorId().equals(authorId)) {
            throw new IllegalArgumentException(
                    "The author does not match the registered question"
            );
        }

        question.returnToDraft();

        return questionRepository.save(question);
    }

    public Optional<Question> findQuestionById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Question ID cannot be null"
            );
        }

        return questionRepository.findById(id);
    }

    public QuestionPage findQuestionsByAuthor(
            Long authorId,
            QuestionStatus status,
            String difficulty,
            String category,
            String search,
            int page,
            int size) {

        if (authorId == null || authorId <= 0) {
            throw new IllegalArgumentException(
                    "Author ID must be greater than zero"
            );
        }

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }

        return questionRepository.findByAuthor(
                authorId,
                status,
                difficulty,
                category,
                search,
                page,
                size
        );
    }
}