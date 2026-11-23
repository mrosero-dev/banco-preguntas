package co.edu.unicauca.notification.api.dto;

import java.time.LocalDateTime;

import co.edu.unicauca.notification.domain.model.Notification;
import co.edu.unicauca.notification.domain.model.NotificationType;

public class NotificationResponse {

    private Long id;
    private Long recipientId;
    private NotificationType type;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;

    public NotificationResponse(
            Long id,
            Long recipientId,
            NotificationType type,
            String message,
            LocalDateTime createdAt,
            boolean read) {

        this.id = id;
        this.recipientId = recipientId;
        this.type = type;
        this.message = message;
        this.createdAt = createdAt;
        this.read = read;
    }

    public static NotificationResponse fromDomain(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getRecipientId(),
                notification.getType(),
                notification.getMessage(),
                notification.getCreatedAt(),
                notification.isRead()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return read;
    }
}
