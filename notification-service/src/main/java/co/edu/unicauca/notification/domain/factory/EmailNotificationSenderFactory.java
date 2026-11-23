package co.edu.unicauca.notification.domain.factory;

import co.edu.unicauca.notification.domain.sender.EmailDelivery;
import co.edu.unicauca.notification.domain.sender.EmailNotificationSender;
import co.edu.unicauca.notification.domain.sender.NotificationSender;

/**
 * Creates email notification senders.
 */
public class EmailNotificationSenderFactory extends NotificationSenderFactory {

    private final EmailDelivery emailDelivery;

    public EmailNotificationSenderFactory(EmailDelivery emailDelivery) {
        this.emailDelivery = emailDelivery;
    }

    @Override
    public NotificationSender createSender() {
        return new EmailNotificationSender(emailDelivery);
    }
}