package co.edu.unicauca.lisw2t5g02.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import co.edu.unicauca.lisw2t5g02.client.ApiClients;
import co.edu.unicauca.lisw2t5g02.model.Question;
import co.edu.unicauca.lisw2t5g02.model.QuestionStatus;
import co.edu.unicauca.lisw2t5g02.model.Role;
import co.edu.unicauca.lisw2t5g02.model.User;

/** HU-2.1: the administrator assigns between one and three reviewers. */
public final class AdminReviewView extends JFrame {

    private static final int MAX_REVIEWERS = 3;

    private record Data(List<Question> questions, List<User> reviewers) {
    }

    private final ApiClients api;
    private final JComboBox<Question> questions = new JComboBox<>();
    private final JList<User> reviewers = new JList<>();
    private final JTextArea detail = new JTextArea();
    private final JLabel message = new JLabel(" ");
    private final JButton save = new JButton("GUARDAR ASIGNACIÓN");

    public AdminReviewView(ApiClients api) {

        super("Administrador - Asignar revisores");
        this.api = api;

        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Pregunta pendiente de revisión:"));
        questions.addActionListener(e -> showDetail());
        top.add(questions);
        add(top, BorderLayout.NORTH);

        detail.setEditable(false);
        detail.setLineWrap(true);
        detail.setWrapStyleWord(true);

        reviewers.setSelectionMode(
                javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JPanel center = new JPanel(new GridLayout(1, 2, 8, 8));
        JScrollPane detailPane = new JScrollPane(detail);
        detailPane.setBorder(BorderFactory.createTitledBorder("Pregunta"));
        JScrollPane reviewersPane = new JScrollPane(reviewers);
        reviewersPane.setBorder(BorderFactory.createTitledBorder(
                "Docentes disponibles (seleccione de 1 a 3)"));
        center.add(detailPane);
        center.add(reviewersPane);
        add(center, BorderLayout.CENTER);

        save.addActionListener(e -> assign());
        JPanel bottom = new JPanel(new BorderLayout());
        message.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        bottom.add(message, BorderLayout.CENTER);
        bottom.add(save, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        setSize(980, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        load();
    }

    private void load() {

        show("Consultando...", false);

        Background.run(
                () -> {
                    List<Question> pending = new ArrayList<>();
                    for (var assignment : api.review().findPending()) {
                        Question question =
                                api.question().find(assignment.questionId());
                        if (question.status() == QuestionStatus.PENDING_REVIEW) {
                            pending.add(question);
                        }
                    }
                    return new Data(pending,
                            api.identity().findUsers(Role.REVIEWER));
                },
                data -> {
                    questions.setModel(new DefaultComboBoxModel<>(
                            data.questions().toArray(new Question[0])));

                    DefaultListModel<User> users = new DefaultListModel<>();
                    data.reviewers().forEach(users::addElement);
                    reviewers.setModel(users);

                    showDetail();
                    show(data.questions().isEmpty()
                            ? "No hay preguntas pendientes de revisión."
                            : " ", false);
                    save.setEnabled(!data.questions().isEmpty());
                },
                error -> {
                    save.setEnabled(false);
                    show(error.getMessage(), true);
                });
    }

    private void showDetail() {

        Question question = (Question) questions.getSelectedItem();

        detail.setText(question == null ? "" : QuestionText.format(question));
        detail.setCaretPosition(0);
        reviewers.clearSelection();
    }

    private void assign() {

        Question question = (Question) questions.getSelectedItem();
        List<User> selected = reviewers.getSelectedValuesList();

        if (question == null) {
            show("Seleccione una pregunta pendiente de revisión.", true);
            return;
        }

        if (selected.isEmpty()) {
            show("Debe asignarse al menos un revisor.", true);
            return;
        }

        if (selected.size() > MAX_REVIEWERS) {
            show("Solo pueden asignarse hasta tres revisores por pregunta.", true);
            return;
        }

        List<Long> ids = selected.stream().map(User::id).toList();

        save.setEnabled(false);

        Background.run(
                () -> api.review().assignReviewers(
                        question.id(), question.authorId(), ids),
                assignment -> {
                    JOptionPane.showMessageDialog(this,
                            "Asignación registrada. Se notificará por correo a "
                                    + selected.size() + " revisor(es).",
                            "Asignación exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                    load();
                },
                error -> {
                    save.setEnabled(true);
                    JOptionPane.showMessageDialog(this, error.getMessage(),
                            "No se pudo asignar", JOptionPane.WARNING_MESSAGE);
                });
    }

    private void show(String text, boolean error) {
        message.setForeground(error ? new Color(180, 30, 30) : Color.DARK_GRAY);
        message.setText(text);
    }
}
