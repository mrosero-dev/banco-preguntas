
package co.edu.unicauca.notification.domain.sender;

/**
 * Adds logging behavior to a notification sender.
 */
public class LoggingNotificationDecorator
        extends NotificationSenderDecorator {

    public LoggingNotificationDecorator(
            NotificationSender decoratedSender) {
        super(decoratedSender);
    }

    @Override
    public void send(String recipient, String message) {
        System.out.println(
                "Sending notification to " + recipient
        );

        decoratedSender.send(recipient, message);

        System.out.println("Notification sent successfully");
    }
}
