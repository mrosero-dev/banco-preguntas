package co.edu.unicauca.identity.infrastructure.jpa.mapper;

import org.springframework.stereotype.Component;

import co.edu.unicauca.identity.domain.model.User;
import co.edu.unicauca.identity.infrastructure.jpa.record.UserRecord;

@Component
public class UserMapper {

    public UserRecord toRecord(User user) {

        UserRecord record = new UserRecord();
        updateRecord(user, record);

        return record;
    }

    public void updateRecord(User user, UserRecord record) {
        record.setFullName(user.getFullName());
        record.setEmail(user.getEmail());
        record.setPasswordHash(user.getPasswordHash());
        record.setRole(user.getRole());
    }

    public User toDomain(UserRecord record) {
        return User.restore(
                record.getId(),
                record.getFullName(),
                record.getEmail(),
                record.getPasswordHash(),
                record.getRole()
        );
    }
}
