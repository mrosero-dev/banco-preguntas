package co.edu.unicauca.question.api.dto;

import java.time.Instant;
import java.util.List;

import co.edu.unicauca.question.domain.model.QuestionVersion;

/**
 * Immutable copy of a previous version of a question (HU-2.3).
 */
public record QuestionVersionResponse(
        int versionNumber,
        String statement,
        String context,
        String difficulty,
        String category,
        String topic,
        String subtopic,
        String competence,
        String justification,
        String source,
        List<OptionResponse> options,
        Instant createdAt) {

    /** Option of a stored version. */
    public record OptionResponse(String text, boolean correct) {
    }

    public static QuestionVersionResponse from(QuestionVersion version) {
        return new QuestionVersionResponse(
                version.getVersionNumber(),
                version.getStatement(),
                version.getContext(),
                version.getDifficulty(),
                version.getCategory(),
                version.getTopic(),
                version.getSubtopic(),
                version.getCompetence(),
                version.getJustification(),
                version.getSource(),
                version.getOptions().stream()
                        .map(option -> new OptionResponse(
                                option.text(), option.correct()))
                        .toList(),
                version.getCreatedAt());
    }
}
