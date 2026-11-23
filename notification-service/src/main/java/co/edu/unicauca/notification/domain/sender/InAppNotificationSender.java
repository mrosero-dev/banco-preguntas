package co.edu.unicauca.notification.domain.sender;

/**
 * Sends notifications within the application.
 */
public class InAppNotificationSender implements NotificationSender {

    @Override
    public void send(String recipient, String message) {
        System.out.println(
                "Sending in-app notification to " + recipient + ": " + message
        );
    }
}