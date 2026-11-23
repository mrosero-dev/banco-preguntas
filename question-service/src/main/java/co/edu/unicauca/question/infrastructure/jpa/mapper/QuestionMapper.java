
package co.edu.unicauca.question.infrastructure.jpa.mapper;

import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;

import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;
import co.edu.unicauca.question.domain.model.QuestionVersion;
import co.edu.unicauca.question.infrastructure.jpa.record.QuestionVersionRecord;
import co.edu.unicauca.question.domain.model.QuestionStatus;
import co.edu.unicauca.question.domain.state.ApprovedState;
import co.edu.unicauca.question.domain.state.DraftState;
import co.edu.unicauca.question.domain.state.InReviewState;
import co.edu.unicauca.question.domain.state.PendingReviewState;
import co.edu.unicauca.question.domain.state.QuestionState;
import co.edu.unicauca.question.domain.state.RejectedState;
import co.edu.unicauca.question.infrastructure.jpa.record.QuestionOptionRecord;
import co.edu.unicauca.question.infrastructure.jpa.record.QuestionRecord;

/**
 * Maps questions between domain objects and JPA records.
 */
@Mapper(componentModel = "spring")
public interface QuestionMapper {

    /**
     * Converts a domain question into a JPA record.
     */

    @org.mapstruct.Mapping(target = "versionSnapshots", ignore = true)
    QuestionRecord toRecord(Question question);

    /**
    * Updates an existing JPA record with domain question data.
    */
    @org.mapstruct.Mapping(target = "id", ignore = true)
    @org.mapstruct.Mapping(target = "options", ignore = true)
    @org.mapstruct.Mapping(target = "versionSnapshots", ignore = true)
    void updateRecord(
            Question question,
            @org.mapstruct.MappingTarget QuestionRecord record
    );

    /**
     * Converts a domain option into a JPA record.
     */
    @Mapping(target = "question", ignore = true)
    QuestionOptionRecord toRecordOption(QuestionOption option);

    /**
     * Restores the bidirectional relationship between the question
     * record and its option records.
     */
    @AfterMapping
    default void linkOptions(@MappingTarget QuestionRecord record) {
        if (record.getOptions() != null) {
            record.getOptions().forEach(
                    option -> option.setQuestion(record)
            );
        }
    }

    /**
     * Reconstructs a domain question from a JPA record.
     */
    default Question toDomain(QuestionRecord record) {
        if (record == null) {
            throw new IllegalArgumentException(
                    "Question record cannot be null"
            );
        }

        List<QuestionOption> options = record.getOptions()
                .stream()
                .map(this::toDomainOption)
                .toList();

        return Question.restore(
                record.getId(),
                record.getAuthorId(),
                record.getStatement(),
                record.getContext(),
                record.getDifficulty(),
                record.getCategory(),
                record.getTopic(),
                record.getSubtopic(),
                record.getCompetence(),
                record.getJustification(),
                record.getSource(),
                statusToState(record.getStatus()),
                options,
                record.getVersion(),
                record.getVersionSnapshots().stream()
                        .map(this::toDomainVersion)
                        .toList()
        );
    }

    /**
     * Converts a domain version snapshot into a JPA record.
     */
    default QuestionVersionRecord toRecordVersion(QuestionVersion version) {
        QuestionVersionRecord record = new QuestionVersionRecord();
        record.setVersionNumber(version.getVersionNumber());
        record.setStatement(version.getStatement());
        record.setContext(version.getContext());
        record.setDifficulty(version.getDifficulty());
        record.setCategory(version.getCategory());
        record.setTopic(version.getTopic());
        record.setSubtopic(version.getSubtopic());
        record.setCompetence(version.getCompetence());
        record.setJustification(version.getJustification());
        record.setSource(version.getSource());
        record.setCreatedAt(version.getCreatedAt());
        version.getOptions().forEach(option -> record.getOptions().add(
                new QuestionVersionRecord.OptionSnapshot(
                        option.text(), option.correct())));
        return record;
    }

    /**
     * Converts a persisted version snapshot into its domain object.
     */
    default QuestionVersion toDomainVersion(QuestionVersionRecord record) {
        return new QuestionVersion(
                record.getVersionNumber(),
                record.getStatement(),
                record.getContext(),
                record.getDifficulty(),
                record.getCategory(),
                record.getTopic(),
                record.getSubtopic(),
                record.getCompetence(),
                record.getJustification(),
                record.getSource(),
                record.getOptions().stream()
                        .map(option -> new QuestionVersion.Option(
                                option.getText(), option.isCorrect()))
                        .toList(),
                record.getCreatedAt()
        );
    }

    /**
     * Converts a persisted option record into a domain option.
     */
    default QuestionOption toDomainOption(QuestionOptionRecord record) {
        if (record == null) {
            throw new IllegalArgumentException(
                    "Question option record cannot be null"
            );
        }

        QuestionOption option = new QuestionOption(
                record.getText(),
                record.isCorrect()
        );

        option.restoreId(record.getId());

        return option;
    }

    /**
     * Converts a persisted status into its corresponding domain state.
     */
    default QuestionState statusToState(QuestionStatus status) {
        if (status == null) {
            throw new IllegalArgumentException(
                    "Question status cannot be null"
            );
        }

        return switch (status) {
            case DRAFT -> new DraftState();
            case PENDING_REVIEW -> new PendingReviewState();
            case IN_REVIEW -> new InReviewState();
            case APPROVED -> new ApprovedState();
            case REJECTED -> new RejectedState();
        };
    }
}
