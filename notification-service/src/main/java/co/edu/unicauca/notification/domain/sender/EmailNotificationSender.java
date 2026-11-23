package co.edu.unicauca.notification.domain.sender;

/**
 * Sends notifications through email.
 */
public class EmailNotificationSender implements NotificationSender {

    private final EmailDelivery emailDelivery;

    public EmailNotificationSender(EmailDelivery emailDelivery) {
        this.emailDelivery = emailDelivery;
    }

    @Override
    public void send(String recipient, String message) {
        emailDelivery.deliver(recipient, message);
    }
}