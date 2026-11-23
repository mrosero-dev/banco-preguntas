package co.edu.unicauca.question.domain.builder;

import java.util.ArrayList;
import java.util.List;

import co.edu.unicauca.question.domain.exception.InvalidQuestionException;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;

/**
 * Builds single-choice questions.
 */
public class SingleChoiceQuestionBuilder extends QuestionBuilder {

    private Long authorId;
    private String statement;
    private String context;
    private String difficulty;
    private String category;
    private String topic;
    private String subtopic;
    private String competence;
    private String justification;
    private String source;

    private final List<QuestionOption> options = new ArrayList<>();

    @Override
    public void buildAuthor(Long authorId) { this.authorId = authorId; }

    @Override
    public void buildStatement(String statement) { this.statement = statement; }

    @Override
    public void buildContext(String context) { this.context = context; }

    @Override
    public void buildDifficulty(String difficulty) { this.difficulty = difficulty; }

    @Override
    public void buildCategory(String category) { this.category = category; }

    @Override
    public void buildTopic(String topic) { this.topic = topic; }

    @Override
    public void buildSubtopic(String subtopic) { this.subtopic = subtopic; }

    @Override
    public void buildCompetence(String competence) { this.competence = competence; }

    @Override
    public void buildJustification(String justification) { this.justification = justification; }

    @Override
    public void buildSource(String source) { this.source = source; }

    @Override
    public void addOption(String text, boolean correct) {
        options.add(new QuestionOption(text, correct));
    }

    @Override
    public void buildOptions() {
        if (options.size() != 5) {
            throw new InvalidQuestionException(
                    "A single-choice question must have exactly five options"
            );
        }

        long correctAnswers = options.stream()
                .filter(QuestionOption::isCorrect)
                .count();

        if (correctAnswers != 1) {
            throw new InvalidQuestionException(
                    "A single-choice question must have exactly one correct answer"
            );
        }
    }

   @Override
    public Question build() {
        buildOptions();

        Question question = new Question(
                authorId, statement, context, difficulty, category,
                topic, subtopic, competence, justification, source
        );

        for (QuestionOption option : options) {
            question.addOption(option);
        }

        reset();

        return question;
    }

    private void reset() {
        authorId = null;
        statement = null;
        context = null;
        difficulty = null;
        category = null;
        topic = null;
        subtopic = null;
        competence = null;
        justification = null;
        source = null;
        options.clear();
    }
}