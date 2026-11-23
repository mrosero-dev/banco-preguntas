package co.edu.unicauca.lisw2t5g02.client;

import java.util.List;

import com.fasterxml.jackson.databind.node.ObjectNode;

import co.edu.unicauca.lisw2t5g02.model.Role;
import co.edu.unicauca.lisw2t5g02.model.User;

/** REST client of the Identity Service. */
public class IdentityClient {

    private final RestClient http;
    private final String baseUrl;
    private final String tokenUrl;

    public IdentityClient(RestClient http, String baseUrl, String tokenUrl) {
        this.http = http;
        this.baseUrl = baseUrl;
        this.tokenUrl = tokenUrl;
    }

    public User login(String email, String password) {
        http.setBearerToken(null);
        String form = "grant_type=password&client_id=banco-preguntas-client"
                + "&username=" + java.net.URLEncoder.encode(email, java.nio.charset.StandardCharsets.UTF_8)
                + "&password=" + java.net.URLEncoder.encode(password, java.nio.charset.StandardCharsets.UTF_8);
        com.fasterxml.jackson.databind.JsonNode tokenResponse;
        try {
            tokenResponse = http.postForm(tokenUrl, form);
        } catch (ApiException error) {
            if (error.getStatus() == 400 || error.getStatus() == 401) {
                throw new ApiException(401, "Credenciales inválidas.");
            }
            throw error;
        }
        String accessToken = tokenResponse.path("access_token").asText("");
        if (accessToken.isBlank()) throw new ApiException(401, "Keycloak no devolvió un access token.");
        http.setBearerToken(accessToken);
        return Mappers.toUser(http.get(baseUrl + "/api/users/me"));
    }

    public List<User> findUsers(Role role) {
        String query = role == null ? "" : "?role=" + role.name();
        return Mappers.toUsers(http.get(baseUrl + "/api/users" + query));
    }

    public User findById(long id) {
        return Mappers.toUser(http.get(baseUrl + "/api/users/" + id));
    }
}
