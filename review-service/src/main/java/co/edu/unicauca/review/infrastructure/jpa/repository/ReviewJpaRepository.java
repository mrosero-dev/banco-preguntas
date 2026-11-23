package co.edu.unicauca.review.infrastructure.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unicauca.review.infrastructure.jpa.record.ReviewAssignmentRecord;

public interface ReviewJpaRepository extends JpaRepository<ReviewAssignmentRecord, Long> {

    Optional<ReviewAssignmentRecord> findByQuestionId(Long questionId);

    @Query("select distinct a from ReviewAssignmentRecord a "
            + "join a.reviews r where r.reviewerId = :reviewerId "
            + "and r.roundNumber = a.currentRound order by a.id desc")
    List<ReviewAssignmentRecord> findByReviewerId(
            @Param("reviewerId") Long reviewerId);

    @Query("select a from ReviewAssignmentRecord a "
            + "where not exists (select r from ReviewRecord r "
            + "where r.assignment = a and r.roundNumber = a.currentRound) "
            + "order by a.id")
    List<ReviewAssignmentRecord> findWithoutReviewers();
}