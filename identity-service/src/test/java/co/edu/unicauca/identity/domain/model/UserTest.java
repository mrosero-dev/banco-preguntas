package co.edu.unicauca.identity.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void shouldCreateUserNormalizingEmail() {
        User user = new User(" Ana ", " ANA@Mail.com ", Role.AUTHOR);

        assertEquals("Ana", user.getFullName());
        assertEquals("ana@mail.com", user.getEmail());
        assertEquals(Role.AUTHOR, user.getRole());
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class,
                () -> new User(" ", "a@b.com", Role.AUTHOR));
    }

    @Test
    void shouldRejectInvalidEmail() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("Ana", "invalid", Role.AUTHOR));
    }

    @Test
    void shouldRejectMissingRole() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("Ana", "a@b.com", null));
    }

    @Test
    void shouldRestoreUserWithIdentifier() {
        User user = User.restore(7L, "Ana", "a@b.com", Role.ADMIN);

        assertEquals(7L, user.getId());
    }
}
