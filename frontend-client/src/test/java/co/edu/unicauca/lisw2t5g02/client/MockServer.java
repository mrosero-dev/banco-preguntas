package co.edu.unicauca.lisw2t5g02.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpServer;

/** Tiny HTTP server used to test the REST clients without the backend. */
final class MockServer implements AutoCloseable {

    private final HttpServer server;
    private final Map<String, String[]> routes = new HashMap<>();

    String lastMethod;
    String lastUri;
    String lastBody;

    MockServer() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/", exchange -> {
            lastMethod = exchange.getRequestMethod();
            lastUri = exchange.getRequestURI().toString();
            lastBody = new String(
                    exchange.getRequestBody().readAllBytes(),
                    StandardCharsets.UTF_8);

            String[] route = routes.get(lastMethod + " " + lastUri);
            int status = route == null ? 404 : Integer.parseInt(route[0]);
            byte[] body = (route == null ? "Not found" : route[1])
                    .getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(status, body.length == 0 ? -1 : body.length);
            if (body.length > 0) {
                exchange.getResponseBody().write(body);
            }
            exchange.close();
        });
        server.start();
    }

    /** Registers the answer for "METHOD /path?query". */
    MockServer on(String method, String uri, int status, String body) {
        routes.put(method + " " + uri, new String[] {String.valueOf(status), body});
        return this;
    }

    String url() {
        return "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @Override
    public void close() {
        server.stop(0);
    }
}
