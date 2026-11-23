package co.edu.unicauca.notification.infrastructure.messaging;

import java.util.List;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.notification.application.NotificationService;
import co.edu.unicauca.notification.application.event.QuestionResubmittedEvent;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class QuestionResubmittedConsumerTest {

    @Test
    void shouldDelegateQuestionResubmittedEventToNotificationService() {
        NotificationService service = mock(NotificationService.class);
        QuestionResubmittedConsumer consumer =
                new QuestionResubmittedConsumer(service);

        QuestionResubmittedEvent event =
                new QuestionResubmittedEvent(100L, 2L, 2, List.of(4L, 5L));

        consumer.receiveQuestionResubmitted(event);

        verify(service).notifyQuestionResubmitted(event);
    }
}
