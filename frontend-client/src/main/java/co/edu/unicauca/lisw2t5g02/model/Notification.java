package co.edu.unicauca.lisw2t5g02.model;

/** Notification stored by the Notification Service. */
public record Notification(
        long id,
        long recipientId,
        String type,
        String message,
        String createdAt,
        boolean read) {
}
