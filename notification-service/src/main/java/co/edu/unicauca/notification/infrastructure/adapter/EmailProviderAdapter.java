package co.edu.unicauca.notification.infrastructure.adapter;

import co.edu.unicauca.notification.domain.sender.EmailDelivery;

/**
 * Adapts the external email provider to the email delivery contract.
 */
public class EmailProviderAdapter implements EmailDelivery {

    private final ExternalEmailProvider emailProvider;

    public EmailProviderAdapter(ExternalEmailProvider emailProvider) {
        this.emailProvider = emailProvider;
    }

    @Override
    public void deliver(String recipient, String message) {
        emailProvider.deliverEmail(recipient, message);
    }
}