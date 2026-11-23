package co.edu.unicauca.notification.domain.model;

import java.time.LocalDateTime;

/**
 * Represents a notification addressed to one user.
 */
public class Notification {

    private Long id;
    private Long recipientId;
    private String recipientEmail;
    private NotificationType type;
    private String message;
    private LocalDateTime createdAt;
    private boolean read;

    public Notification(
            Long recipientId,
            String recipientEmail,
            NotificationType type,
            String message) {

        if (recipientId == null) {
            throw new IllegalArgumentException("Recipient ID is required");
        }

        if (type == null) {
            throw new IllegalArgumentException("Notification type is required");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message is required");
        }

        this.recipientId = recipientId;
        this.recipientEmail = recipientEmail;
        this.type = type;
        this.message = message;
        this.createdAt = LocalDateTime.now();
        this.read = false;
    }

    public void markAsRead() {
        this.read = true;
    }

    public Long getId() {
        return id;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public String getRecipientEmail() {
        return recipientEmail;
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

    /**
     * Restores a notification from previously persisted information.
     */
    public static Notification restore(
            Long id,
            Long recipientId,
            String recipientEmail,
            NotificationType type,
            String message,
            LocalDateTime createdAt,
            boolean read) {

        Notification notification =
                new Notification(recipientId, recipientEmail, type, message);

        notification.id = id;
        notification.createdAt = createdAt;
        notification.read = read;

        return notification;
    }
}
