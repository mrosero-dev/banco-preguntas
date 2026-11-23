package co.edu.unicauca.notification.infrastructure.adapter;

/**
 * Represents an external email provider.
 */
public class ExternalEmailProvider {

    public void deliverEmail(String emailAddress, String content) {
        System.out.println(
                "External provider delivering email to "
                + emailAddress + ": " + content
        );
    }
}