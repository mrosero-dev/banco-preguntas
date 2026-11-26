package co.edu.unicauca.notification.application;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unicauca.notification.application.client.UserContactDto;
import co.edu.unicauca.notification.application.client.IdentityGateway;
import co.edu.unicauca.notification.application.event.QuestionResubmittedEvent;
import co.edu.unicauca.notification.application.event.QuestionSubmittedEvent;
import co.edu.unicauca.notification.application.event.ReviewAssignedEvent;
import co.edu.unicauca.notification.application.event.ReviewCompletedEvent;
import co.edu.unicauca.notification.domain.exception.NotificationNotFoundException;
import co.edu.unicauca.notification.domain.model.Notification;
import co.edu.unicauca.notification.domain.model.NotificationType;
import co.edu.unicauca.notification.domain.repository.NotificationRepository;
import co.edu.unicauca.notification.domain.sender.NotificationSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository repository;

    @Mock
    private IdentityGateway identityGateway;

    @Mock
    private NotificationSender emailSender;

    @Mock
    private NotificationSender inAppSender;

    private NotificationService service;

    @BeforeEach
    void setUp() {
        service = new NotificationService(
                repository, identityGateway, emailSender, inAppSender);
    }

    @Test
    void shouldNotifyEveryAssignedReviewerByEmailAndInApp() {
        when(identityGateway.findById(4L)).thenReturn(Optional.of(
                new UserContactDto(4L, "Rev Uno", "r1@u.co", "REVIEWER")));
        when(identityGateway.findById(5L)).thenReturn(Optional.of(
                new UserContactDto(5L, "Rev Dos", "r2@u.co", "REVIEWER")));

        service.notifyReviewersAssigned(
                new ReviewAssignedEvent(100L, 2L, List.of(4L, 5L)));

        verify(repository, times(2)).save(any(Notification.class));
        verify(emailSender).send(eq("r1@u.co"), contains("#100"));
        verify(emailSender).send(eq("r2@u.co"), contains("#100"));
        verify(inAppSender, times(2)).send(anyString(), contains("#100"));
    }

    @Test
    void shouldStoreInAppNotificationWhenIdentityDoesNotKnowTheUser() {
        when(identityGateway.findById(4L)).thenReturn(Optional.empty());

        service.notifyReviewersAssigned(
                new ReviewAssignedEvent(100L, 2L, List.of(4L)));

        verify(repository).save(any(Notification.class));
        verify(inAppSender).send(eq("user-4"), anyString());
        verifyNoInteractions(emailSender);
    }

    @Test
    void shouldNotifyEveryAdministratorWhenAuthorSubmitsQuestion() {
        when(identityGateway.findByRole("ADMIN")).thenReturn(List.of(
                new UserContactDto(1L, "Admin", "admin@u.co", "ADMIN"),
                new UserContactDto(9L, "Admin 2", "admin2@u.co", "ADMIN")));
        when(identityGateway.findById(1L)).thenReturn(Optional.of(
                new UserContactDto(1L, "Admin", "admin@u.co", "ADMIN")));
        when(identityGateway.findById(9L)).thenReturn(Optional.of(
                new UserContactDto(9L, "Admin 2", "admin2@u.co", "ADMIN")));

        service.notifyQuestionSubmitted(new QuestionSubmittedEvent(100L, 2L));

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);
        verify(repository, times(2)).save(captor.capture());
        assertTrue(captor.getAllValues().stream().allMatch(
                n -> n.getType() == NotificationType.QUESTION_SUBMITTED));
        verify(emailSender).send(eq("admin@u.co"), contains("#100"));
        verify(emailSender).send(eq("admin2@u.co"), contains("#100"));
        verify(inAppSender).send(eq("user-1"), contains("#100"));
        verify(inAppSender).send(eq("user-9"), contains("#100"));
    }

    @Test
    void shouldNotNotifyAnyoneOnSubmissionWhenThereAreNoAdministrators() {
        when(identityGateway.findByRole("ADMIN")).thenReturn(List.of());

        service.notifyQuestionSubmitted(new QuestionSubmittedEvent(100L, 2L));

        verifyNoInteractions(repository, emailSender, inAppSender);
    }

    @Test
    void shouldNotifyAuthorAndAdministratorsWhenQuestionIsApproved() {
        when(identityGateway.findById(2L)).thenReturn(Optional.of(
                new UserContactDto(2L, "Autor", "autor@u.co", "AUTHOR")));
        when(identityGateway.findByRole("ADMIN")).thenReturn(List.of(
                new UserContactDto(1L, "Admin", "admin@u.co", "ADMIN")));
        when(identityGateway.findById(1L)).thenReturn(Optional.of(
                new UserContactDto(1L, "Admin", "admin@u.co", "ADMIN")));

        service.notifyReviewCompleted(
                new ReviewCompletedEvent(100L, 2L, "APPROVED", List.of()));

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);
        verify(repository, times(2)).save(captor.capture());

        assertTrue(captor.getAllValues().stream().allMatch(
                n -> n.getType() == NotificationType.REVIEW_APPROVED));
        verify(emailSender).send(eq("autor@u.co"), contains("APROBADA"));
        verify(emailSender).send(eq("admin@u.co"), contains("APROBADA"));
    }

    @Test
    void shouldIncludeObservationsWhenQuestionIsRejected() {
        when(identityGateway.findById(2L)).thenReturn(Optional.of(
                new UserContactDto(2L, "Autor", "autor@u.co", "AUTHOR")));
        when(identityGateway.findByRole("ADMIN")).thenReturn(List.of());

        service.notifyReviewCompleted(new ReviewCompletedEvent(
                100L, 2L, "REJECTED", List.of("Falta bibliografia")));

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);
        verify(repository).save(captor.capture());

        assertEquals(NotificationType.REVIEW_REJECTED,
                captor.getValue().getType());
        assertTrue(captor.getValue().getMessage()
                .contains("Falta bibliografia"));
        verify(emailSender).send(eq("autor@u.co"), contains("RECHAZADA"));
    }

    @Test
    void shouldListNotificationsOfRecipient() {
        Notification notification = new Notification(
                2L, null, NotificationType.REVIEW_APPROVED, "Hola");
        when(repository.findByRecipientId(2L))
                .thenReturn(List.of(notification));

        assertEquals(1, service.findByRecipient(2L).size());
    }

    @Test
    void shouldRequireRecipientToListNotifications() {
        assertThrows(IllegalArgumentException.class,
                () -> service.findByRecipient(null));
    }

    @Test
    void shouldMarkNotificationAsRead() {
        Notification notification = new Notification(
                2L, null, NotificationType.REVIEW_APPROVED, "Hola");
        when(repository.findById(1L)).thenReturn(Optional.of(notification));
        when(repository.save(notification)).thenReturn(notification);

        assertTrue(service.markAsRead(1L).isRead());
    }

    @Test
    void shouldFailMarkingUnknownNotification() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(NotificationNotFoundException.class,
                () -> service.markAsRead(9L));
    }

    @Test
    void shouldNotifyAdministratorsAndPreviousReviewersOnlyOnce() {
        when(identityGateway.findByRole("ADMIN")).thenReturn(List.of(
                new UserContactDto(1L, "Admin", "a@u.co", "ADMIN")));
        when(identityGateway.findById(any())).thenReturn(Optional.empty());

        // El revisor 4 aparece dos veces y el administrador 1 también es revisor previo.
        service.notifyQuestionResubmitted(
                new QuestionResubmittedEvent(100L, 2L, 2, List.of(4L, 4L, 1L)));

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(Notification.class);
        verify(repository, times(2)).save(captor.capture());

        assertEquals(
                List.of(1L, 4L),
                captor.getAllValues().stream()
                        .map(Notification::getRecipientId).toList());
        assertTrue(captor.getAllValues().stream().allMatch(n ->
                n.getType() == NotificationType.QUESTION_RESUBMITTED));
    }
}
