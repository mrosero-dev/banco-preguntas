package co.edu.unicauca.lisw2t5g02.model;

import java.util.List;

/** Question as exposed by the Question Service REST API. */
public record Question(
        Long id,
        long authorId,
        String statement,
        String context,
        String difficulty,
        String category,
        String topic,
        String subtopic,
        String competence,
        String justification,
        String source,
        QuestionStatus status,
        List<QuestionOption> options) {

    /** Short text used in lists and combo boxes. */
    public String title() {
        String text = statement == null ? "" : statement.replace('\n', ' ');
        String shortText = text.length() > 60 ? text.substring(0, 57) + "..." : text;
        return "#" + id + " - " + shortText;
    }

    /** Text of the correct option, or an empty string if none is marked. */
    public String correctText() {
        return options.stream()
                .filter(QuestionOption::correct)
                .map(QuestionOption::text)
                .findFirst()
                .orElse("");
    }

    @Override
    public String toString() {
        return title();
    }
}
