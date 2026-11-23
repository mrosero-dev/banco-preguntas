package co.edu.unicauca.review.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import co.edu.unicauca.review.api.dto.AssignReviewersRequest;
import co.edu.unicauca.review.api.dto.ReviewAssignmentResponse;
import co.edu.unicauca.review.api.dto.ReviewDecisionRequest;
import co.edu.unicauca.review.application.ReviewApplicationService;
import co.edu.unicauca.review.domain.model.ReviewAssignment;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewApplicationService service;

    public ReviewController(ReviewApplicationService service) {
        this.service = service;
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ReviewAssignmentResponse> assignReviewers(
            @Valid @RequestBody AssignReviewersRequest request) {

        ReviewAssignment assignment = service.assignReviewers(
                request.getQuestionId(),
                request.getAuthorId(),
                request.getReviewerIds()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ReviewAssignmentResponse.fromDomain(assignment));
    }

    @PostMapping("/{questionId}/decision")
    @PreAuthorize("hasRole('REVIEWER')")
    public ResponseEntity<ReviewAssignmentResponse> registerDecision(
            @PathVariable Long questionId,
            @Valid @RequestBody ReviewDecisionRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        Object userId = jwt == null ? null : jwt.getClaim("user_id");
        if (jwt != null && (userId == null || !request.getReviewerId().toString().equals(userId.toString()))) {
            throw new AccessDeniedException("No puedes registrar decisiones en nombre de otro revisor.");
        }
        ReviewAssignment assignment;

        if (request.getApproved()) {
            assignment = service.approveReview(
                    questionId,
                    request.getReviewerId()
            );
        } else {
            assignment = service.rejectReview(
                    questionId,
                    request.getReviewerId(),
                    request.getObservations()
            );
        }

        return ResponseEntity.ok(
                ReviewAssignmentResponse.fromDomain(assignment)
        );
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReviewAssignmentResponse>>
            getPendingAssignments() {

        return ResponseEntity.ok(
                service.findPendingAssignments().stream()
                        .map(ReviewAssignmentResponse::fromDomain)
                        .toList()
        );
    }

    @GetMapping("/reviewer/{reviewerId}")
    @PreAuthorize("hasRole('REVIEWER')")
    public ResponseEntity<List<ReviewAssignmentResponse>>
            getAssignmentsByReviewer(@PathVariable Long reviewerId, @AuthenticationPrincipal Jwt jwt) {
        Object userId = jwt == null ? null : jwt.getClaim("user_id");
        if (jwt != null && (userId == null || !reviewerId.toString().equals(userId.toString()))) {
            throw new AccessDeniedException("Solo puedes consultar tus propias asignaciones.");
        }
        return ResponseEntity.ok(
                service.findAssignmentsByReviewer(reviewerId).stream()
                        .map(ReviewAssignmentResponse::fromDomain)
                        .toList()
        );
    }

    @GetMapping("/{questionId}")
    @PreAuthorize("hasAnyRole('AUTHOR', 'ADMIN', 'REVIEWER')")
    public ResponseEntity<ReviewAssignmentResponse> getAssignment(
            @PathVariable Long questionId, @AuthenticationPrincipal Jwt jwt,
            Authentication authentication) {

        ReviewAssignment assignment = service.getAssignment(questionId);
        Object userId = jwt == null ? null : jwt.getClaim("user_id");
        if (jwt != null && userId != null && authentication != null
                && authentication.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_REVIEWER"))
                && !assignment.getAuthorId().toString().equals(userId.toString())) {
            throw new AccessDeniedException("Solo puedes consultar revisiones de tus preguntas.");
        }
        return ResponseEntity.ok(
                ReviewAssignmentResponse.fromDomain(assignment)
        );
    }

    /** HU-2.3: only the owner of the question can inspect its review history. */
    @GetMapping("/{questionId}/history")
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<ReviewAssignmentResponse> getHistory(
            @PathVariable Long questionId, @AuthenticationPrincipal Jwt jwt) {
        ReviewAssignment assignment = service.getAssignment(questionId);
        Object userIdClaim = jwt == null ? null : jwt.getClaim("user_id");
        String userId = userIdClaim == null ? null : userIdClaim.toString();
        if (jwt != null && (userId == null || !assignment.getAuthorId().toString().equals(userId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ReviewAssignmentResponse.fromDomain(assignment));
    }
}
