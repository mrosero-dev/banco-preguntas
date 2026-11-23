package co.edu.unicauca.identity.domain.repository;

import java.util.List;
import java.util.Optional;

import co.edu.unicauca.identity.domain.model.Role;
import co.edu.unicauca.identity.domain.model.User;

/**
 * Defines the contract for storing and retrieving users.
 */
public interface UserRepository {

    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    List<User> findByRole(Role role);

    long count();
}
