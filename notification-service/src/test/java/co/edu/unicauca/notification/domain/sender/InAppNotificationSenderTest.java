package co.edu.unicauca.notification.domain.sender;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * Unit tests of InAppNotificationSender.
 */
class InAppNotificationSenderTest {

    @Test
    void shouldSendWithoutFailing() {
        InAppNotificationSender sender = new InAppNotificationSender();

        assertDoesNotThrow(() -> sender.send("user-1", "Hola"));
    }
}
