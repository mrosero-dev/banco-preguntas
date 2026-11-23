package co.edu.unicauca.notification.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void shouldCreateUnreadNotification() {
        Notification notification = new Notification(
                5L, "a@b.com", NotificationType.REVIEW_ASSIGNED, "Hola");

        assertFalse(notification.isRead());
        assertNotNull(notification.getCreatedAt());
        assertEquals("a@b.com", notification.getRecipientEmail());
    }

    @Test
    void shouldMarkAsRead() {
        Notification notification = new Notification(
                5L, null, NotificationType.REVIEW_APPROVED, "Hola");

        notification.markAsRead();

        assertTrue(notification.isRead());
    }

    @Test
    void shouldRequireRecipient() {
        assertThrows(IllegalArgumentException.class, () -> new Notification(
                null, "a@b.com", NotificationType.REVIEW_ASSIGNED, "Hola"));
    }

    @Test
    void shouldRequireType() {
        assertThrows(IllegalArgumentException.class,
                () -> new Notification(5L, "a@b.com", null, "Hola"));
    }

    @Test
    void shouldRequireMessage() {
        assertThrows(IllegalArgumentException.class, () -> new Notification(
                5L, "a@b.com", NotificationType.REVIEW_ASSIGNED, " "));
    }
}
