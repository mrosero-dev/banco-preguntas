package co.edu.unicauca.notification.infrastructure.jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import co.edu.unicauca.notification.domain.model.Notification;
import co.edu.unicauca.notification.domain.model.NotificationType;
import co.edu.unicauca.notification.domain.repository.NotificationRepository;
import co.edu.unicauca.notification.infrastructure.jpa.mapper.NotificationMapper;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({NotificationRepositoryImpl.class, NotificationMapper.class})
class NotificationRepositoryIntegrationTest {

    @Autowired
    private NotificationRepository repository;

    @Test
    void shouldPersistAndListByRecipientNewestFirst() {
        repository.save(new Notification(
                2L, "a@u.co", NotificationType.REVIEW_ASSIGNED, "Primera"));
        repository.save(new Notification(
                2L, "a@u.co", NotificationType.REVIEW_APPROVED, "Segunda"));
        repository.save(new Notification(
                3L, null, NotificationType.REVIEW_REJECTED, "Otra persona"));

        var result = repository.findByRecipientId(2L);

        assertEquals(2, result.size());
        assertEquals("Segunda", result.get(0).getMessage());
    }

    @Test
    void shouldPersistReadFlag() {
        Notification saved = repository.save(new Notification(
                2L, null, NotificationType.REVIEW_APPROVED, "Hola"));

        saved.markAsRead();
        repository.save(saved);

        assertTrue(repository.findById(saved.getId()).orElseThrow().isRead());
    }
}
