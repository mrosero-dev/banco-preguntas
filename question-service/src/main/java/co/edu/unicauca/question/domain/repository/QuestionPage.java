package co.edu.unicauca.question.domain.repository;

import java.util.List;

import co.edu.unicauca.question.domain.model.Question;

/**
 * Represents a paginated collection of questions.
 */
public record QuestionPage(
        List<Question> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public QuestionPage {
        content = List.copyOf(content);
    }
}