package co.edu.unicauca.lisw2t5g02.client;

/** Error returned by a microservice, or failure to reach it. */
public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    /** HTTP status, or 0 when the service could not be reached. */
    public int getStatus() {
        return status;
    }

    public boolean isNotFound() {
        return status == 404;
    }
}
