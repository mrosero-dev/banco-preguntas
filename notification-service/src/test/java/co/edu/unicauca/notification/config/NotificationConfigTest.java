package co.edu.unicauca.notification.config;

import co.edu.unicauca.notification.domain.factory.NotificationSenderFactory;
import co.edu.unicauca.notification.domain.sender.EmailNotificationSender;
import co.edu.unicauca.notification.domain.sender.InAppNotificationSender;
import co.edu.unicauca.notification.domain.sender.LoggingNotificationDecorator;
import co.edu.unicauca.notification.domain.sender.NotificationSender;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class NotificationConfigTest {

    @Autowired
    @Qualifier("emailNotificationSenderFactory")
    private NotificationSenderFactory emailFactory;

    @Autowired
    @Qualifier("inAppNotificationSenderFactory")
    private NotificationSenderFactory inAppFactory;

    @Autowired
    @Qualifier("emailNotificationSender")
    private NotificationSender emailSender;

    @Autowired
    @Qualifier("inAppNotificationSender")
    private NotificationSender inAppSender;

    @Test
    void shouldLoadApplicationContext() {
        assertNotNull(emailFactory);
        assertNotNull(inAppFactory);
    }

    @Test
    void shouldCreateEmailSenderBean() {
        assertInstanceOf(EmailNotificationSender.class,
                emailFactory.createSender());
    }

    @Test
    void shouldCreateInAppSenderBean() {
        assertInstanceOf(InAppNotificationSender.class,
                inAppFactory.createSender());
    }

    @Test
    void shouldProvideDecoratedEmailSender() {
        assertInstanceOf(
                LoggingNotificationDecorator.class,
                emailSender
        );
    }

    @Test
    void shouldProvideDecoratedInAppSender() {
        assertInstanceOf(
                LoggingNotificationDecorator.class,
                inAppSender
        );
    }
}