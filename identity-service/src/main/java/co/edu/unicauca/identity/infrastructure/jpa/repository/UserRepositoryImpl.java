package co.edu.unicauca.identity.infrastructure.jpa.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import co.edu.unicauca.identity.domain.model.Role;
import co.edu.unicauca.identity.domain.model.User;
import co.edu.unicauca.identity.domain.repository.UserRepository;
import co.edu.unicauca.identity.infrastructure.jpa.mapper.UserMapper;
import co.edu.unicauca.identity.infrastructure.jpa.record.UserRecord;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    public UserRepositoryImpl(
            UserJpaRepository jpaRepository,
            UserMapper mapper) {

        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {

        UserRecord record;

        if (user.getId() == null) {
            record = mapper.toRecord(user);
        } else {
            record = jpaRepository.findById(user.getId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "User not found: " + user.getId()
                    ));
            mapper.updateRecord(user, record);
        }

        return mapper.toDomain(jpaRepository.save(record));
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<User> findByRole(Role role) {
        return jpaRepository.findByRoleOrderByFullName(role).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
