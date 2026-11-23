
package co.edu.unicauca.question.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import co.edu.unicauca.question.api.dto.QuestionResponse;
import co.edu.unicauca.question.api.dto.UpdateQuestionRequest;
import co.edu.unicauca.question.api.dto.QuestionOptionRequest;
import co.edu.unicauca.question.api.mapper.QuestionRequestMapper;
import co.edu.unicauca.question.application.QuestionService;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;

@ExtendWith(MockitoExtension.class)
class QuestionEditingControllerTest {

    @Mock
    private QuestionService questionService;

    @Mock
    private QuestionRequestMapper questionRequestMapper;

    @InjectMocks
    private QuestionController questionController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private UpdateQuestionRequest request;
    private Question question;
    private QuestionResponse response;
    private List<QuestionOption> options;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(questionController)
                .build();

        objectMapper = new ObjectMapper();

        question = new Question(
                10L,
                "Updated statement",
                "Updated context",
                "MEDIA",
                "MATEMATICAS",
                "ARITMETICA",
                "MULTIPLICACION",
                "RAZONAMIENTO",
                "Updated justification",
                "Updated source"
        );

        options = List.of(
                new QuestionOption("Six", false),
                new QuestionOption("Seven", false),
                new QuestionOption("Eight", true),
                new QuestionOption("Nine", false),
                new QuestionOption("Ten", false)
        );

        request = new UpdateQuestionRequest();
        request.setStatement("Updated statement");
        request.setContext("Updated context");
        request.setDifficulty("MEDIA");
        request.setCategory("MATEMATICAS");
        request.setTopic("ARITMETICA");
        request.setSubtopic("MULTIPLICACION");
        request.setCompetence("RAZONAMIENTO");
        request.setJustification("Updated justification");
        request.setSource("Updated source");

        request.setOptions(List.of(
                createOptionRequest("Six", false),
                createOptionRequest("Seven", false),
                createOptionRequest("Eight", true),
                createOptionRequest("Nine", false),
                createOptionRequest("Ten", false)
        ));

        response = new QuestionResponse();
        response.setId(1L);
        response.setAuthorId(10L);
        response.setStatement("Updated statement");
        response.setStatus("DRAFT");
    }

    private QuestionOptionRequest createOptionRequest(
            String text,
            boolean correct) {

        QuestionOptionRequest option = new QuestionOptionRequest();
        option.setText(text);
        option.setCorrect(correct);

        return option;
    }

    @Test
    void shouldUpdateQuestionAndReturnOk() throws Exception {
        when(questionRequestMapper.toDomainOptions(any()))
                .thenReturn(options);

        when(questionService.updateQuestion(
                eq(1L),
                eq(10L),
                eq("Updated statement"),
                eq("Updated context"),
                eq("MEDIA"),
                eq("MATEMATICAS"),
                eq("ARITMETICA"),
                eq("MULTIPLICACION"),
                eq("RAZONAMIENTO"),
                eq("Updated justification"),
                eq("Updated source"),
                eq(options)
        )).thenReturn(question);

        when(questionRequestMapper.toResponse(question))
                .thenReturn(response);

        mockMvc.perform(put("/api/questions/1")
                        .param("authorId", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.statement").value("Updated statement"));

        verify(questionService).updateQuestion(
                1L,
                10L,
                "Updated statement",
                "Updated context",
                "MEDIA",
                "MATEMATICAS",
                "ARITMETICA",
                "MULTIPLICACION",
                "RAZONAMIENTO",
                "Updated justification",
                "Updated source",
                options
        );
    }

    @Test
    void shouldReturnBadRequestWhenAuthorIdIsMissing() throws Exception {
        mockMvc.perform(put("/api/questions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
