package co.edu.unicauca.notification.infrastructure.client;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import co.edu.unicauca.notification.application.client.UserContactDto;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class IdentityServiceClientTest {

    private MockRestServiceServer server;
    private IdentityServiceClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder =
                RestClient.builder().baseUrl("http://identity");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new IdentityServiceClient(builder.build());
    }

    @Test
    void shouldFindUserById() {
        server.expect(requestTo("http://identity/api/users/4"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        "{\"id\":4,\"fullName\":\"Rev\","
                                + "\"email\":\"r@u.co\",\"role\":\"REVIEWER\"}",
                        MediaType.APPLICATION_JSON));

        Optional<UserContactDto> user = client.findById(4L);

        assertTrue(user.isPresent());
        assertEquals("r@u.co", user.get().email());
    }

    @Test
    void shouldReturnEmptyWhenIdentityFails() {
        server.expect(requestTo("http://identity/api/users/9"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertTrue(client.findById(9L).isEmpty());
    }

    @Test
    void shouldFindUsersByRole() {
        server.expect(requestTo("http://identity/api/users?role=ADMIN"))
                .andRespond(withSuccess(
                        "[{\"id\":1,\"fullName\":\"Admin\","
                                + "\"email\":\"a@u.co\",\"role\":\"ADMIN\"}]",
                        MediaType.APPLICATION_JSON));

        List<UserContactDto> admins = client.findByRole("ADMIN");

        assertEquals(1, admins.size());
        assertEquals("ADMIN", admins.get(0).role());
    }

    @Test
    void shouldReturnEmptyListWhenIdentityFails() {
        server.expect(requestTo("http://identity/api/users?role=ADMIN"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertTrue(client.findByRole("ADMIN").isEmpty());
    }
}
