package co.edu.unicauca.question.domain.builder;

import co.edu.unicauca.question.domain.exception.InvalidQuestionException;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class QuestionDirectorTest {

    private static List<QuestionOption> options(int total, int correct) {
        List<QuestionOption> list = new ArrayList<>();
        for (int i = 0; i < total; i++) {
            list.add(new QuestionOption("Option " + i, i < correct));
        }
        return list;
    }

    private static Question construct(QuestionDirector director, List<QuestionOption> options) {
        return director.constructQuestion(1L, "Statement", "Context", "EASY", "COMPUTING",
                "Topic", "Subtopic", "Competence", "Justification", "Source", options);
    }

    @Test
    void shouldRejectNullBuilder() {
        assertThrows(IllegalArgumentException.class, () -> new QuestionDirector(null));
    }

    @Test
    void shouldRejectNullOptions() {
        QuestionDirector director = new QuestionDirector(new SingleChoiceQuestionBuilder());
        assertThrows(IllegalArgumentException.class, () -> construct(director, null));
    }

    @Test
    void shouldInvokeBuilderStepsInOrder() {
        QuestionBuilder builder = mock(QuestionBuilder.class);
        Question expected = mock(Question.class);
        when(builder.build()).thenReturn(expected);

        Question result = construct(new QuestionDirector(builder), options(2, 1));

        assertSame(expected, result);
        InOrder order = inOrder(builder);
        order.verify(builder).buildAuthor(1L);
        order.verify(builder).buildStatement("Statement");
        order.verify(builder).buildContext("Context");
        order.verify(builder).buildDifficulty("EASY");
        order.verify(builder).buildCategory("COMPUTING");
        order.verify(builder).buildTopic("Topic");
        order.verify(builder).buildSubtopic("Subtopic");
        order.verify(builder).buildCompetence("Competence");
        order.verify(builder).buildJustification("Justification");
        order.verify(builder).buildSource("Source");
        order.verify(builder, times(2)).addOption(anyString(), anyBoolean());
        order.verify(builder).buildOptions();
        order.verify(builder).build();
    }

    @Test
    void shouldFailWhenQuestionDoesNotHaveFiveOptions() {
        QuestionDirector director = new QuestionDirector(new SingleChoiceQuestionBuilder());
        assertThrows(InvalidQuestionException.class, () -> construct(director, options(4, 1)));
    }

    @Test
    void shouldFailWhenThereIsNoCorrectOption() {
        QuestionDirector director = new QuestionDirector(new SingleChoiceQuestionBuilder());
        assertThrows(InvalidQuestionException.class, () -> construct(director, options(5, 0)));
    }

    @Test
    void shouldFailWhenThereIsMoreThanOneCorrectOption() {
        QuestionDirector director = new QuestionDirector(new SingleChoiceQuestionBuilder());
        assertThrows(InvalidQuestionException.class, () -> construct(director, options(5, 2)));
    }
}
