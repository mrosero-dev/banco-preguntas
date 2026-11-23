package co.edu.unicauca.notification.domain.factory;

import co.edu.unicauca.notification.domain.sender.InAppNotificationSender;
import co.edu.unicauca.notification.domain.sender.NotificationSender;

/**
 * Creates in-app notification senders.
 */
public class InAppNotificationSenderFactory extends NotificationSenderFactory {

    @Override
    public NotificationSender createSender() {
        return new InAppNotificationSender();
    }
}