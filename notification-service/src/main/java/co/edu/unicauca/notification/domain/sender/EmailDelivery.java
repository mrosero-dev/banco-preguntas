package co.edu.unicauca.notification.domain.sender;

/**
 * Defines the contract for delivering emails.
 */
public interface EmailDelivery {

    void deliver(String recipient, String message);
}