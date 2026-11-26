package co.edu.unicauca.notification.domain.factory;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.notification.domain.sender.EmailDelivery;
import co.edu.unicauca.notification.domain.sender.EmailNotificationSender;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests of the factory method that creates email senders.
 */
class EmailNotificationSenderFactoryTest {

    @Test
    void shouldCreateAnEmailSender() {
        EmailNotificationSenderFactory factory =
                new EmailNotificationSenderFactory(mock(EmailDelivery.class));

        assertInstanceOf(EmailNotificationSender.class, factory.createSender());
    }

    @Test
    void shouldSendThroughTheInjectedDelivery() {
        EmailDelivery delivery = mock(EmailDelivery.class);

        new EmailNotificationSenderFactory(delivery)
                .sendNotification("a@u.co", "Hola");

        verify(delivery).deliver("a@u.co", "Hola");
    }
}
