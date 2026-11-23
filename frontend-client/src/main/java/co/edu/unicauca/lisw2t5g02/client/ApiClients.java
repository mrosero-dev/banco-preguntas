package co.edu.unicauca.lisw2t5g02.client;

import co.edu.unicauca.lisw2t5g02.config.ServiceUrls;

/** Groups the clients of the four microservices. */
public record ApiClients(
        IdentityClient identity,
        QuestionClient question,
        ReviewClient review,
        NotificationClient notification,
        RestClient http) {

    public static ApiClients create(ServiceUrls urls) {
        RestClient http = new RestClient();

        String tokenUrl = System.getenv().getOrDefault("KEYCLOAK_TOKEN_URL",
                "http://localhost:8180/realms/banco-preguntas/protocol/openid-connect/token");
        return new ApiClients(
                new IdentityClient(http, urls.identity(), tokenUrl),
                new QuestionClient(http, urls.question()),
                new ReviewClient(http, urls.review()),
                new NotificationClient(http, urls.notification()),
                http);
    }

    /** Closes the session: the token is removed from memory right away. */
    public void logout() {
        http.clearSession();
    }

    /** Registers what to do when the server reports an expired session. */
    public void onSessionExpired(Runnable callback) {
        http.setOnSessionExpired(callback);
    }
}
