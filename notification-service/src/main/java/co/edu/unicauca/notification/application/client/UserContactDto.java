package co.edu.unicauca.notification.application.client;

/**
 * Contact data of a user, as published by the Identity Service.
 */
public record UserContactDto(Long id, String fullName, String email, String role) {
}
