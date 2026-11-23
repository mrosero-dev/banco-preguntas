package co.edu.unicauca.question.domain.builder;

import co.edu.unicauca.question.domain.model.Question;

/**
 * Defines the steps required to build a question.
 */
public abstract class QuestionBuilder {

    public abstract void buildAuthor(Long authorId);

    public abstract void buildStatement(String statement);

    public abstract void buildContext(String context);

    public abstract void buildDifficulty(String difficulty);

    public abstract void buildCategory(String category);

    public abstract void buildTopic(String topic);

    public abstract void buildSubtopic(String subtopic);

    public abstract void buildCompetence(String competence);

    public abstract void buildJustification(String justification);

    public abstract void buildSource(String source);

    public abstract void buildOptions();

    public abstract void addOption(String text, boolean correct);

    public abstract Question build();
}