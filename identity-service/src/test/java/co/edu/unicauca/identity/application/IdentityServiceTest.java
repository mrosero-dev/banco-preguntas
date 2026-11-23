package co.edu.unicauca.identity.application;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unicauca.identity.domain.exception.InvalidCredentialsException;
import co.edu.unicauca.identity.domain.exception.UserNotFoundException;
import co.edu.unicauca.identity.domain.model.Role;
import co.edu.unicauca.identity.domain.model.User;
import co.edu.unicauca.identity.domain.repository.UserRepository;
import co.edu.unicauca.identity.domain.security.PasswordHasher;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordHasher hasher;

    private IdentityService service;

    @BeforeEach
    void setUp() {
        service = new IdentityService(repository, hasher);
    }

    @Test
    void shouldRegisterUserWithHashedPassword() {
        when(hasher.hash("secret")).thenReturn("hashed");
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = service.register("Ana", "A@b.com", "secret", Role.AUTHOR);

        assertEquals("hashed", user.getPasswordHash());
        assertEquals("a@b.com", user.getEmail());
    }

    @Test
    void shouldRejectDuplicatedEmail() {
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(
                new User("Ana", "a@b.com", "hash", Role.AUTHOR)));

        assertThrows(IllegalArgumentException.class,
                () -> service.register("Ana", "a@b.com", "secret", Role.AUTHOR));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldAuthenticateWithValidCredentials() {
        User stored = new User("Ana", "a@b.com", "hashed", Role.AUTHOR);
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(stored));
        when(hasher.hash("secret")).thenReturn("hashed");

        assertSame(stored, service.authenticate("a@b.com", "secret"));
    }

    @Test
    void shouldRejectWrongPassword() {
        User stored = new User("Ana", "a@b.com", "hashed", Role.AUTHOR);
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(stored));
        when(hasher.hash("wrong")).thenReturn("other");

        assertThrows(InvalidCredentialsException.class,
                () -> service.authenticate("a@b.com", "wrong"));
    }

    @Test
    void shouldRejectUnknownEmail() {
        when(repository.findByEmail("x@b.com")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> service.authenticate("x@b.com", "secret"));
    }

    @Test
    void shouldFindUserById() {
        User stored = new User("Ana", "a@b.com", "hashed", Role.AUTHOR);
        when(repository.findById(1L)).thenReturn(Optional.of(stored));

        assertSame(stored, service.findById(1L));
    }

    @Test
    void shouldFailWhenUserDoesNotExist() {
        when(repository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.findById(9L));
    }

    @Test
    void shouldListUsersByRole() {
        User reviewer = new User("Rev", "r@b.com", "hashed", Role.REVIEWER);
        when(repository.findByRole(Role.REVIEWER))
                .thenReturn(List.of(reviewer));

        assertEquals(List.of(reviewer), service.findUsers(Role.REVIEWER));
    }

    @Test
    void shouldListAllUsersWhenNoRoleIsGiven() {
        when(repository.findAll()).thenReturn(List.of());

        assertTrue(service.findUsers(null).isEmpty());
        verify(repository).findAll();
    }
}
