
package co.edu.unicauca.notification.domain.sender;

/**
 * Base decorator for notification senders.
 */
public abstract class NotificationSenderDecorator
        implements NotificationSender {

    protected final NotificationSender decoratedSender;

    protected NotificationSenderDecorator(
            NotificationSender decoratedSender) {
        this.decoratedSender = decoratedSender;
    }
}
