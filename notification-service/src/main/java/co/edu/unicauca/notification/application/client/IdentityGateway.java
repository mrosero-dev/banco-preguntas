package co.edu.unicauca.notification.application.client;

import java.util.List;
import java.util.Optional;

/**
 * Obtains contact data of users from the Identity Service.
 */
public interface IdentityGateway {

    Optional<UserContactDto> findById(Long userId);

    List<UserContactDto> findByRole(String role);
}
