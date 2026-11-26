package co.edu.unicauca.notification.infrastructure.messaging;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.notification.application.NotificationService;
import co.edu.unicauca.notification.application.event.QuestionSubmittedEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class QuestionSubmittedConsumerTest {

    @Test
    void shouldDelegateQuestionSubmittedEventToNotificationService() {
        NotificationService service = mock(NotificationService.class);
        QuestionSubmittedConsumer consumer =
                new QuestionSubmittedConsumer(service);

        QuestionSubmittedEvent event = new QuestionSubmittedEvent(100L, 2L);

        consumer.receiveQuestionSubmitted(event);

        verify(service).notifyQuestionSubmitted(event);
    }
}
