package co.edu.unicauca.lisw2t5g02.client;

import org.junit.jupiter.api.Test;

import co.edu.unicauca.lisw2t5g02.model.Role;
import co.edu.unicauca.lisw2t5g02.model.User;

import static org.junit.jupiter.api.Assertions.*;

class IdentityClientTest {

    private static final String USER =
            "{\"id\":4,\"fullName\":\"Revisor Uno\","
                    + "\"email\":\"revisor1@unicauca.edu.co\",\"role\":\"REVIEWER\"}";

    @Test
    void shouldLoginAndMapUser() throws Exception {
        try (MockServer server = new MockServer()
                .on("POST", "/realms/banco-preguntas/protocol/openid-connect/token", 200, "{\"access_token\":\"demo-token\"}")
                .on("GET", "/api/users/me", 200, USER)) {

            User user = new IdentityClient(new RestClient(), server.url(),
                    server.url() + "/realms/banco-preguntas/protocol/openid-connect/token")
                    .login("revisor1@unicauca.edu.co", "revisor123");

            assertEquals(4L, user.id());
            assertEquals(Role.REVIEWER, user.role());
        }
    }

    @Test
    void shouldReportInvalidCredentials() throws Exception {
        try (MockServer server = new MockServer()
                .on("POST", "/realms/banco-preguntas/protocol/openid-connect/token", 400, "{\"error\":\"invalid_grant\"}")) {

            ApiException error = assertThrows(ApiException.class,
                    () -> new IdentityClient(new RestClient(), server.url(),
                            server.url() + "/realms/banco-preguntas/protocol/openid-connect/token")
                            .login("a@b.com", "bad"));

            assertEquals(401, error.getStatus());
        }
    }

    @Test
    void shouldListUsersByRole() throws Exception {
        try (MockServer server = new MockServer()
                .on("GET", "/api/users?role=REVIEWER", 200, "[" + USER + "]")) {

            var users = new IdentityClient(new RestClient(), server.url(), server.url() + "/token")
                    .findUsers(Role.REVIEWER);

            assertEquals(1, users.size());
            assertEquals("Revisor Uno", users.get(0).fullName());
        }
    }

    @Test
    void shouldFindUserById() throws Exception {
        try (MockServer server = new MockServer()
                .on("GET", "/api/users/4", 200, USER)) {

            assertEquals(4L, new IdentityClient(new RestClient(), server.url(), server.url() + "/token")
                    .findById(4).id());
        }
    }
}
