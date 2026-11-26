package co.edu.unicauca.notification.domain.factory;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.notification.domain.sender.InAppNotificationSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Unit tests of the factory method that creates in-app senders.
 */
class InAppNotificationSenderFactoryTest {

    private final InAppNotificationSenderFactory factory =
            new InAppNotificationSenderFactory();

    @Test
    void shouldCreateAnInAppSender() {
        assertInstanceOf(InAppNotificationSender.class, factory.createSender());
    }

    @Test
    void shouldSendNotificationWithoutFailing() {
        assertDoesNotThrow(() -> factory.sendNotification("user-1", "Hola"));
    }
}
