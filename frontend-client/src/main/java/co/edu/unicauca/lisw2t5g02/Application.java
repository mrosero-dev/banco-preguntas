package co.edu.unicauca.lisw2t5g02;

import java.awt.EventQueue;
import java.awt.Window;

import javax.swing.UIManager;

import co.edu.unicauca.lisw2t5g02.client.ApiClients;
import co.edu.unicauca.lisw2t5g02.config.ServiceUrls;
import co.edu.unicauca.lisw2t5g02.presentation.LoginView;
import co.edu.unicauca.lisw2t5g02.presentation.MainMenuView;

/**
 * Entry point of the Swing client.
 *
 * The client has no database and no business rules: every screen talks to
 * the four microservices (Identity, Question, Review, Notification) through
 * their REST API. Base URLs can be overridden with system properties such as
 * -Dquestion.url=http://host:8080 or environment variables QUESTION_URL.
 */
public final class Application {

    private Application() {
    }

    public static void main(String[] args) {

        applyNimbus();

        ApiClients api = ApiClients.create(ServiceUrls.fromEnvironment());

        EventQueue.invokeLater(() -> showLogin(api));
    }

    private static void showLogin(ApiClients api) {
        showLogin(api, null);
    }

    private static void showLogin(ApiClients api, String notice) {
        // Cuando el servidor responde 401 por token vencido se cierran todas
        // las ventanas y se pide autenticarse de nuevo.
        api.onSessionExpired(() -> EventQueue.invokeLater(() -> {
            for (Window window : Window.getWindows()) {
                window.dispose();
            }
            showLogin(api, "La sesión expiró. Inicia sesión de nuevo.");
        }));

        LoginView login = new LoginView(api.identity(), user ->
                new MainMenuView(user, api, () -> showLogin(api))
                        .setVisible(true)
        );
        if (notice != null) {
            login.showNotice(notice);
        }
        login.setVisible(true);
    }

    private static void applyNimbus() {
        try {
            for (UIManager.LookAndFeelInfo info
                    : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {
            // The default look and feel is used.
        }
    }
}
