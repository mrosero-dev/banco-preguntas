package co.edu.unicauca.lisw2t5g02.model;

import java.util.List;

/** One page of questions. */
public record QuestionPage(
        List<Question> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last) {
}
