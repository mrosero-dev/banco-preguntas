package co.edu.unicauca.lisw2t5g02.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;

/**
 * Minimal JSON-over-HTTP client shared by all service clients.
 * Services answer errors with plain text, which becomes the exception message.
 */
public class RestClient {

    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(3))
            .build();

    private final ObjectMapper mapper = new ObjectMapper();
    private volatile String bearerToken;

    private volatile Runnable onSessionExpired = () -> { };

    public void setBearerToken(String token) { this.bearerToken = token; }

    /** Removes the token immediately (used when the user closes the session). */
    public void clearSession() { this.bearerToken = null; }

    /**
     * Callback invoked once when an authenticated call is answered with 401
     * (expired or invalid token). It runs on the calling thread.
     */
    public void setOnSessionExpired(Runnable callback) {
        this.onSessionExpired = callback == null ? () -> { } : callback;
    }

    public JsonNode get(String url) {
        return send(request(url).GET().build(), url);
    }

    public JsonNode post(String url, Object body) {
        HttpRequest.BodyPublisher publisher = body == null
                ? HttpRequest.BodyPublishers.noBody()
                : json(body);

        return send(request(url).POST(publisher).build(), url);
    }

    public JsonNode put(String url, Object body) {
        return send(request(url).PUT(json(body)).build(), url);
    }

    public JsonNode patch(String url) {
        return send(request(url)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build(), url);
    }

    public JsonNode postForm(String url, String formBody) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(TIMEOUT).header("Accept", "application/json")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(formBody)).build();
        return send(request, url);
    }

    public ObjectMapper mapper() {
        return mapper;
    }

    private HttpRequest.Builder request(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url))
                .timeout(TIMEOUT)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json");
        String token = bearerToken;
        if (token != null && !token.isBlank()) builder.header("Authorization", "Bearer " + token);
        return builder;
    }

    private HttpRequest.BodyPublisher json(Object body) {
        try {
            return HttpRequest.BodyPublishers.ofString(
                    mapper.writeValueAsString(body));
        } catch (IOException exception) {
            throw new IllegalArgumentException(
                    "No se pudo convertir la solicitud a JSON", exception);
        }
    }

    /**
     * A 401 on a call that carried a token means the JWT expired: the token is
     * discarded and the callback asks the user to authenticate again. Failed
     * logins carry no token, so they never reach this branch.
     */
    private synchronized void handleUnauthorized() {
        if (bearerToken == null) {
            return;
        }
        bearerToken = null;
        onSessionExpired.run();
    }

    private JsonNode send(HttpRequest request, String url) {

        HttpResponse<String> response;

        try {
            response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new ApiException(0, "Operación interrumpida");
        } catch (IOException exception) {
            throw new ApiException(0,
                    "No se pudo conectar con el servicio: " + url);
        }

        String body = response.body() == null ? "" : response.body().trim();

        if (response.statusCode() == 401) {
            handleUnauthorized();
        }

        if (response.statusCode() / 100 != 2) {
            throw new ApiException(
                    response.statusCode(),
                    body.isEmpty() ? "Error HTTP " + response.statusCode() : body);
        }

        if (body.isEmpty()) {
            return MissingNode.getInstance();
        }

        try {
            return mapper.readTree(body);
        } catch (IOException exception) {
            throw new ApiException(response.statusCode(),
                    "Respuesta no válida del servicio: " + url);
        }
    }
}
