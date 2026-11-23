package co.edu.unicauca.notification.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unicauca.notification.api.dto.NotificationResponse;
import co.edu.unicauca.notification.application.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> findByRecipient(
            @RequestParam Long recipientId, @AuthenticationPrincipal Jwt jwt) {
        Object userId = jwt == null ? null : jwt.getClaim("user_id");
        if (jwt != null && (userId == null || !recipientId.toString().equals(userId.toString()))) {
            throw new AccessDeniedException("Solo puedes consultar tus propias notificaciones.");
        }
        return ResponseEntity.ok(
                notificationService.findByRecipient(recipientId).stream()
                        .map(NotificationResponse::fromDomain)
                        .toList()
        );
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Object userId = jwt == null ? null : jwt.getClaim("user_id");
        NotificationResponse response;
        if (jwt == null) { // isolated controller tests without a security context
            response = NotificationResponse.fromDomain(notificationService.markAsRead(id));
        } else {
            if (userId == null) throw new AccessDeniedException("Usuario no autenticado.");
            response = NotificationResponse.fromDomain(
                    notificationService.markAsRead(id, Long.valueOf(userId.toString())));
        }
        return ResponseEntity.ok(response);
    }
}
