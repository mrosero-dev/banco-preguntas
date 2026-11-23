package co.edu.unicauca.notification.infrastructure.adapter;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SmtpEmailProviderTest {

    @Test
    void shouldSendRealMailThroughTheMailSender() {
        JavaMailSender sender = mock(JavaMailSender.class);
        SmtpEmailProvider provider =
                new SmtpEmailProvider(sender, "noreply@banco.co");

        provider.deliverEmail("autor@unicauca.edu.co", "Pregunta aprobada");

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(sender).send(captor.capture());

        SimpleMailMessage mail = captor.getValue();
        assertEquals("noreply@banco.co", mail.getFrom());
        assertArrayEquals(new String[] {"autor@unicauca.edu.co"}, mail.getTo());
        assertEquals("Pregunta aprobada", mail.getText());
        assertEquals(SmtpEmailProvider.SUBJECT, mail.getSubject());
    }
}
