
package co.edu.unicauca.question.application;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unicauca.question.domain.builder.QuestionDirector;
import co.edu.unicauca.question.domain.exception.InvalidQuestionException;
import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;
import co.edu.unicauca.question.domain.model.QuestionStatus;
import co.edu.unicauca.question.domain.repository.QuestionRepository;
import co.edu.unicauca.question.domain.validation.QuestionValidationTemplate;
import co.edu.unicauca.question.application.port.QuestionEventPort;

@ExtendWith(MockitoExtension.class)
class QuestionEditingServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private QuestionValidationTemplate questionValidator;

    @Mock
    private QuestionDirector questionDirector;

    @Mock
    private QuestionEventPort questionEventPublisher;

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

    private Question createQuestion() {
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

        return question;
    }

    private List<QuestionOption> updatedOptions() {
        return List.of(
                new QuestionOption("Six", false),
                new QuestionOption("Seven", false),
                new QuestionOption("Eight", true),
                new QuestionOption("Nine", false),
                new QuestionOption("Ten", false)
        );
    }

    private Question executeUpdate(Long questionId, Long authorId) {
        return questionService.updateQuestion(
                questionId,
                authorId,
                "Updated statement",
                "Updated context",
                "MEDIA",
                "MATEMATICAS",
                "ARITMETICA",
                "MULTIPLICACION",
                "RAZONAMIENTO",
                "Updated justification",
                "Updated source",
                updatedOptions()
        );
    }

    @Test
    void shouldUpdateAndSaveQuestion() {
        Question question = createQuestion();

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        when(questionRepository.save(question))
                .thenReturn(question);

        Question result = executeUpdate(1L, 10L);

        assertSame(question, result);
        assertEquals("Updated statement", result.getStatement());
        assertEquals(QuestionStatus.DRAFT, result.getStatus());
        assertEquals("Eight", result.getOptions().get(2).getText());

        verify(questionValidator).validate(question);
        verify(questionRepository).save(question);
    }

    @Test
    void shouldRejectUpdateWhenAuthorDoesNotMatch() {
        Question question = createQuestion();

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        assertThrows(
                IllegalArgumentException.class,
                () -> executeUpdate(1L, 99L)
        );

        assertEquals("Original statement", question.getStatement());
        verify(questionRepository, never()).save(any());
        verifyNoInteractions(questionValidator);
    }

    @Test
    void shouldRejectUpdateWhenQuestionDoesNotExist() {
        when(questionRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> executeUpdate(99L, 10L)
        );

        verify(questionRepository, never()).save(any());
    }

    @Test
    void shouldRejectUpdateWhenQuestionIdIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> executeUpdate(null, 10L)
        );

        verify(questionRepository, never()).findById(any());
    }

    @Test
    void shouldRejectUpdateWhenAuthorIdIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> executeUpdate(1L, null)
        );

        verify(questionRepository, never()).findById(any());
    }

    @Test
    void shouldRejectUpdateWhenQuestionIsNotDraft() {
        Question question = createQuestion();
        question.submitForReview();

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        assertThrows(
                QuestionStateConflictException.class,
                () -> executeUpdate(1L, 10L)
        );

        verify(questionRepository, never()).save(any());
        verifyNoInteractions(questionValidator);
    }

    @Test
    void shouldNotSaveWhenQuestionValidationFails() {
        Question question = createQuestion();

        when(questionRepository.findById(1L))
                .thenReturn(Optional.of(question));

        doThrow(new InvalidQuestionException("Invalid question"))
                .when(questionValidator).validate(question);

        assertThrows(
                InvalidQuestionException.class,
                () -> executeUpdate(1L, 10L)
        );

        verify(questionValidator).validate(question);
        verify(questionRepository, never()).save(any());
    }
}
