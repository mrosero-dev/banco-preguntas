package co.edu.unicauca.identity.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.identity.api.dto.UserResponse;
import co.edu.unicauca.identity.application.IdentityService;
import co.edu.unicauca.identity.domain.model.Role;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final IdentityService identityService;

    public UserController(IdentityService identityService) {
        this.identityService = identityService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findUsers(
            @RequestParam(required = false) Role role) {

        return ResponseEntity.ok(
                identityService.findUsers(role).stream()
                        .map(UserResponse::fromDomain)
                        .toList()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> findUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                UserResponse.fromDomain(identityService.findById(id))
        );
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> currentUser(@AuthenticationPrincipal Jwt jwt) {
        Object userIdClaim = jwt.getClaim("user_id");
        String userId = userIdClaim == null ? null : userIdClaim.toString();
        if (userId == null) return ResponseEntity.status(403).build();
        return ResponseEntity.ok(UserResponse.fromDomain(
                identityService.findById(Long.parseLong(userId))));
    }
}
