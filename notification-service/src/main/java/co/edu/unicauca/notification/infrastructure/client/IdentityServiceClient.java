package co.edu.unicauca.notification.infrastructure.client;

import java.util.List;
import java.util.Optional;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import co.edu.unicauca.notification.application.client.UserContactDto;
import co.edu.unicauca.notification.application.client.IdentityGateway;

/**
 * Reads user contact data from the Identity Service through REST.
 * When Identity is unavailable the notification is still stored in-app.
 */
public class IdentityServiceClient implements IdentityGateway {

    private final RestClient restClient;

    public IdentityServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public Optional<UserContactDto> findById(Long userId) {

        try {
            return Optional.ofNullable(
                    restClient.get()
                            .uri("/api/users/{id}", userId)
                            .retrieve()
                            .body(UserContactDto.class)
            );
        } catch (RestClientException exception) {
            System.out.println(
                    "Identity Service unavailable for user " + userId
                            + ": " + exception.getMessage()
            );
            return Optional.empty();
        }
    }

    @Override
    public List<UserContactDto> findByRole(String role) {

        try {
            List<UserContactDto> users = restClient.get()
                    .uri("/api/users?role={role}", role)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<UserContactDto>>() {
                    });

            return users == null ? List.of() : users;

        } catch (RestClientException exception) {
            System.out.println(
                    "Identity Service unavailable for role " + role
                            + ": " + exception.getMessage()
            );
            return List.of();
        }
    }
}
