package co.edu.unicauca.question.infrastructure.jpa.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.criteria.Predicate;

import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionStatus;
import co.edu.unicauca.question.domain.repository.QuestionPage;
import co.edu.unicauca.question.domain.repository.QuestionRepository;
import co.edu.unicauca.question.infrastructure.jpa.mapper.QuestionMapper;
import co.edu.unicauca.question.infrastructure.jpa.record.QuestionRecord;

@Repository
public class QuestionRepositoryAdapter implements QuestionRepository {

    private final QuestionJpaRepository jpaRepository;
    private final QuestionMapper mapper;

    public QuestionRepositoryAdapter(
            QuestionJpaRepository jpaRepository,
            QuestionMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Transactional
    @Override
    public Question save(Question question) {

        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }

        QuestionRecord record;

        if (question.getId() == null) {
            record = mapper.toRecord(question);
        } else {
            record = jpaRepository.findById(question.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Question not found: " + question.getId()
                    ));

            mapper.updateRecord(question, record);

            record.getOptions().clear();

            question.getOptions().stream()
                    .map(mapper::toRecordOption)
                    .forEach(record::addOption);
        }

        // Snapshots are immutable: only the ones not yet persisted are added.
        for (int i = record.getVersionSnapshots().size();
                i < question.getVersionHistory().size(); i++) {
            record.addVersionSnapshot(
                    mapper.toRecordVersion(question.getVersionHistory().get(i)));
        }

        QuestionRecord savedRecord = jpaRepository.save(record);

        return mapper.toDomain(savedRecord);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Question> findById(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("Question ID cannot be null");
        }

        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Transactional(readOnly = true)
    @Override
    public QuestionPage findByAuthor(
            Long authorId,
            QuestionStatus status,
            String difficulty,
            String category,
            String search,
            int page,
            int size) {

        if (authorId == null || authorId <= 0) {
            throw new IllegalArgumentException(
                    "Author ID must be greater than zero"
            );
        }

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Invalid pagination parameters"
            );
        }

        Specification<QuestionRecord> specification =
                (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Every query must be restricted to the requested author.
            predicates.add(
                    criteriaBuilder.equal(
                            root.get("authorId"),
                            authorId
                    )
            );

            if (status != null) {
                predicates.add(
                        criteriaBuilder.equal(
                                root.get("status"),
                                status
                        )
                );
            }

            if (difficulty != null && !difficulty.isBlank()) {
                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.upper(
                                        root.get("difficulty")
                                ),
                                difficulty.trim().toUpperCase()
                        )
                );
            }

            if (category != null && !category.isBlank()) {
                predicates.add(
                        criteriaBuilder.equal(
                                criteriaBuilder.upper(
                                        root.get("category")
                                ),
                                category.trim().toUpperCase()
                        )
                );
            }

            if (search != null && !search.isBlank()) {
                predicates.add(
                        criteriaBuilder.like(
                                criteriaBuilder.lower(
                                        root.get("statement")
                                ),
                                "%" + search.trim().toLowerCase() + "%"
                        )
                );
            }

            return criteriaBuilder.and(
                    predicates.toArray(new Predicate[0])
            );
        };

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "id")
        );

        Page<QuestionRecord> result = jpaRepository.findAll(
                specification,
                pageable
        );

        List<Question> questions = result.getContent()
                .stream()
                .map(mapper::toDomain)
                .toList();

        return new QuestionPage(
                questions,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }
}