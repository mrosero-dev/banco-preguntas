package co.edu.unicauca.identity.infrastructure.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

import co.edu.unicauca.identity.domain.security.PasswordHasher;

/**
 * Hashes passwords with SHA-256 (academic scope; use BCrypt in production).
 */
@Component
public class Sha256PasswordHasher implements PasswordHasher {

    @Override
    public String hash(String rawPassword) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            return HexFormat.of().formatHex(
                    digest.digest(
                            rawPassword.getBytes(StandardCharsets.UTF_8)
                    )
            );

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 is not available", exception
            );
        }
    }
}
