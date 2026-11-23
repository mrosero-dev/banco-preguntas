package co.edu.unicauca.identity.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.unicauca.identity.application.IdentityService;
import co.edu.unicauca.identity.domain.model.Role;
import co.edu.unicauca.identity.domain.repository.UserRepository;

/**
 * Loads demo users the first time the in-memory database starts.
 * Identifiers are assigned in order: 1 admin, 2-3 authors, 4-6 reviewers.
 */
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedUsers(
            IdentityService identityService,
            UserRepository repository) {

        return args -> {

            if (repository.count() > 0) {
                return;
            }

            identityService.register("Administrador", "admin@unicauca.edu.co",
                    "admin123", Role.ADMIN);
            identityService.register("Autor Uno", "autor1@unicauca.edu.co",
                    "autor123", Role.AUTHOR);
            identityService.register("Autor Dos", "autor2@unicauca.edu.co",
                    "autor123", Role.AUTHOR);
            identityService.register("Revisor Uno", "revisor1@unicauca.edu.co",
                    "revisor123", Role.REVIEWER);
            identityService.register("Revisor Dos", "revisor2@unicauca.edu.co",
                    "revisor123", Role.REVIEWER);
            identityService.register("Revisor Tres", "revisor3@unicauca.edu.co",
                    "revisor123", Role.REVIEWER);
        };
    }
}
