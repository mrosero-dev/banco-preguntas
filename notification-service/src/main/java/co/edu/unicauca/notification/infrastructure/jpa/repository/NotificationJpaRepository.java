package co.edu.unicauca.notification.infrastructure.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicauca.notification.infrastructure.jpa.record.NotificationRecord;

public interface NotificationJpaRepository
        extends JpaRepository<NotificationRecord, Long> {

    List<NotificationRecord> findByRecipientIdOrderByIdDesc(Long recipientId);
}
