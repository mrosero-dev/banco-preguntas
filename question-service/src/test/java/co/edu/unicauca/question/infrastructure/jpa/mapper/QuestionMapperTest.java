
package co.edu.unicauca.question.infrastructure.jpa.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;
import co.edu.unicauca.question.domain.model.QuestionStatus;
import co.edu.unicauca.question.infrastructure.jpa.record.QuestionOptionRecord;
import co.edu.unicauca.question.infrastructure.jpa.record.QuestionRecord;

class QuestionMapperTest {

    private QuestionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(QuestionMapper.class);
    }

    @Test
    void shouldMapDomainQuestionToRecord() {

        Question question = createQuestion();

        QuestionRecord record = mapper.toRecord(question);

        assertNotNull(record);

        assertEquals(question.getAuthorId(), record.getAuthorId());
        assertEquals(question.getStatement(), record.getStatement());
        assertEquals(question.getContext(), record.getContext());
        assertEquals(question.getDifficulty(), record.getDifficulty());
        assertEquals(question.getCategory(), record.getCategory());
        assertEquals(question.getTopic(), record.getTopic());
        assertEquals(question.getSubtopic(), record.getSubtopic());
        assertEquals(question.getCompetence(), record.getCompetence());
        assertEquals(question.getJustification(), record.getJustification());
        assertEquals(question.getSource(), record.getSource());
        assertEquals(question.getStatus(), record.getStatus());

        assertNotNull(record.getOptions());
        assertEquals(5, record.getOptions().size());

        assertEquals(
                1,
                record.getOptions().stream()
                        .filter(QuestionOptionRecord::isCorrect)
                        .count()
        );

        for (QuestionOptionRecord option : record.getOptions()) {
            assertSame(record, option.getQuestion());
        }
    }

    @Test
    void shouldRestoreDomainQuestionFromRecord() throws Exception {

        QuestionRecord record = createRecord();

        setId(record, 1L);

        Question question = mapper.toDomain(record);

        assertNotNull(question);

        assertEquals(1L, question.getId());
        assertEquals(10L, question.getAuthorId());
        assertEquals("Pregunta de prueba", question.getStatement());
        assertEquals("Contexto de prueba", question.getContext());
        assertEquals("MEDIA", question.getDifficulty());
        assertEquals("LECTURA", question.getCategory());
        assertEquals("Comprensión", question.getTopic());
        assertEquals("Textos", question.getSubtopic());
        assertEquals("Interpretativa", question.getCompetence());
        assertEquals("Justificación", question.getJustification());
        assertEquals("Fuente de prueba", question.getSource());

        assertEquals(QuestionStatus.DRAFT, question.getStatus());

        assertEquals(5, question.getOptions().size());
        assertEquals(101L, question.getOptions().get(0).getId());
        assertEquals("Opción A", question.getOptions().get(0).getText());
        assertTrue(question.getOptions().get(0).isCorrect());
    }

    @Test
    void shouldRestoreEveryQuestionStatus() throws Exception {

        QuestionStatus[] statuses = QuestionStatus.values();

        for (QuestionStatus status : statuses) {

            QuestionRecord record = createRecord();
            setId(record, 1L);
            record.setStatus(status);

            Question question = mapper.toDomain(record);

            assertEquals(status, question.getStatus());
        }
    }

    @Test
    void shouldRejectNullQuestionRecord() {

        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.toDomain(null)
        );
    }

    @Test
    void shouldRejectNullQuestionStatus() {

        assertThrows(
                IllegalArgumentException.class,
                () -> mapper.statusToState(null)
        );
    }

    private Question createQuestion() {

        Question question = new Question(
                10L,
                "Pregunta de prueba",
                "Contexto de prueba",
                "MEDIA",
                "LECTURA",
                "Comprensión",
                "Textos",
                "Interpretativa",
                "Justificación",
                "Fuente de prueba"
        );

        question.addOption(new QuestionOption("Opción A", true));
        question.addOption(new QuestionOption("Opción B", false));
        question.addOption(new QuestionOption("Opción C", false));
        question.addOption(new QuestionOption("Opción D", false));
        question.addOption(new QuestionOption("Opción E", false));

        return question;
    }

    private QuestionRecord createRecord() {

        QuestionRecord record = new QuestionRecord();

        record.setAuthorId(10L);
        record.setStatement("Pregunta de prueba");
        record.setContext("Contexto de prueba");
        record.setDifficulty("MEDIA");
        record.setCategory("LECTURA");
        record.setTopic("Comprensión");
        record.setSubtopic("Textos");
        record.setCompetence("Interpretativa");
        record.setJustification("Justificación");
        record.setSource("Fuente de prueba");
        record.setStatus(QuestionStatus.DRAFT);

        record.addOption(createOption(101L, "Opción A", true));
        record.addOption(createOption(102L, "Opción B", false));
        record.addOption(createOption(103L, "Opción C", false));
        record.addOption(createOption(104L, "Opción D", false));
        record.addOption(createOption(105L, "Opción E", false));

        return record;
    }

    private QuestionOptionRecord createOption(
            Long id,
            String text,
            boolean correct
    ) {

        QuestionOptionRecord option = new QuestionOptionRecord();

        setId(option, id);
        option.setText(text);
        option.setCorrect(correct);

        return option;
    }

    private void setId(Object object, Long id) {

        try {
            Field field = object.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(object, id);

        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException(exception);
        }
    }
}
