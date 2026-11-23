package co.edu.unicauca.identity.api.dto;

import co.edu.unicauca.identity.domain.model.Role;
import co.edu.unicauca.identity.domain.model.User;

public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private Role role;

    public UserResponse(Long id, String fullName, String email, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole()
        );
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}
