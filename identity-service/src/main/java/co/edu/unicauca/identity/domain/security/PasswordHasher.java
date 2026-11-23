package co.edu.unicauca.identity.domain.security;

/**
 * Defines the contract for hashing passwords.
 */
public interface PasswordHasher {

    String hash(String rawPassword);
}
