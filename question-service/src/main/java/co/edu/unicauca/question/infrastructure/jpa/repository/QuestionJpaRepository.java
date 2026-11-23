package co.edu.unicauca.question.infrastructure.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import co.edu.unicauca.question.infrastructure.jpa.record.QuestionRecord;

/**
 * Provides JPA operations for question records.
 */
public interface QuestionJpaRepository
        extends JpaRepository<QuestionRecord, Long>,
                JpaSpecificationExecutor<QuestionRecord> {
}