package co.edu.unicauca.lisw2t5g02.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
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
import javax.swing.JTextField;

import co.edu.unicauca.lisw2t5g02.client.ApiClients;
import co.edu.unicauca.lisw2t5g02.model.Question;
import co.edu.unicauca.lisw2t5g02.model.QuestionPage;
import co.edu.unicauca.lisw2t5g02.model.QuestionStatus;
import co.edu.unicauca.lisw2t5g02.model.Review;
import co.edu.unicauca.lisw2t5g02.model.ReviewAssignment;
import co.edu.unicauca.lisw2t5g02.model.VersionedQuestion;
import co.edu.unicauca.lisw2t5g02.model.User;

/**
 * HU-1.2 / HU-1.3 / HU-2.2 (scenario 11): the author lists, filters, reads,
 * edits and sends to review his own questions.
 */
public class GUIQuestions extends JFrame {

    private static final int PAGE_SIZE = 5;

    private final ApiClients api;
    private final User user;

    private final JComboBox<QuestionStatus> statusFilter =
            new JComboBox<>(new QuestionStatus[] {
                null, QuestionStatus.DRAFT, QuestionStatus.PENDING_REVIEW,
                QuestionStatus.IN_REVIEW, QuestionStatus.APPROVED,
                QuestionStatus.REJECTED});
    private final JTextField search = new JTextField(14);
    private final DefaultListModel<Question> model = new DefaultListModel<>();
    private final JList<Question> list = new JList<>(model);
    private final JLabel pageLabel = new JLabel(" ");
    private final JLabel message = new JLabel(" ");
    private final QuestionForm form = new QuestionForm();

    private final JButton view = new JButton("VER COMPLETA");
    private final JButton modify = new JButton("MODIFICAR");
    private final JButton saveChanges = new JButton("GUARDAR MODIFICACIÓN");
    private final JButton send = new JButton("ENVIAR A REVISIÓN");
    private final JButton backToDraft = new JButton("CREAR NUEVA VERSIÓN");
    private final JButton historyButton = new JButton("HISTORIAL DE REVISIONES");

    private int page = 0;
    private int totalPages = 1;
    private Question selected;

    public GUIQuestions(ApiClients api, User user) {
        super("Banco Saber Pro - Mis preguntas");
        this.api = api;
        this.user = user;
        build();
        refresh(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 760);
        setLocationRelativeTo(null);
    }

    private void build() {

        setLayout(new BorderLayout(8, 8));

        statusFilter.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> l, Object value, int i, boolean s, boolean f) {
                return super.getListCellRendererComponent(l,
                        value == null ? "Todos los estados" : value, i, s, f);
            }
        });

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Estado:"));
        top.add(statusFilter);
        top.add(new JLabel("Buscar:"));
        top.add(search);
        JButton filter = new JButton("FILTRAR");
        filter.addActionListener(e -> {
            page = 0;
            refresh(null);
        });
        top.add(filter);
        add(top, BorderLayout.NORTH);

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> l, Object value, int i, boolean sel, boolean focus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        l, value, i, sel, focus);

                if (value instanceof Question q) {
                    label.setText("<html>" + escape(q.title()) + "<br><b>"
                            + q.status().getLabel() + "</b></html>");
                    if (!sel) {
                        label.setForeground(StatusColors.of(q.status()));
                    }
                }
                return label;
            }
        });
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                select(list.getSelectedValue());
            }
        });

        JPanel left = new JPanel(new BorderLayout(5, 5));
        left.setBorder(BorderFactory.createTitledBorder(
                "Preguntas creadas por el autor"));
        left.add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel nav = new JPanel();
        JButton previous = new JButton("< Anterior");
        JButton next = new JButton("Siguiente >");
        previous.addActionListener(e -> {
            if (page > 0) {
                page--;
                refresh(null);
            }
        });
        next.addActionListener(e -> {
            if (page + 1 < totalPages) {
                page++;
                refresh(null);
            }
        });
        nav.add(previous);
        nav.add(pageLabel);
        nav.add(next);
        left.add(nav, BorderLayout.SOUTH);
        left.setPreferredSize(new Dimension(380, 600));
        add(left, BorderLayout.WEST);

        JPanel right = new JPanel(new BorderLayout());
        right.setBorder(BorderFactory.createTitledBorder("Pregunta"));
        right.add(new JScrollPane(form), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        view.addActionListener(e -> showFull());
        modify.addActionListener(e -> startEditing());
        saveChanges.addActionListener(e -> saveChanges());
        send.addActionListener(e -> sendForReview());
        backToDraft.addActionListener(e -> returnToDraft());
        historyButton.addActionListener(e -> showHistory());
        buttons.add(view);
        buttons.add(modify);
        buttons.add(send);
        buttons.add(backToDraft);
        buttons.add(historyButton);
        buttons.add(saveChanges);
        right.add(buttons, BorderLayout.SOUTH);
        add(right, BorderLayout.CENTER);

        message.setBorder(BorderFactory.createEmptyBorder(2, 8, 4, 8));
        add(message, BorderLayout.SOUTH);

        select(null);
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;");
    }

    /** Loads the current page; reselects the given question if present. */
    private void refresh(Long reselectId) {

        QuestionStatus status = (QuestionStatus) statusFilter.getSelectedItem();
        String text = search.getText();
        int requestedPage = page;

        show("Consultando...", false);

        Background.run(
                () -> api.question().listByAuthor(
                        user.id(), status, text, requestedPage, PAGE_SIZE),
                result -> showPage(result, status, reselectId),
                error -> show(error.getMessage(), true));
    }

    private void showPage(QuestionPage result, QuestionStatus status, Long reselectId) {

        model.clear();
        result.content().forEach(model::addElement);
        totalPages = Math.max(1, result.totalPages());
        pageLabel.setText("Página " + (result.page() + 1) + " de "
                + totalPages + " (" + result.totalElements() + ")");

        if (result.content().isEmpty()) {
            select(null);
            show(status == null
                    ? "No se encontraron preguntas."
                    : "No se encontraron preguntas con el estado "
                            + status.getLabel() + ".", false);
            return;
        }

        show(" ", false);

        for (int i = 0; reselectId != null && i < model.size(); i++) {
            if (reselectId.equals(model.get(i).id())) {
                list.setSelectedIndex(i);
                return;
            }
        }
    }

    private void select(Question question) {

        selected = question;

        if (question == null) {
            form.clear();
        } else {
            form.load(question);
        }

        form.setEditable(false);
        saveChanges.setEnabled(false);
        view.setEnabled(question != null);
        modify.setEnabled(question != null);
        send.setEnabled(question != null
                && question.status() == QuestionStatus.DRAFT);
        backToDraft.setEnabled(question != null
                && question.status() == QuestionStatus.REJECTED);
        historyButton.setEnabled(question != null);
    }

    private void startEditing() {

        if (selected.status() != QuestionStatus.DRAFT) {
            JOptionPane.showMessageDialog(this,
                    "Solo las preguntas en estado BORRADOR pueden ser modificadas.",
                    "Acción no permitida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        form.setEditable(true);
        saveChanges.setEnabled(true);
    }

    private void saveChanges() {

        var missing = form.highlightMissing();
        if (!missing.isEmpty()) {
            show("Campos obligatorios vacíos: " + String.join(", ", missing), true);
            return;
        }

        Question edited = form.read(
                selected.id(), user.id(), selected.status());

        Background.run(
                () -> api.question().update(edited),
                saved -> {
                    JOptionPane.showMessageDialog(this,
                            "Modificación guardada y validada correctamente.");
                    refresh(saved.id());
                },
                error -> JOptionPane.showMessageDialog(this,
                        error.getMessage(), "Validación",
                        JOptionPane.WARNING_MESSAGE));
    }

    private void sendForReview() {

        long id = selected.id();

        Background.run(
                () -> api.question().submitForReview(id),
                sent -> {
                    JOptionPane.showMessageDialog(this,
                            "La pregunta pasó a PENDIENTE DE REVISIÓN.");
                    refresh(id);
                },
                error -> JOptionPane.showMessageDialog(this,
                        error.getMessage(), "No se puede enviar",
                        JOptionPane.WARNING_MESSAGE));
    }

    private void returnToDraft() {

        long id = selected.id();

        Background.run(
                () -> api.question().returnToDraft(id, user.id()),
                draft -> {
                    JOptionPane.showMessageDialog(this,
                            "Se creó una nueva versión en estado BORRADOR. "
                            + "La versión rechazada queda en el historial "
                            + "como copia de solo lectura.");
                    refresh(id);
                },
                error -> JOptionPane.showMessageDialog(this,
                        error.getMessage(), "No se puede crear la nueva versión",
                        JOptionPane.WARNING_MESSAGE));
    }

    /** Shows the whole question plus the observations left by reviewers. */
    private void showFull() {

        Question question = selected;

        Background.run(
                () -> {
                    String text = QuestionText.format(question);
                    if (question.status() != QuestionStatus.DRAFT
                            && question.status() != QuestionStatus.PENDING_REVIEW) {
                        text += api.review().find(question.id())
                                .map(QuestionText::formatReviews).orElse("");
                    }
                    return text;
                },
                text -> {
                    JTextArea area = new JTextArea(text, 24, 70);
                    area.setEditable(false);
                    area.setLineWrap(true);
                    area.setWrapStyleWord(true);
                    area.setCaretPosition(0);
                    JOptionPane.showMessageDialog(this, new JScrollPane(area),
                            question.title(), JOptionPane.INFORMATION_MESSAGE);
                },
                error -> show(error.getMessage(), true));
    }

    /** HU-2.3: history by version, with decisions, observations and the evaluated content. */
    private void showHistory() {
        if (selected == null) return;
        long questionId = selected.id();
        long authorId = selected.authorId();

        record HistoryData(
                java.util.Optional<ReviewAssignment> assignment,
                Question current,
                java.util.List<VersionedQuestion> versions,
                java.util.Map<Long, String> names) { }

        Background.run(() -> {
            var assignment = api.review().history(questionId);
            Question current = api.question().find(questionId);
            var versions = api.question().versions(questionId, authorId);
            java.util.Map<Long, String> names = new java.util.HashMap<>();
            assignment.ifPresent(a -> {
                java.util.stream.Stream.concat(
                        a.history().stream(), a.reviews().stream())
                        .map(Review::reviewerId).distinct()
                        .forEach(id -> {
                            try {
                                names.put(id, api.identity().findById(id).fullName());
                            } catch (RuntimeException ignored) {
                                names.put(id, "Revisor #" + id);
                            }
                        });
            });
            return new HistoryData(assignment, current, versions, names);
        }, data -> {
            if (data.assignment().isEmpty()
                    || (data.assignment().get().history().isEmpty()
                            && data.assignment().get().reviews().isEmpty())) {
                JOptionPane.showMessageDialog(this,
                        "Esta pregunta todavía no tiene revisiones registradas.",
                        "Historial de revisiones", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            new ReviewHistoryDialog(this, data.current(), data.versions(),
                    data.assignment().get(), data.names()).setVisible(true);
        }, error -> show(error.getMessage(), true));
    }

    private void show(String text, boolean error) {
        message.setForeground(error ? new Color(180, 30, 30) : Color.DARK_GRAY);
        message.setText(text);
    }
}
