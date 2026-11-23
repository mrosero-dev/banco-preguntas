package co.edu.unicauca.notification.config;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import co.edu.unicauca.notification.application.client.IdentityGateway;
import co.edu.unicauca.notification.infrastructure.client.IdentityServiceClient;

@Configuration
public class IdentityClientConfig {
    @Bean
    public IdentityGateway identityGateway(
            @Value("${identity.service.url}") String identityUrl,
            @Value("${keycloak.token-url:http://localhost:8180/realms/banco-preguntas/protocol/openid-connect/token}") String tokenUrl,
            @Value("${keycloak.client-id:notification-service}") String clientId,
            @Value("${keycloak.client-secret:notification-service-secret-dev}") String clientSecret) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); factory.setReadTimeout(5000);
        RestClient tokenClient = RestClient.create();
        return new IdentityServiceClient(RestClient.builder().baseUrl(identityUrl)
                .requestFactory(factory)
                .requestInterceptor((request, body, execution) -> {
                    Map<?, ?> token = tokenClient.post().uri(tokenUrl)
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .body("grant_type=client_credentials&client_id=" + clientId + "&client_secret=" + clientSecret)
                            .retrieve().body(Map.class);
                    Object accessToken = token == null ? null : token.get("access_token");
                    if (accessToken == null) throw new IllegalStateException("Keycloak service token not available");
                    request.getHeaders().setBearerAuth(accessToken.toString());
                    return execution.execute(request, body);
                }).build());
    }
}
