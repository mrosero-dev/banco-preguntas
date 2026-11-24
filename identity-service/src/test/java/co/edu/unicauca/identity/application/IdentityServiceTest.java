package co.edu.unicauca.identity.application;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import co.edu.unicauca.identity.domain.exception.UserNotFoundException;
import co.edu.unicauca.identity.domain.model.Role;
import co.edu.unicauca.identity.domain.model.User;
import co.edu.unicauca.identity.domain.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IdentityServiceTest {

    @Mock
    private UserRepository repository;

    private IdentityService service;

    @BeforeEach
    void setUp() {
        service = new IdentityService(repository);
    }

    @Test
    void shouldRegisterUserNormalizingEmail() {
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.empty());
        when(repository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        User user = service.register("Ana", "A@b.com", Role.AUTHOR);

        assertEquals("Ana", user.getFullName());
        assertEquals("a@b.com", user.getEmail());
    }

    @Test
    void shouldRejectDuplicatedEmail() {
        when(repository.findByEmail("a@b.com")).thenReturn(Optional.of(
                new User("Ana", "a@b.com", Role.AUTHOR)));

        assertThrows(IllegalArgumentException.class,
                () -> service.register("Ana", "a@b.com", Role.AUTHOR));

        verify(repository, never()).save(any());
    }

    @Test
    void shouldFindUserById() {
        User stored = new User("Ana", "a@b.com", Role.AUTHOR);
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
        User reviewer = new User("Rev", "r@b.com", Role.REVIEWER);
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
