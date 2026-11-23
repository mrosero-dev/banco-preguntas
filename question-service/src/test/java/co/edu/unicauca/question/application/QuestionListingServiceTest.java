package co.edu.unicauca.question.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unicauca.question.domain.builder.QuestionDirector;
import co.edu.unicauca.question.domain.model.QuestionStatus;
import co.edu.unicauca.question.domain.repository.QuestionPage;
import co.edu.unicauca.question.domain.repository.QuestionRepository;
import co.edu.unicauca.question.domain.validation.QuestionValidationTemplate;
import co.edu.unicauca.question.infrastructure.messaging.QuestionEventPublisher;

@ExtendWith(MockitoExtension.class)
class QuestionListingServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionValidationTemplate questionValidator;

    @Mock
    private QuestionDirector questionDirector;

    @Mock
    private QuestionEventPublisher questionEventPublisher;

    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        questionService = new QuestionService(
                questionRepository,
                questionValidator,
                questionDirector,
                questionEventPublisher
        );
    }

    @Test
    void shouldFindQuestionsByAuthor() {

        QuestionPage expected = new QuestionPage(
                List.of(),
                0,
                10,
                0,
                0,
                true,
                true
        );

        when(questionRepository.findByAuthor(
                10L,
                QuestionStatus.DRAFT,
                "FACIL",
                "MATEMATICAS",
                "suma",
                0,
                10
        )).thenReturn(expected);

        QuestionPage result = questionService.findQuestionsByAuthor(
                10L,
                QuestionStatus.DRAFT,
                "FACIL",
                "MATEMATICAS",
                "suma",
                0,
                10
        );

        assertSame(expected, result);

        verify(questionRepository).findByAuthor(
                10L,
                QuestionStatus.DRAFT,
                "FACIL",
                "MATEMATICAS",
                "suma",
                0,
                10
        );
    }

    @Test
    void shouldRejectNullAuthorId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> questionService.findQuestionsByAuthor(
                        null, null, null, null, null, 0, 10
                )
        );
    }

    @Test
    void shouldRejectNonPositiveAuthorId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> questionService.findQuestionsByAuthor(
                        0L, null, null, null, null, 0, 10
                )
        );
    }

    @Test
    void shouldRejectNegativePage() {

        assertThrows(
                IllegalArgumentException.class,
                () -> questionService.findQuestionsByAuthor(
                        10L, null, null, null, null, -1, 10
                )
        );
    }

    @Test
    void shouldRejectInvalidPageSize() {

        assertThrows(
                IllegalArgumentException.class,
                () -> questionService.findQuestionsByAuthor(
                        10L, null, null, null, null, 0, 101
                )
        );
    }
}