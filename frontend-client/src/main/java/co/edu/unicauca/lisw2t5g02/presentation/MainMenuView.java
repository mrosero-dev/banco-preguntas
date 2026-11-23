package co.edu.unicauca.lisw2t5g02.presentation;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import co.edu.unicauca.lisw2t5g02.client.ApiClients;
import co.edu.unicauca.lisw2t5g02.model.Role;
import co.edu.unicauca.lisw2t5g02.model.User;

/** Main menu; the options depend on the role of the logged user. */
public class MainMenuView extends JFrame {

    private final User user;
    private final ApiClients api;
    private final Runnable onLogout;

    public MainMenuView(User user, ApiClients api, Runnable onLogout) {
        super("Banco de Preguntas Saber PRO");
        this.user = user;
        this.api = api;
        this.onLogout = onLogout;
        build();
        setSize(620, 430);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void build() {

        setLayout(new BorderLayout(10, 10));

        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setBorder(BorderFactory.createEmptyBorder(15, 15, 5, 15));
        JLabel welcome = new JLabel("Bienvenido, " + user.fullName());
        welcome.setFont(welcome.getFont().deriveFont(Font.BOLD, 18f));
        header.add(welcome);
        header.add(new JLabel("Rol: " + user.role().getLabel()));
        add(header, BorderLayout.NORTH);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 8, 8));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        if (user.role() == Role.AUTHOR) {
            addButton(buttons, "Crear pregunta (HU-1.1)",
                    () -> new CreateQuestionView(api.question(), user, null));
            addButton(buttons, "Mis preguntas (HU-1.2 / HU-1.3)",
                    () -> new GUIQuestions(api, user));
        }

        if (user.role() == Role.ADMIN) {
            addButton(buttons, "Asignar revisores (HU-2.1)",
                    () -> new AdminReviewView(api));
        }

        if (user.role() == Role.REVIEWER) {
            addButton(buttons, "Mis preguntas asignadas (HU-2.2)",
                    () -> new ReviewerView(api, user));
        }

        addButton(buttons, "Mis notificaciones",
                () -> new NotificationsView(api.notification(), user));

        JButton logout = new JButton("Cerrar sesión");
        logout.addActionListener(e -> {
            api.logout();
            for (java.awt.Window window : java.awt.Window.getWindows()) {
                window.dispose();
            }
            onLogout.run();
        });
        buttons.add(logout);

        add(buttons, BorderLayout.CENTER);
    }

    private void addButton(
            JPanel panel, String text, java.util.function.Supplier<JFrame> view) {

        JButton button = new JButton(text);
        button.addActionListener(e -> view.get().setVisible(true));
        panel.add(button);
    }
}
