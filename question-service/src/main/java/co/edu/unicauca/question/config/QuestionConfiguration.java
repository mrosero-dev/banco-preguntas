
package co.edu.unicauca.question.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.unicauca.question.domain.builder.QuestionDirector;
import co.edu.unicauca.question.domain.builder.SingleChoiceQuestionBuilder;
import co.edu.unicauca.question.domain.validation.SingleChoiceQuestionValidator;

@Configuration
public class QuestionConfiguration {

    @Bean
    public SingleChoiceQuestionValidator singleChoiceQuestionValidator() {
        return new SingleChoiceQuestionValidator();
    }

    @Bean
    public QuestionDirector questionDirector() {
        return QuestionDirector.using(SingleChoiceQuestionBuilder::new);
    }
}
