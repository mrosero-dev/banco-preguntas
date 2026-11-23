package co.edu.unicauca.notification.infrastructure.jpa.mapper;

import org.springframework.stereotype.Component;

import co.edu.unicauca.notification.domain.model.Notification;
import co.edu.unicauca.notification.infrastructure.jpa.record.NotificationRecord;

@Component
public class NotificationMapper {

    public NotificationRecord toRecord(Notification notification) {

        NotificationRecord record = new NotificationRecord();
        updateRecord(notification, record);

        return record;
    }

    public void updateRecord(
            Notification notification,
            NotificationRecord record) {

        record.setRecipientId(notification.getRecipientId());
        record.setRecipientEmail(notification.getRecipientEmail());
        record.setType(notification.getType());
        record.setMessage(notification.getMessage());
        record.setCreatedAt(notification.getCreatedAt());
        record.setRead(notification.isRead());
    }

    public Notification toDomain(NotificationRecord record) {

        return Notification.restore(
                record.getId(),
                record.getRecipientId(),
                record.getRecipientEmail(),
                record.getType(),
                record.getMessage(),
                record.getCreatedAt(),
                record.isRead()
        );
    }
}
