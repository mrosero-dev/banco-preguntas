package co.edu.unicauca.question.domain.repository;

import java.util.Optional;

import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionStatus;

/**
 * Defines the contract for storing and retrieving questions.
 */
public interface QuestionRepository {

    Question save(Question question);

    Optional<Question> findById(Long id);

    QuestionPage findByAuthor(
            Long authorId,
            QuestionStatus status,
            String difficulty,
            String category,
            String search,
            int page,
            int size
    );
}