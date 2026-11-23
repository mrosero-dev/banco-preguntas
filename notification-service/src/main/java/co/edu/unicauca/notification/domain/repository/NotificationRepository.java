package co.edu.unicauca.notification.domain.repository;

import java.util.List;
import java.util.Optional;

import co.edu.unicauca.notification.domain.model.Notification;

/**
 * Defines the contract for storing and retrieving notifications.
 */
public interface NotificationRepository {

    Notification save(Notification notification);

    Optional<Notification> findById(Long id);

    List<Notification> findByRecipientId(Long recipientId);
}
