package co.edu.unicauca.notification.config;

import co.edu.unicauca.notification.domain.factory.EmailNotificationSenderFactory;
import co.edu.unicauca.notification.domain.factory.InAppNotificationSenderFactory;
import co.edu.unicauca.notification.domain.factory.NotificationSenderFactory;
import co.edu.unicauca.notification.domain.sender.EmailDelivery;
import co.edu.unicauca.notification.infrastructure.adapter.EmailProviderAdapter;
import co.edu.unicauca.notification.infrastructure.adapter.ExternalEmailProvider;
import co.edu.unicauca.notification.infrastructure.adapter.SmtpEmailProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import co.edu.unicauca.notification.domain.sender.LoggingNotificationDecorator;
import co.edu.unicauca.notification.domain.sender.NotificationSender;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * Configures the dependencies required by the notification service.
 */
@Configuration
public class NotificationConfig {

    /** Modo por defecto: el correo se simula escribiendo en consola. */
    @Bean
    @ConditionalOnProperty(name = "notification.email.mode",
            havingValue = "simulated", matchIfMissing = true)
    public ExternalEmailProvider externalEmailProvider() {
        return new ExternalEmailProvider();
    }

    /** Modo smtp (NOTIFICATION_EMAIL_MODE=smtp): correo real por SMTP. */
    @Bean
    @ConditionalOnProperty(name = "notification.email.mode", havingValue = "smtp")
    public ExternalEmailProvider smtpEmailProvider(
            JavaMailSender mailSender,
            @Value("${notification.email.from:noreply@bancopreguntas.unicauca.edu.co}")
            String from) {
        return new SmtpEmailProvider(mailSender, from);
    }

    @Bean
    public EmailDelivery emailDelivery(ExternalEmailProvider provider) {
        return new EmailProviderAdapter(provider);
    }

    @Bean
    public NotificationSenderFactory emailNotificationSenderFactory(
            EmailDelivery emailDelivery) {
        return new EmailNotificationSenderFactory(emailDelivery);
    }

    @Bean
    public NotificationSenderFactory inAppNotificationSenderFactory() {
        return new InAppNotificationSenderFactory();
    }

    @Bean
    public NotificationSender emailNotificationSender(
            @Qualifier("emailNotificationSenderFactory")
            NotificationSenderFactory factory) {

        return new LoggingNotificationDecorator(
                factory.createSender()
        );
    }

    @Bean
    public NotificationSender inAppNotificationSender(
            @Qualifier("inAppNotificationSenderFactory")
            NotificationSenderFactory factory) {

        return new LoggingNotificationDecorator(
                factory.createSender()
        );
    }
}