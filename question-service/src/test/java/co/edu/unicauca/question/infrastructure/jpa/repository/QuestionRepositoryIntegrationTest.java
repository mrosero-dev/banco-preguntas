
package co.edu.unicauca.question.infrastructure.jpa.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;
import co.edu.unicauca.question.infrastructure.jpa.mapper.QuestionMapperImpl;

@DataJpaTest
@Import({
        QuestionRepositoryAdapter.class,
        QuestionMapperImpl.class
})
class QuestionEditingIntegrationTest {

    @Autowired
    private QuestionRepositoryAdapter repository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void shouldPersistUpdatedQuestionAndReplaceItsOptions() {
        Question question = new Question(
                10L,
                "Original statement",
                "Original context",
                "FACIL",
                "MATEMATICAS",
                "ARITMETICA",
                "SUMA",
                "RAZONAMIENTO",
                "Original justification",
                "Original source"
        );

        question.addOption(new QuestionOption("One", true));
        question.addOption(new QuestionOption("Two", false));
        question.addOption(new QuestionOption("Three", false));
        question.addOption(new QuestionOption("Four", false));
        question.addOption(new QuestionOption("Five", false));

        Question saved = repository.save(question);

        saved.updateDetails(
                "Updated statement",
                "Updated context",
                "MEDIA",
                "MATEMATICAS",
                "ARITMETICA",
                "MULTIPLICACION",
                "RAZONAMIENTO",
                "Updated justification",
                "Updated source",
                List.of(
                        new QuestionOption("Six", false),
                        new QuestionOption("Seven", false),
                        new QuestionOption("Eight", true),
                        new QuestionOption("Nine", false),
                        new QuestionOption("Ten", false)
                )
        );

        Question updated = repository.save(saved);

        entityManager.flush();
        entityManager.clear();

        Question retrieved = repository.findById(updated.getId())
                .orElseThrow();

        assertEquals("Updated statement", retrieved.getStatement());
        assertEquals("Updated context", retrieved.getContext());
        assertEquals("MEDIA", retrieved.getDifficulty());
        assertEquals("MULTIPLICACION", retrieved.getSubtopic());
        assertEquals("Updated justification", retrieved.getJustification());
        assertEquals("Updated source", retrieved.getSource());

        assertEquals(10L, retrieved.getAuthorId());
        assertEquals(5, retrieved.getOptions().size());

        assertEquals(
                1,
                retrieved.getOptions().stream()
                        .filter(QuestionOption::isCorrect)
                        .count()
        );

        assertEquals(
                List.of("Six", "Seven", "Eight", "Nine", "Ten"),
                retrieved.getOptions().stream()
                        .map(QuestionOption::getText)
                        .toList()
        );
    }
}
