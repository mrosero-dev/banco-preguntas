package co.edu.unicauca.notification.infrastructure.messaging;

import java.util.List;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.notification.application.NotificationService;
import co.edu.unicauca.notification.application.event.ReviewCompletedEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class QuestionReviewConsumerTest {

    @Test
    void shouldDelegateReviewCompletedEventToNotificationService() {
        NotificationService service = mock(NotificationService.class);
        QuestionReviewConsumer consumer = new QuestionReviewConsumer(service);

        ReviewCompletedEvent event =
                new ReviewCompletedEvent(100L, 2L, "APPROVED", List.of());

        consumer.receiveReviewEvent(event);

        verify(service).notifyReviewCompleted(event);
    }
}
