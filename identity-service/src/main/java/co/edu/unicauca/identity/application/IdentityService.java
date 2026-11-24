package co.edu.unicauca.identity.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicauca.identity.domain.exception.UserNotFoundException;
import co.edu.unicauca.identity.domain.model.Role;
import co.edu.unicauca.identity.domain.model.User;
import co.edu.unicauca.identity.domain.repository.UserRepository;

@Service
public class IdentityService {

    private final UserRepository repository;

    public IdentityService(UserRepository repository) {
        this.repository = repository;
    }

    /**
     * Registers the local profile of a user. Credentials live in Keycloak.
     */
    @Transactional
    public User register(
            String fullName,
            String email,
            Role role) {

        if (email != null && repository
                .findByEmail(email.trim().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException(
                    "A user with this email already exists"
            );
        }

        User user = new User(
                fullName,
                email,
                role
        );

        return repository.save(user);
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found: " + id)
                );
    }

    /**
     * Lists the users, optionally restricted to one role.
     */
    @Transactional(readOnly = true)
    public List<User> findUsers(Role role) {
        return role == null
                ? repository.findAll()
                : repository.findByRole(role);
    }
}
