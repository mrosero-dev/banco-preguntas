package co.edu.unicauca.notification.application;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicauca.notification.application.client.UserContactDto;
import co.edu.unicauca.notification.application.client.IdentityGateway;
import co.edu.unicauca.notification.application.event.QuestionResubmittedEvent;
import co.edu.unicauca.notification.application.event.ReviewAssignedEvent;
import co.edu.unicauca.notification.application.event.ReviewCompletedEvent;
import co.edu.unicauca.notification.domain.exception.NotificationNotFoundException;
import co.edu.unicauca.notification.domain.model.Notification;
import co.edu.unicauca.notification.domain.model.NotificationType;
import co.edu.unicauca.notification.domain.repository.NotificationRepository;
import co.edu.unicauca.notification.domain.sender.NotificationSender;

@Service
public class NotificationService {

    private static final String ADMIN_ROLE = "ADMIN";

    private final NotificationRepository repository;
    private final IdentityGateway identityGateway;
    private final NotificationSender emailSender;
    private final NotificationSender inAppSender;

    public NotificationService(
            NotificationRepository repository,
            IdentityGateway identityGateway,
            @Qualifier("emailNotificationSender")
            NotificationSender emailSender,
            @Qualifier("inAppNotificationSender")
            NotificationSender inAppSender) {

        this.repository = repository;
        this.identityGateway = identityGateway;
        this.emailSender = emailSender;
        this.inAppSender = inAppSender;
    }

    /**
     * Tells every assigned reviewer that a question is waiting for them.
     */
    @Transactional
    public void notifyReviewersAssigned(ReviewAssignedEvent event) {

        String message = "Se le asignó la pregunta #" + event.getQuestionId()
                + " para revisión.";

        for (Long reviewerId : event.getReviewerIds()) {
            deliver(reviewerId, NotificationType.REVIEW_ASSIGNED, message);
        }
    }

    /**
     * Tells the author and the administrators the result of the review.
     */
    @Transactional
    public void notifyReviewCompleted(ReviewCompletedEvent event) {

        boolean approved = "APPROVED".equalsIgnoreCase(event.getResult());

        NotificationType type = approved
                ? NotificationType.REVIEW_APPROVED
                : NotificationType.REVIEW_REJECTED;

        StringBuilder message = new StringBuilder("La pregunta #")
                .append(event.getQuestionId())
                .append(approved ? " fue APROBADA." : " fue RECHAZADA.");

        if (!event.getObservations().isEmpty()) {
            message.append(" Observaciones: ")
                    .append(String.join(" | ", event.getObservations()));
        }

        deliver(event.getAuthorId(), type, message.toString());

        for (UserContactDto admin : identityGateway.findByRole(ADMIN_ROLE)) {
            deliver(admin.id(), type, message.toString());
        }
    }

    /**
     * Tells the administrators and the reviewers of the previous round that
     * an improved version of the question is waiting for review.
     */
    @Transactional
    public void notifyQuestionResubmitted(QuestionResubmittedEvent event) {

        String message = "La pregunta #" + event.getQuestionId()
                + " tiene una nueva versión (" + event.getVersion()
                + ") pendiente de revisión.";

        Set<Long> recipients = new LinkedHashSet<>();

        for (UserContactDto admin : identityGateway.findByRole(ADMIN_ROLE)) {
            recipients.add(admin.id());
        }

        recipients.addAll(event.getPreviousReviewerIds());

        for (Long recipientId : recipients) {
            deliver(recipientId, NotificationType.QUESTION_RESUBMITTED, message);
        }
    }

    @Transactional(readOnly = true)
    public List<Notification> findByRecipient(Long recipientId) {

        if (recipientId == null) {
            throw new IllegalArgumentException("Recipient ID is required");
        }

        return repository.findByRecipientId(recipientId);
    }

    @Transactional
    public Notification markAsRead(Long notificationId, Long recipientId) {
        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification not found: " + notificationId));
        if (!notification.getRecipientId().equals(recipientId)) {
            throw new org.springframework.security.access.AccessDeniedException(
                    "No puedes modificar notificaciones de otro usuario.");
        }
        notification.markAsRead();
        return repository.save(notification);
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {

        Notification notification = repository.findById(notificationId)
                .orElseThrow(() -> new NotificationNotFoundException(
                        "Notification not found: " + notificationId
                ));

        notification.markAsRead();

        return repository.save(notification);
    }

    private void deliver(
            Long recipientId,
            NotificationType type,
            String message) {

        Optional<UserContactDto> contact = identityGateway.findById(recipientId);

        String email = contact.map(UserContactDto::email).orElse(null);

        repository.save(
                new Notification(recipientId, email, type, message)
        );

        inAppSender.send("user-" + recipientId, message);

        if (email != null && !email.isBlank()) {
            emailSender.send(email, message);
        }
    }
}
