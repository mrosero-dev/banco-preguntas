package co.edu.unicauca.lisw2t5g02.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceUrlsTest {

    @Test
    void shouldProvideDefaultPortsOfEachMicroservice() {
        ServiceUrls urls = ServiceUrls.defaults();

        assertEquals("http://localhost:8080", urls.question());
        assertEquals("http://localhost:8081", urls.identity());
        assertEquals("http://localhost:8082", urls.review());
        assertEquals("http://localhost:8083", urls.notification());
    }

    @Test
    void shouldOverrideUrlWithSystemProperty() {
        System.setProperty("question.url", "http://example:9000");
        try {
            assertEquals("http://example:9000",
                    ServiceUrls.fromEnvironment().question());
        } finally {
            System.clearProperty("question.url");
        }
    }
}
