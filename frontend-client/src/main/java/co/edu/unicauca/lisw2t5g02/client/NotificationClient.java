package co.edu.unicauca.lisw2t5g02.client;

import java.util.List;

import co.edu.unicauca.lisw2t5g02.model.Notification;

/** REST client of the Notification Service. */
public class NotificationClient {

    private final RestClient http;
    private final String baseUrl;

    public NotificationClient(RestClient http, String baseUrl) {
        this.http = http;
        this.baseUrl = baseUrl + "/api/notifications";
    }

    public List<Notification> findByRecipient(long recipientId) {
        return Mappers.toNotifications(
                http.get(baseUrl + "?recipientId=" + recipientId));
    }

    public Notification markAsRead(long notificationId) {
        return Mappers.toNotification(
                http.patch(baseUrl + "/" + notificationId + "/read"));
    }
}
