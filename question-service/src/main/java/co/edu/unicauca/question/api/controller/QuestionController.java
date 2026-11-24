package co.edu.unicauca.question.api.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import co.edu.unicauca.question.api.dto.CreateQuestionRequest;
import co.edu.unicauca.question.api.dto.QuestionPageResponse;
import co.edu.unicauca.question.api.dto.QuestionResponse;
import co.edu.unicauca.question.api.dto.QuestionVersionResponse;
import co.edu.unicauca.question.api.dto.UpdateQuestionRequest;
import co.edu.unicauca.question.api.mapper.QuestionRequestMapper;
import co.edu.unicauca.question.application.QuestionService;
import co.edu.unicauca.question.domain.model.Question;
import co.edu.unicauca.question.domain.model.QuestionOption;
import co.edu.unicauca.question.domain.model.QuestionStatus;
import co.edu.unicauca.question.domain.repository.QuestionPage;

@RestController
@RequestMapping("/api/questions")
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionRequestMapper questionRequestMapper;

    public QuestionController(
            QuestionService questionService,
            QuestionRequestMapper questionRequestMapper) {

        this.questionService = questionService;
        this.questionRequestMapper = questionRequestMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<QuestionResponse> createQuestion(
            @Valid @RequestBody CreateQuestionRequest request) {

        ensureAuthor(request.getAuthorId());
        List<QuestionOption> options =
                questionRequestMapper.toDomainOptions(request.getOptions());

        Question question = questionService.createQuestion(
                request.getAuthorId(),
                request.getStatement(),
                request.getContext(),
                request.getDifficulty(),
                request.getCategory(),
                request.getTopic(),
                request.getSubtopic(),
                request.getCompetence(),
                request.getJustification(),
                request.getSource(),
                options
        );

        QuestionResponse response =
                questionRequestMapper.toResponse(question);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<QuestionResponse> updateQuestion(
            @PathVariable Long id,
            @RequestParam Long authorId,
            @Valid @RequestBody UpdateQuestionRequest request) {

        ensureAuthor(authorId);
        List<QuestionOption> options =
                questionRequestMapper.toDomainOptions(request.getOptions());

        Question question = questionService.updateQuestion(
                id,
                authorId,
                request.getStatement(),
                request.getContext(),
                request.getDifficulty(),
                request.getCategory(),
                request.getTopic(),
                request.getSubtopic(),
                request.getCompetence(),
                request.getJustification(),
                request.getSource(),
                options
        );

        QuestionResponse response =
                questionRequestMapper.toResponse(question);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<QuestionPageResponse> findQuestionsByAuthor(
            @RequestParam Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) QuestionStatus status,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {

        ensureAuthor(authorId);
        QuestionPage result = questionService.findQuestionsByAuthor(
                authorId,
                status,
                difficulty,
                category,
                search,
                page,
                size
        );

        List<QuestionResponse> questions = result.content()
                .stream()
                .map(questionRequestMapper::toResponse)
                .toList();

        QuestionPageResponse response = new QuestionPageResponse();
        response.setContent(questions);
        response.setPage(result.page());
        response.setSize(result.size());
        response.setTotalElements(result.totalElements());
        response.setTotalPages(result.totalPages());
        response.setFirst(result.first());
        response.setLast(result.last());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('AUTHOR', 'REVIEWER', 'ADMIN')")
    public ResponseEntity<QuestionResponse> findQuestionById(
            @PathVariable Long id) {

        Optional<Question> question =
                questionService.findQuestionById(id);

        if (question.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (question.get().getAuthorId() != null) ensureAuthorOrPrivileged(question.get().getAuthorId());
        QuestionResponse response =
                questionRequestMapper.toResponse(question.get());

        return ResponseEntity.ok(response);
    }

    /**
     * HU-2.3: previous versions of a question, most recent first.
     */
    @GetMapping("/{id}/versions")
    @PreAuthorize("hasAnyRole('AUTHOR', 'REVIEWER', 'ADMIN')")
    public ResponseEntity<List<QuestionVersionResponse>> findQuestionVersions(
            @PathVariable Long id) {

        Optional<Question> question =
                questionService.findQuestionById(id);

        if (question.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if (question.get().getAuthorId() != null) {
            ensureAuthorOrPrivileged(question.get().getAuthorId());
        }

        List<QuestionVersionResponse> versions = question.get()
                .getVersionHistory().stream()
                .map(QuestionVersionResponse::from)
                .sorted((a, b) -> Integer.compare(
                        b.versionNumber(), a.versionNumber()))
                .toList();

        return ResponseEntity.ok(versions);
    }

    @PostMapping("/{id}/submit-for-review")
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<QuestionResponse> submitQuestionForReview(
            @PathVariable Long id) {

        Optional<Question> question =
                questionService.findQuestionById(id);

        if (question.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ensureAuthor(question.get().getAuthorId());
        Question submittedQuestion =
                questionService.submitQuestionForReview(question.get());

        QuestionResponse response =
                questionRequestMapper.toResponse(submittedQuestion);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/return-to-draft")
    @PreAuthorize("hasRole('AUTHOR')")
    public ResponseEntity<QuestionResponse> returnRejectedQuestionToDraft(
            @PathVariable Long id,
            @RequestParam Long authorId) {

        ensureAuthor(authorId);
        Question question = questionService.returnRejectedQuestionToDraft(
                id,
                authorId
        );

        QuestionResponse response =
                questionRequestMapper.toResponse(question);

        return ResponseEntity.ok(response);
    }

    private String currentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwt) {
            Object id = jwt.getToken().getClaim("user_id");
            return id == null ? null : id.toString();
        }
        return null; // permits isolated controller unit tests without a security context
    }

    private void ensureAuthor(Long authorId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        // Without a JWT (isolated controller tests) there is nothing to compare.
        if (authentication instanceof JwtAuthenticationToken) {
            String authenticatedId = currentUserId();
            if (authenticatedId == null
                    || !authenticatedId.equals(String.valueOf(authorId))) {
                throw new AccessDeniedException("Solo puedes operar sobre tus propias preguntas.");
            }
        }
    }

    private void ensureAuthorOrPrivileged(Long authorId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ROLE_REVIEWER"))) return;
        ensureAuthor(authorId);
    }
}
