package co.edu.unicauca.notification.domain.factory;

import co.edu.unicauca.notification.domain.sender.NotificationSender;

/**
 * Defines the base creator for notification senders.
 */
public abstract class NotificationSenderFactory {

    /**
     * Factory method that creates a notification sender.
     *
     * @return the notification sender created
     */
    public abstract NotificationSender createSender();

    /**
     * Sends a notification using the sender created by the factory method.
     *
     * @param recipient notification recipient
     * @param message notification message
     */
    public void sendNotification(String recipient, String message) {
        NotificationSender sender = createSender();
        sender.send(recipient, message);
    }
}