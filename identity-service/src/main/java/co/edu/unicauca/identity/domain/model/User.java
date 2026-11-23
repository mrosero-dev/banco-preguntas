package co.edu.unicauca.identity.domain.model;

/**
 * Represents a person who uses the question bank.
 */
public class User {

    private Long id;
    private String fullName;
    private String email;
    private String passwordHash;
    private Role role;

    public User(
            String fullName,
            String email,
            String passwordHash,
            Role role) {

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required");
        }

        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("A valid email is required");
        }

        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }

        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }

        this.fullName = fullName.trim();
        this.email = email.trim().toLowerCase();
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public boolean passwordMatches(String candidateHash) {
        return passwordHash.equals(candidateHash);
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

    public String getPasswordHash() {
        return passwordHash;
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
            String passwordHash,
            Role role) {

        User user = new User(fullName, email, passwordHash, role);
        user.id = id;

        return user;
    }
}
