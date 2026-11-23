package co.edu.unicauca.lisw2t5g02.client;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.lisw2t5g02.model.Notification;

import static org.junit.jupiter.api.Assertions.*;

class NotificationClientTest {

    private static final String NOTIFICATION =
            "{\"id\":3,\"recipientId\":2,\"type\":\"REVIEW_APPROVED\","
                    + "\"message\":\"Aprobada\",\"createdAt\":\"2026-11-05T10:00:00\","
                    + "\"read\":false}";

    @Test
    void shouldListNotificationsOfRecipient() throws Exception {
        try (MockServer server = new MockServer().on("GET",
                "/api/notifications?recipientId=2", 200, "[" + NOTIFICATION + "]")) {

            Notification first = new NotificationClient(new RestClient(), server.url())
                    .findByRecipient(2).get(0);

            assertEquals("REVIEW_APPROVED", first.type());
            assertFalse(first.read());
        }
    }

    @Test
    void shouldMarkNotificationAsRead() throws Exception {
        try (MockServer server = new MockServer().on("PATCH",
                "/api/notifications/3/read", 200,
                NOTIFICATION.replace("false", "true"))) {

            assertTrue(new NotificationClient(new RestClient(), server.url())
                    .markAsRead(3).read());
        }
    }
}
