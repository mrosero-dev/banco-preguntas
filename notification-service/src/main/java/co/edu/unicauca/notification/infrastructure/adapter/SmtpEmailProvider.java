package co.edu.unicauca.notification.infrastructure.adapter;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Real email provider: sends the message through an SMTP server configured
 * with the standard spring.mail.* properties (MailHog in Docker Compose).
 */
public class SmtpEmailProvider extends ExternalEmailProvider {

    static final String SUBJECT = "Banco de Preguntas Saber Pro: notificación";

    private final JavaMailSender mailSender;
    private final String from;

    public SmtpEmailProvider(JavaMailSender mailSender, String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void deliverEmail(String emailAddress, String content) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(from);
        mail.setTo(emailAddress);
        mail.setSubject(SUBJECT);
        mail.setText(content);
        mailSender.send(mail);
    }
}
