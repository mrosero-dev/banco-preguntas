package co.edu.unicauca.lisw2t5g02.presentation;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import co.edu.unicauca.lisw2t5g02.client.ApiException;
import co.edu.unicauca.lisw2t5g02.client.IdentityClient;
import co.edu.unicauca.lisw2t5g02.model.User;

/** Login screen; authenticates against the Identity Service. */
public class LoginView extends JFrame {

    private final IdentityClient identity;
    private final Consumer<User> onLogin;

    private final JTextField email = new JTextField(18);
    private final JPasswordField password = new JPasswordField(18);
    private final JLabel message = new JLabel(" ");
    private final JButton enter = new JButton("Iniciar sesión");

    public LoginView(IdentityClient identity, Consumer<User> onLogin) {
        super("Banco de Preguntas Saber PRO - Iniciar sesión");
        this.identity = identity;
        this.onLogin = onLogin;
        build();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(460, 280);
        setLocationRelativeTo(null);
    }

    /** Shows a message under the form, for example when the session expired. */
    public void showNotice(String text) {
        message.setText(text);
    }

    private void build() {

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Banco de Preguntas Saber PRO");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        g.gridx = 0;
        g.gridy = 0;
        g.gridwidth = 2;
        panel.add(title, g);

        g.gridwidth = 1;
        g.gridy = 1;
        panel.add(new JLabel("Correo:"), g);
        g.gridx = 1;
        g.weightx = 1;
        panel.add(email, g);

        g.gridx = 0;
        g.gridy = 2;
        g.weightx = 0;
        panel.add(new JLabel("Contraseña:"), g);
        g.gridx = 1;
        g.weightx = 1;
        panel.add(password, g);

        enter.addActionListener(e -> login());
        getRootPane().setDefaultButton(enter);
        g.gridx = 0;
        g.gridy = 3;
        g.gridwidth = 2;
        panel.add(enter, g);

        message.setForeground(new Color(180, 30, 30));
        g.gridy = 4;
        panel.add(message, g);

        add(panel);
    }

    private void login() {

        String typedEmail = email.getText().trim();
        String typedPassword = new String(password.getPassword());

        if (typedEmail.isEmpty() || typedPassword.isEmpty()) {
            message.setText("Correo y contraseña son obligatorios.");
            return;
        }

        enter.setEnabled(false);
        message.setText("Conectando...");

        Background.run(
                () -> identity.login(typedEmail, typedPassword),
                user -> {
                    dispose();
                    onLogin.accept(user);
                },
                error -> {
                    enter.setEnabled(true);
                    password.setText("");
                    message.setText(error instanceof ApiException api
                            && api.getStatus() == 401
                            ? "Credenciales inválidas."
                            : error.getMessage());
                });
    }
}
