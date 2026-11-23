package co.edu.unicauca.notification.api.controller;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import co.edu.unicauca.notification.api.NoSecurityWebTestConfig;

import co.edu.unicauca.notification.application.NotificationService;
import co.edu.unicauca.notification.domain.exception.NotificationNotFoundException;
import co.edu.unicauca.notification.domain.model.Notification;
import co.edu.unicauca.notification.domain.model.NotificationType;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@Import(NoSecurityWebTestConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService service;

    @Test
    void shouldListNotificationsOfRecipient() throws Exception {
        when(service.findByRecipient(2L)).thenReturn(List.of(
                new Notification(2L, null,
                        NotificationType.REVIEW_APPROVED, "Aprobada")));

        mockMvc.perform(get("/api/notifications").param("recipientId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].type").value("REVIEW_APPROVED"))
                .andExpect(jsonPath("$[0].read").value(false));
    }

    @Test
    void shouldMarkNotificationAsRead() throws Exception {
        Notification notification = new Notification(
                2L, null, NotificationType.REVIEW_APPROVED, "Aprobada");
        notification.markAsRead();
        when(service.markAsRead(1L)).thenReturn(notification);

        mockMvc.perform(patch("/api/notifications/1/read"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.read").value(true));
    }

    @Test
    void shouldReturnNotFoundForUnknownNotification() throws Exception {
        when(service.markAsRead(9L)).thenThrow(
                new NotificationNotFoundException("Notification not found: 9"));

        mockMvc.perform(patch("/api/notifications/9/read"))
                .andExpect(status().isNotFound());
    }
}
