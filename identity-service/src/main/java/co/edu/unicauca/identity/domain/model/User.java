package co.edu.unicauca.identity.domain.model;

/**
 * Represents the local profile of a person who uses the question bank.
 * Credentials are managed exclusively by Keycloak.
 */
public class User {

    private Long id;
    private String fullName;
    private String email;
    private Role role;

    public User(
            String fullName,
            String email,
            Role role) {

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }

        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("A valid email is required");
        }

        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }

        this.fullName = fullName.trim();
        this.email = email.trim().toLowerCase();
        this.role = role;
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

    /**
     * Restores a user from previously persisted information.
     */
    public static User restore(
            Long id,
            String fullName,
            String email,
            Role role) {

        User user = new User(fullName, email, role);
        user.id = id;

        return user;
    }
}
