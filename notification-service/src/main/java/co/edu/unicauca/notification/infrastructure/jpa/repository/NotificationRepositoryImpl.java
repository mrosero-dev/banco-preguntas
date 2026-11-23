package co.edu.unicauca.notification.infrastructure.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import co.edu.unicauca.notification.domain.model.Notification;
import co.edu.unicauca.notification.domain.repository.NotificationRepository;
import co.edu.unicauca.notification.infrastructure.jpa.mapper.NotificationMapper;
import co.edu.unicauca.notification.infrastructure.jpa.record.NotificationRecord;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private final NotificationJpaRepository jpaRepository;
    private final NotificationMapper mapper;

    public NotificationRepositoryImpl(
            NotificationJpaRepository jpaRepository,
            NotificationMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Notification save(Notification notification) {

        NotificationRecord record;

        if (notification.getId() == null) {
            record = mapper.toRecord(notification);
        } else {
            record = jpaRepository.findById(notification.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Notification not found: " + notification.getId()
                    ));
            mapper.updateRecord(notification, record);
        }

        return mapper.toDomain(jpaRepository.save(record));
    }

    @Override
    public Optional<Notification> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Notification> findByRecipientId(Long recipientId) {
        return jpaRepository.findByRecipientIdOrderByIdDesc(recipientId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
