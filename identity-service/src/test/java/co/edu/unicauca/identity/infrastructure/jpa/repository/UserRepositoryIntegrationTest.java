package co.edu.unicauca.identity.infrastructure.jpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import co.edu.unicauca.identity.domain.model.Role;
import co.edu.unicauca.identity.domain.model.User;
import co.edu.unicauca.identity.domain.repository.UserRepository;
import co.edu.unicauca.identity.infrastructure.jpa.mapper.UserMapper;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({UserRepositoryImpl.class, UserMapper.class})
class UserRepositoryIntegrationTest {

    @Autowired
    private UserRepository repository;

    @Test
    void shouldPersistAndFindByEmail() {
        User saved = repository.save(
                new User("Ana", "ana@b.com", Role.AUTHOR));

        assertNotNull(saved.getId());
        assertEquals("Ana",
                repository.findByEmail("ana@b.com").orElseThrow().getFullName());
    }

    @Test
    void shouldFilterByRole() {
        repository.save(new User("Rev A", "ra@b.com", Role.REVIEWER));
        repository.save(new User("Rev B", "rb@b.com", Role.REVIEWER));
        repository.save(new User("Ana", "ana@b.com", Role.AUTHOR));

        assertEquals(2, repository.findByRole(Role.REVIEWER).size());
        assertEquals(3, repository.findAll().size());
        assertEquals(3, repository.count());
    }

    @Test
    void shouldUpdateExistingUser() {
        User saved = repository.save(
                new User("Ana", "ana@b.com", Role.AUTHOR));

        User changed = User.restore(saved.getId(), "Ana Maria",
                "ana@b.com", Role.AUTHOR);

        assertEquals("Ana Maria", repository.save(changed).getFullName());
        assertEquals(1, repository.count());
    }
}
