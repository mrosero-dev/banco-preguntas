
package co.edu.unicauca.question.domain.builder;

import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;

import java.util.List;
import java.util.function.Supplier;

/**
 * Directs the construction of a question.
 */
public class QuestionDirector {

    private final Supplier<QuestionBuilder> builderFactory;

    // Constructor compatible con las pruebas existentes.
    public QuestionDirector(QuestionBuilder builder) {
        if (builder == null) {
            throw new IllegalArgumentException(
                    "Question builder cannot be null"
            );
        }

        this.builderFactory = () -> builder;
    }

    private QuestionDirector(Supplier<QuestionBuilder> builderFactory) {
        if (builderFactory == null) {
            throw new IllegalArgumentException(
                    "Question builder factory cannot be null"
            );
        }

        this.builderFactory = builderFactory;
    }

    // Crea un Director que obtiene un Builder nuevo en cada construcción.
    public static QuestionDirector using(Supplier<QuestionBuilder> builderFactory) {
        return new QuestionDirector(builderFactory);
    }

    public Question constructQuestion(
            Long authorId,
            String statement,
            String context,
            String difficulty,
            String category,
            String topic,
            String subtopic,
            String competence,
            String justification,
            String source,
            List<QuestionOption> options) {

        if (options == null) {
            throw new IllegalArgumentException(
                    "Question options cannot be null"
            );
        }

        QuestionBuilder builder = builderFactory.get();

        if (builder == null) {
            throw new IllegalStateException(
                    "Question builder cannot be null"
            );
        }

        builder.buildAuthor(authorId);
        builder.buildStatement(statement);
        builder.buildContext(context);
        builder.buildDifficulty(difficulty);
        builder.buildCategory(category);
        builder.buildTopic(topic);
        builder.buildSubtopic(subtopic);
        builder.buildCompetence(competence);
        builder.buildJustification(justification);
        builder.buildSource(source);

        for (QuestionOption option : options) {
            builder.addOption(option.getText(), option.isCorrect());
        }

        builder.buildOptions();

        return builder.build();
    }
}
