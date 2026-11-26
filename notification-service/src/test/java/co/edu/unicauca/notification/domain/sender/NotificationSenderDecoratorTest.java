package co.edu.unicauca.notification.domain.sender;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests of the base decorator: it keeps the wrapped sender.
 */
class NotificationSenderDecoratorTest {

    /** Minimal concrete decorator that only forwards the call. */
    private static class ForwardingDecorator extends NotificationSenderDecorator {

        ForwardingDecorator(NotificationSender decorated) {
            super(decorated);
        }

        @Override
        public void send(String recipient, String message) {
            decoratedSender.send(recipient, message);
        }

        NotificationSender wrapped() {
            return decoratedSender;
        }
    }

    @Test
    void shouldKeepTheDecoratedSender() {
        NotificationSender inner = mock(NotificationSender.class);

        ForwardingDecorator decorator = new ForwardingDecorator(inner);

        assertSame(inner, decorator.wrapped());
    }

    @Test
    void shouldForwardToTheDecoratedSender() {
        NotificationSender inner = mock(NotificationSender.class);

        new ForwardingDecorator(inner).send("a@u.co", "Hola");

        verify(inner).send("a@u.co", "Hola");
    }
}
