package co.edu.unicauca.lisw2t5g02.client;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RestClientTest {

    @Test
    void shouldReadJsonFromGet() throws Exception {
        try (MockServer server = new MockServer()
                .on("GET", "/ping", 200, "{\"ok\":true}")) {

            assertTrue(new RestClient().get(server.url() + "/ping")
                    .path("ok").asBoolean());
        }
    }

    @Test
    void shouldUseServiceTextAsErrorMessage() throws Exception {
        try (MockServer server = new MockServer()
                .on("POST", "/x", 400, "statement: must not be blank")) {

            ApiException error = assertThrows(ApiException.class,
                    () -> new RestClient().post(server.url() + "/x", null));

            assertEquals(400, error.getStatus());
            assertEquals("statement: must not be blank", error.getMessage());
        }
    }

    @Test
    void shouldSendJsonBodyWithPutAndPost() throws Exception {
        try (MockServer server = new MockServer()
                .on("PUT", "/q/1", 200, "{}")
                .on("POST", "/q", 200, "{}")) {

            RestClient client = new RestClient();
            java.util.Map<String, Object> body = java.util.Map.of("a", 1);

            client.put(server.url() + "/q/1", body);
            assertEquals("PUT", server.lastMethod);
            assertEquals("{\"a\":1}", server.lastBody);

            client.post(server.url() + "/q", body);
            assertEquals("POST", server.lastMethod);
            assertEquals("{\"a\":1}", server.lastBody);
        }
    }

    @Test
    void shouldSendPatchWithoutBody() throws Exception {
        try (MockServer server = new MockServer()
                .on("PATCH", "/n/1/read", 200, "{\"read\":true}")) {

            assertTrue(new RestClient().patch(server.url() + "/n/1/read")
                    .path("read").asBoolean());
            assertEquals("PATCH", server.lastMethod);
        }
    }

    @Test
    void shouldReturnMissingNodeWhenBodyIsEmpty() throws Exception {
        try (MockServer server = new MockServer().on("GET", "/e", 200, "")) {

            assertTrue(new RestClient().get(server.url() + "/e").isMissingNode());
        }
    }

    @Test
    void shouldReportUnreachableService() {
        ApiException error = assertThrows(ApiException.class,
                () -> new RestClient().get("http://127.0.0.1:1/none"));

        assertEquals(0, error.getStatus());
        assertTrue(error.getMessage().contains("No se pudo conectar"));
    }

    @Test
    void shouldDiscardTokenAndNotifyWhenAuthenticatedCallGets401() throws Exception {
        try (MockServer server = new MockServer()
                .on("GET", "/me", 401, "")) {

            RestClient client = new RestClient();
            java.util.concurrent.atomic.AtomicInteger expired =
                    new java.util.concurrent.atomic.AtomicInteger();
            client.setOnSessionExpired(expired::incrementAndGet);
            client.setBearerToken("token-vencido");

            ApiException error = assertThrows(ApiException.class,
                    () -> client.get(server.url() + "/me"));
            assertEquals(401, error.getStatus());
            assertEquals(1, expired.get());

            // El token ya se descartó: otro 401 no vuelve a avisar.
            assertThrows(ApiException.class,
                    () -> client.get(server.url() + "/me"));
            assertEquals(1, expired.get());
        }
    }

    @Test
    void shouldNotReportExpiredSessionWhenThereIsNoToken() throws Exception {
        try (MockServer server = new MockServer()
                .on("POST", "/token", 401, "")) {

            RestClient client = new RestClient();
            java.util.concurrent.atomic.AtomicInteger expired =
                    new java.util.concurrent.atomic.AtomicInteger();
            client.setOnSessionExpired(expired::incrementAndGet);

            assertThrows(ApiException.class,
                    () -> client.postForm(server.url() + "/token", "a=b"));
            assertEquals(0, expired.get());
        }
    }

    @Test
    void clearSessionShouldRemoveTheTokenImmediately() throws Exception {
        try (MockServer server = new MockServer()
                .on("GET", "/me", 200, "{}")) {

            RestClient client = new RestClient();
            java.util.concurrent.atomic.AtomicInteger expired =
                    new java.util.concurrent.atomic.AtomicInteger();
            client.setOnSessionExpired(expired::incrementAndGet);
            client.setBearerToken("token");
            client.clearSession();

            client.get(server.url() + "/me");
            assertEquals(0, expired.get());
        }
    }
}
