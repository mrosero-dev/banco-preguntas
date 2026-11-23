
package co.edu.unicauca.question.infrastructure.jpa.repository;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.infrastructure.jpa.mapper.QuestionMapper;
import co.edu.unicauca.question.infrastructure.jpa.record.QuestionRecord;

@ExtendWith(MockitoExtension.class)
class QuestionRepositoryAdapterTest {

    @Mock
    private QuestionJpaRepository jpaRepository;

    @Mock
    private QuestionMapper mapper;

    @InjectMocks
    private QuestionRepositoryAdapter adapter;

    private Question question;
    private QuestionRecord record;

    @BeforeEach
    void setUp() {
        question = new Question(
                1L,
                "¿Cuál es el resultado de 2 + 2?",
                "Contexto de prueba",
                "FACIL",
                "MATEMATICAS",
                "ARITMETICA",
                "OPERACIONES",
                "RAZONAMIENTO",
                "La suma de dos más dos es cuatro.",
                "Fuente de prueba"
        );

        record = new QuestionRecord();
        record.setAuthorId(1L);
        record.setStatement("¿Cuál es el resultado de 2 + 2?");
    }

    @Test
    void shouldSaveQuestion() {

        when(mapper.toRecord(question)).thenReturn(record);
        when(jpaRepository.save(record)).thenReturn(record);
        when(mapper.toDomain(record)).thenReturn(question);

        Question result = adapter.save(question);

        assertSame(question, result);

        verify(mapper).toRecord(question);
        verify(jpaRepository).save(record);
        verify(mapper).toDomain(record);
    }

    @Test
    void shouldFindQuestionById() {

        when(jpaRepository.findById(1L))
                .thenReturn(Optional.of(record));

        when(mapper.toDomain(record)).thenReturn(question);

        Optional<Question> result = adapter.findById(1L);

        assertTrue(result.isPresent());
        assertSame(question, result.get());

        verify(jpaRepository).findById(1L);
        verify(mapper).toDomain(record);
    }

    @Test
    void shouldReturnEmptyWhenQuestionDoesNotExist() {

        when(jpaRepository.findById(99L))
                .thenReturn(Optional.empty());

        Optional<Question> result = adapter.findById(99L);

        assertTrue(result.isEmpty());

        verify(jpaRepository).findById(99L);
    }

    @Test
    void shouldThrowExceptionWhenSavingNullQuestion() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adapter.save(null)
        );

        verifyNoInteractions(jpaRepository, mapper);
    }

    @Test
    void shouldThrowExceptionWhenFindingByNullId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adapter.findById(null)
        );

        verifyNoInteractions(jpaRepository, mapper);
    }
}
