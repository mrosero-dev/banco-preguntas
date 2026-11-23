package co.edu.unicauca.notification.domain.sender;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class LoggingNotificationDecoratorTest {

    @Test
    void shouldDelegateNotificationToDecoratedSender() {
        NotificationSender sender = mock(NotificationSender.class);

        LoggingNotificationDecorator decorator =
                new LoggingNotificationDecorator(sender);

        decorator.send("author123", "Question approved");

        verify(sender).send(
                "author123",
                "Question approved"
        );
    }

    @Test
    void shouldPropagateExceptionWhenNotificationFails() {
        NotificationSender sender = mock(NotificationSender.class);

        doThrow(new RuntimeException("Email delivery failed"))
                .when(sender)
                .send("author123", "Question approved");

        LoggingNotificationDecorator decorator =
                new LoggingNotificationDecorator(sender);

        assertThrows(
                RuntimeException.class,
                () -> decorator.send(
                        "author123",
                        "Question approved"
                )
        );

        verify(sender).send(
                "author123",
                "Question approved"
        );
    }

    @Test
    void shouldLogNotificationSending() {
        NotificationSender sender = mock(NotificationSender.class);

        LoggingNotificationDecorator decorator =
                new LoggingNotificationDecorator(sender);

        java.io.ByteArrayOutputStream output =
                new java.io.ByteArrayOutputStream();

        java.io.PrintStream originalOut = System.out;

        try {
            System.setOut(new java.io.PrintStream(output));

            decorator.send("author123", "Question approved");

        } finally {
            System.setOut(originalOut);
        }

        String log = output.toString();

        assertTrue(log.contains("Sending notification to author123"));
        assertTrue(log.contains("Notification sent successfully"));
    }
}