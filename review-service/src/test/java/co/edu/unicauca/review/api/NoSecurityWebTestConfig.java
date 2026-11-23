package co.edu.unicauca.review.api;

import java.util.List;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Las pruebas de controlador excluyen la configuración de seguridad, y sin ella
 * Spring MVC no sabe resolver los parámetros {@code @AuthenticationPrincipal Jwt}
 * (los trataría como un objeto de formulario y responde 400). Este resolvedor
 * entrega null cuando no hay autenticación, que es el caso que los controladores
 * ya contemplan para las pruebas aisladas.
 */
@TestConfiguration
public class NoSecurityWebTestConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthenticationPrincipalArgumentResolver());
    }
}
