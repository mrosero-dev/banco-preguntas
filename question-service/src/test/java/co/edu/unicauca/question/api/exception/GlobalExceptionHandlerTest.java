
package co.edu.unicauca.question.api.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import co.edu.unicauca.question.domain.exception.InvalidQuestionException;
import co.edu.unicauca.question.domain.exception.QuestionStateConflictException;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders
                .standaloneSetup(new ExceptionTestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void shouldReturnBadRequestForIllegalArgumentException() throws Exception {

        mockMvc.perform(get("/test/illegal-argument"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid argument"));
    }

    @Test
    void shouldReturnBadRequestForInvalidQuestionException() throws Exception {

        mockMvc.perform(get("/test/invalid-question"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid question"));
    }

    @Test
    void shouldReturnConflictForQuestionStateConflictException() throws Exception {

        mockMvc.perform(get("/test/state-conflict"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Invalid state transition"));
    }

    @Test
    void shouldReturnBadRequestForMethodArgumentNotValidException() throws Exception {

        mockMvc.perform(post("/test/validation-error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"statement\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("statement: must not be blank"));
    }

    @RestController
    static class ExceptionTestController {

        @GetMapping("/test/illegal-argument")
        void illegalArgument() {
            throw new IllegalArgumentException("Invalid argument");
        }

        @GetMapping("/test/invalid-question")
        void invalidQuestion() {
            throw new InvalidQuestionException("Invalid question");
        }

        @GetMapping("/test/state-conflict")
        void stateConflict() {
            throw new QuestionStateConflictException("Invalid state transition");
        }

        @PostMapping("/test/validation-error")
        void validationError(@Valid @RequestBody TestRequest request) {
        }
    }

    static class TestRequest {

        @NotBlank
        private String statement;

        public String getStatement() {
            return statement;
        }

        public void setStatement(String statement) {
            this.statement = statement;
        }
    }
}
