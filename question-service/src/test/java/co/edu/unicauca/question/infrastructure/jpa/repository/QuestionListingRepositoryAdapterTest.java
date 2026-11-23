package co.edu.unicauca.question.infrastructure.jpa.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionStatus;
import co.edu.unicauca.question.infrastructure.jpa.mapper.QuestionMapper;
import co.edu.unicauca.question.infrastructure.jpa.record.QuestionRecord;

@ExtendWith(MockitoExtension.class)
class QuestionListingRepositoryAdapterTest {

    @Mock
    private QuestionJpaRepository jpaRepository;

    @Mock
    private QuestionMapper mapper;

    private QuestionRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new QuestionRepositoryAdapter(
                jpaRepository,
                mapper
        );
    }

    @Test
    void shouldReturnPaginatedQuestionsForAuthor() {

        QuestionRecord record = new QuestionRecord();
        Question question = new Question(
                10L,
                "Question statement",
                "Question context",
                "FACIL",
                "MATEMATICAS",
                "ARITMETICA",
                "SUMA",
                "RAZONAMIENTO",
                "Question justification",
                "Question source"
        );

        PageImpl<QuestionRecord> databasePage = new PageImpl<>(
                List.of(record),
                PageRequest.of(0, 10),
                1
        );

        when(jpaRepository.findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                any(Pageable.class)
        )).thenReturn(databasePage);

        when(mapper.toDomain(record)).thenReturn(question);

        var result = adapter.findByAuthor(
                10L,
                QuestionStatus.DRAFT,
                "FACIL",
                "MATEMATICAS",
                "suma",
                0,
                10
        );

        assertEquals(1, result.content().size());
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(question, result.content().get(0));

        verify(mapper).toDomain(record);
        verify(jpaRepository).findAll(
                any(org.springframework.data.jpa.domain.Specification.class),
                any(Pageable.class)
        );
    }
}