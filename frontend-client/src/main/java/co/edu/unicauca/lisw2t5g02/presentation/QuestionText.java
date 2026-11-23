package co.edu.unicauca.lisw2t5g02.presentation;

import co.edu.unicauca.lisw2t5g02.model.Question;
import co.edu.unicauca.lisw2t5g02.model.QuestionOption;
import co.edu.unicauca.lisw2t5g02.model.Review;
import co.edu.unicauca.lisw2t5g02.model.ReviewAssignment;

/** Plain-text rendering of a question and of its reviews. */
public final class QuestionText {

    private QuestionText() {
    }

    public static String format(Question q) {

        StringBuilder text = new StringBuilder();

        text.append("Estado: ").append(q.status().getLabel())
                .append("\n\nContexto:\n").append(q.context())
                .append("\n\nPregunta:\n").append(q.statement())
                .append("\n\nOpciones:\n");

        char letter = 'A';
        for (QuestionOption option : q.options()) {
            text.append(letter++).append(") ").append(option.text())
                    .append(option.correct() ? "   [correcta]" : "")
                    .append('\n');
        }

        text.append("\nJustificación:\n").append(q.justification())
                .append("\n\nBibliografía:\n").append(q.source())
                .append("\n\nCategoría: ").append(q.category())
                .append("\nCompetencia: ").append(q.competence())
                .append("\nTema: ").append(q.topic())
                .append("\nSubtema: ").append(q.subtopic())
                .append("\nNivel de dificultad: ").append(q.difficulty());

        return text.toString();
    }

    public static String formatReviews(ReviewAssignment assignment) {

        StringBuilder text = new StringBuilder("\n\n--- Evaluaciones ---");

        for (Review review : assignment.reviews()) {
            text.append("\nRevisor #").append(review.reviewerId())
                    .append(": ").append(review.status().getLabel());

            if (!review.observations().isBlank()) {
                text.append("\n   Observaciones: ")
                        .append(review.observations());
            }
        }

        return text.toString();
    }
}
