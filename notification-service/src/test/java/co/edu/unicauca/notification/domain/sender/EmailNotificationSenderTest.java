package co.edu.unicauca.notification.domain.sender;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Unit tests of EmailNotificationSender with a mocked EmailDelivery.
 */
class EmailNotificationSenderTest {

    @Test
    void shouldDelegateDeliveryToTheEmailPort() {
        EmailDelivery delivery = mock(EmailDelivery.class);
        EmailNotificationSender sender = new EmailNotificationSender(delivery);

        sender.send("a@u.co", "Hola");

        verify(delivery).deliver("a@u.co", "Hola");
        verifyNoMoreInteractions(delivery);
    }
}
