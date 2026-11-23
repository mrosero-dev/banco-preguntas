package co.edu.unicauca.identity.infrastructure.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.unicauca.identity.domain.model.Role;
import co.edu.unicauca.identity.infrastructure.jpa.record.UserRecord;

public interface UserJpaRepository extends JpaRepository<UserRecord, Long> {

    Optional<UserRecord> findByEmail(String email);

    List<UserRecord> findByRoleOrderByFullName(Role role);
}
