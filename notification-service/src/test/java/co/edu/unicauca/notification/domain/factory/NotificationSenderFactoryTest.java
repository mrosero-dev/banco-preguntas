package co.edu.unicauca.notification.domain.factory;

import co.edu.unicauca.notification.domain.sender.EmailNotificationSender;
import co.edu.unicauca.notification.domain.sender.EmailDelivery;
import co.edu.unicauca.notification.domain.sender.InAppNotificationSender;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationSenderFactoryTest {

    @Test
    void shouldCreateEmailNotificationSender() {
        EmailDelivery emailDelivery = mock(EmailDelivery.class);

        NotificationSenderFactory factory =
                new EmailNotificationSenderFactory(emailDelivery);

        assertInstanceOf(
                EmailNotificationSender.class,
                factory.createSender()
        );
    }

    @Test
    void shouldCreateInAppNotificationSender() {
        NotificationSenderFactory factory =
                new InAppNotificationSenderFactory();

        assertInstanceOf(
                InAppNotificationSender.class,
                factory.createSender()
        );
    }

    @Test
    void shouldSendNotificationUsingEmailFactory() {
        EmailDelivery emailDelivery = mock(EmailDelivery.class);

        NotificationSenderFactory factory =
                new EmailNotificationSenderFactory(emailDelivery);

        factory.sendNotification(
                "author@example.com",
                "Your question was reviewed"
        );

        verify(emailDelivery).deliver(
                "author@example.com",
                "Your question was reviewed"
        );
    }
}