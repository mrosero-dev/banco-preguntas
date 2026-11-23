package co.edu.unicauca.notification.domain.sender;

/**
 * Defines the contract for sending notifications.
 */
public interface NotificationSender {

    void send(String recipient, String message);
}