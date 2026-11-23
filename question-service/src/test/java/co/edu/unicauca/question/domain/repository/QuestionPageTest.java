package co.edu.unicauca.question.domain.repository;

import co.edu.unicauca.question.domain.model.Question;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class QuestionPageTest {

    @Test
    void shouldExposePaginationData() {
        Question q = mock(Question.class);
        QuestionPage page = new QuestionPage(List.of(q), 0, 10, 1L, 1, true, true);

        assertEquals(List.of(q), page.content());
        assertEquals(0, page.page());
        assertEquals(10, page.size());
        assertEquals(1L, page.totalElements());
        assertEquals(1, page.totalPages());
        assertTrue(page.first());
        assertTrue(page.last());
    }

    @Test
    void shouldCopyContentDefensively() {
        List<Question> source = new ArrayList<>(List.of(mock(Question.class)));
        QuestionPage page = new QuestionPage(source, 0, 10, 1L, 1, true, true);

        source.clear();

        assertEquals(1, page.content().size());
        assertThrows(UnsupportedOperationException.class, () -> page.content().add(mock(Question.class)));
    }

    @Test
    void shouldRejectNullContent() {
        assertThrows(NullPointerException.class,
                () -> new QuestionPage(null, 0, 10, 0L, 0, true, true));
    }
}
