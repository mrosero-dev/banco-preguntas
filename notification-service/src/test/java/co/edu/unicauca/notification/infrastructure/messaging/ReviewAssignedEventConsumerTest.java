package co.edu.unicauca.notification.infrastructure.messaging;

import java.util.List;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.notification.application.NotificationService;
import co.edu.unicauca.notification.application.event.ReviewAssignedEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ReviewAssignedEventConsumerTest {

    @Test
    void shouldDelegateReviewAssignedEventToNotificationService() {
        NotificationService service = mock(NotificationService.class);
        ReviewAssignedEventConsumer consumer =
                new ReviewAssignedEventConsumer(service);

        ReviewAssignedEvent event =
                new ReviewAssignedEvent(100L, 2L, List.of(4L, 5L));

        consumer.receiveReviewAssignedEvent(event);

        verify(service).notifyReviewersAssigned(event);
    }
}
