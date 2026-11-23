package co.edu.unicauca.identity.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.unicauca.identity.domain.exception.InvalidCredentialsException;
import co.edu.unicauca.identity.domain.exception.UserNotFoundException;
import co.edu.unicauca.identity.domain.model.Role;
import co.edu.unicauca.identity.domain.model.User;
import co.edu.unicauca.identity.domain.repository.UserRepository;
import co.edu.unicauca.identity.domain.security.PasswordHasher;

@Service
public class IdentityService {

    private final UserRepository repository;
    private final PasswordHasher passwordHasher;

    public IdentityService(
            UserRepository repository,
            PasswordHasher passwordHasher) {

        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    /**
     * Registers a new user. The password is stored hashed.
     */
    @Transactional
    public User register(
            String fullName,
            String email,
            String rawPassword,
            Role role) {

        if (rawPassword == null || rawPassword.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (email != null && repository
                .findByEmail(email.trim().toLowerCase()).isPresent()) {
            throw new IllegalArgumentException(
                    "A user with this email already exists"
            );
        }

        User user = new User(
                fullName,
                email,
                passwordHasher.hash(rawPassword),
                role
        );

        return repository.save(user);
    }

    @Transactional(readOnly = true)
    public User authenticate(String email, String rawPassword) {

        if (email == null || rawPassword == null) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        User user = repository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid credentials")
                );

        if (!user.passwordMatches(passwordHasher.hash(rawPassword))) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        return user;
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
