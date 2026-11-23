package co.edu.unicauca.lisw2t5g02.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import co.edu.unicauca.lisw2t5g02.client.NotificationClient;
import co.edu.unicauca.lisw2t5g02.model.Notification;
import co.edu.unicauca.lisw2t5g02.model.User;

/** Lists the notifications stored for the logged user. */
public final class NotificationsView extends JFrame {

    private final NotificationClient notifications;
    private final User user;

    private final DefaultTableModel model = new DefaultTableModel(
            new String[] {"Fecha", "Tipo", "Mensaje", "Leída"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(model);
    private final JLabel message = new JLabel(" ");
    private List<Notification> current = new ArrayList<>();

    public NotificationsView(NotificationClient notifications, User user) {

        super("Mis notificaciones");
        this.notifications = notifications;
        this.user = user;

        setLayout(new BorderLayout(8, 8));

        table.getColumnModel().getColumn(0).setPreferredWidth(130);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(520);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refresh = new JButton("ACTUALIZAR");
        refresh.addActionListener(e -> load());
        JButton markRead = new JButton("MARCAR COMO LEÍDA");
        markRead.addActionListener(e -> markRead());
        buttons.add(refresh);
        buttons.add(markRead);

        JPanel south = new JPanel(new BorderLayout());
        message.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        south.add(message, BorderLayout.CENTER);
        south.add(buttons, BorderLayout.EAST);
        add(south, BorderLayout.SOUTH);

        setSize(900, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        load();
    }

    private void load() {

        show("Consultando...", false);

        Background.run(
                () -> notifications.findByRecipient(user.id()),
                result -> {
                    current = result;
                    model.setRowCount(0);
                    for (Notification n : result) {
                        model.addRow(new Object[] {
                            date(n.createdAt()), typeLabel(n.type()),
                            n.message(), n.read() ? "Sí" : "No"});
                    }
                    show(result.isEmpty() ? "No tiene notificaciones." : " ", false);
                },
                error -> show(error.getMessage(), true));
    }

    private void markRead() {

        int row = table.getSelectedRow();
        if (row < 0) {
            show("Seleccione una notificación.", true);
            return;
        }

        long id = current.get(row).id();

        Background.run(
                () -> notifications.markAsRead(id),
                updated -> load(),
                error -> show(error.getMessage(), true));
    }

    static String date(String iso) {
        String text = iso.replace('T', ' ');
        return text.length() > 16 ? text.substring(0, 16) : text;
    }

    static String typeLabel(String type) {
        return switch (type) {
            case "REVIEW_ASSIGNED" -> "Revisión asignada";
            case "REVIEW_APPROVED" -> "Pregunta aprobada";
            case "REVIEW_REJECTED" -> "Pregunta rechazada";
            case "QUESTION_RESUBMITTED" -> "Nueva versión pendiente";
            default -> type;
        };
    }

    private void show(String text, boolean error) {
        message.setForeground(error ? new Color(180, 30, 30) : Color.DARK_GRAY);
        message.setText(text);
    }
}
