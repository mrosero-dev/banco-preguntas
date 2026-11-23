package co.edu.unicauca.notification.infrastructure.adapter;

import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class EmailProviderAdapterTest {

    @Test
    void shouldDelegateEmailSendingToExternalProvider() {
        ExternalEmailProvider provider =
                mock(ExternalEmailProvider.class);

        EmailProviderAdapter adapter =
                new EmailProviderAdapter(provider);

        adapter.deliver("author@example.com", "Question approved");

        verify(provider).deliverEmail(
                "author@example.com",
                "Question approved"
        );
    }
}