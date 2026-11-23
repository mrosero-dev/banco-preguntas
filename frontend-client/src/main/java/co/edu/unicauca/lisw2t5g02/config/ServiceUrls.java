package co.edu.unicauca.lisw2t5g02.config;

/** Base URLs of the four microservices. */
public record ServiceUrls(
        String identity,
        String question,
        String review,
        String notification) {

    public static ServiceUrls defaults() {
        return new ServiceUrls(
                "http://localhost:8081",
                "http://localhost:8080",
                "http://localhost:8082",
                "http://localhost:8083");
    }

    /**
     * Reads overrides from system properties (-Didentity.url=...) or
     * environment variables (IDENTITY_URL), falling back to defaults.
     */
    public static ServiceUrls fromEnvironment() {
        ServiceUrls d = defaults();
        return new ServiceUrls(
                read("identity", d.identity()),
                read("question", d.question()),
                read("review", d.review()),
                read("notification", d.notification()));
    }

    private static String read(String name, String fallback) {
        String property = System.getProperty(name + ".url");
        if (property != null && !property.isBlank()) {
            return property;
        }
        String env = System.getenv(name.toUpperCase() + "_URL");
        return env != null && !env.isBlank() ? env : fallback;
    }
}
