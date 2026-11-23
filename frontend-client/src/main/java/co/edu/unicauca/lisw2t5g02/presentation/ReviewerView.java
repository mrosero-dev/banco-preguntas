package co.edu.unicauca.lisw2t5g02.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
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
import co.edu.unicauca.lisw2t5g02.model.Review;
import co.edu.unicauca.lisw2t5g02.model.ReviewAssignment;
import co.edu.unicauca.lisw2t5g02.model.ReviewStatus;
import co.edu.unicauca.lisw2t5g02.model.User;

/** HU-2.2: the reviewer reads assigned questions and approves or rejects. */
public final class ReviewerView extends JFrame {

    /** An assignment together with the question it refers to. */
    private record Item(ReviewAssignment assignment, Question question) {
    }

    private final ApiClients api;
    private final User reviewer;

    private final DefaultListModel<Item> model = new DefaultListModel<>();
    private final JList<Item> list = new JList<>(model);
    private final JTextArea questionText = new JTextArea();
    private final JTextArea observations = new JTextArea(4, 40);
    private final JLabel message = new JLabel(" ");
    private final JButton approve = new JButton("APROBAR");
    private final JButton reject = new JButton("RECHAZAR");

    public ReviewerView(ApiClients api, User reviewer) {

        super("Revisor - Mis preguntas asignadas");
        this.api = api;
        this.reviewer = reviewer;

        setLayout(new BorderLayout(8, 8));

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> l, Object value, int i, boolean sel, boolean focus) {

                JLabel label = (JLabel) super.getListCellRendererComponent(
                        l, value, i, sel, focus);

                if (value instanceof Item item) {
                    ReviewStatus mine = item.assignment()
                            .reviewOf(reviewer.id())
                            .map(Review::status).orElse(ReviewStatus.PENDING);
                    label.setText("<html>" + item.question().title()
                            .replace("&", "&amp;").replace("<", "&lt;")
                            + "<br><b>Mi evaluación: " + mine.getLabel()
                            + "</b></html>");
                }
                return label;
            }
        });
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                select(list.getSelectedValue());
            }
        });

        JScrollPane left = new JScrollPane(list);
        left.setBorder(BorderFactory.createTitledBorder("Preguntas asignadas"));
        left.setPreferredSize(new Dimension(360, 500));
        add(left, BorderLayout.WEST);

        questionText.setEditable(false);
        questionText.setLineWrap(true);
        questionText.setWrapStyleWord(true);
        JScrollPane center = new JScrollPane(questionText);
        center.setBorder(BorderFactory.createTitledBorder("Pregunta"));
        add(center, BorderLayout.CENTER);

        observations.setLineWrap(true);
        observations.setWrapStyleWord(true);
        JPanel evaluation = new JPanel(new BorderLayout(4, 4));
        evaluation.setBorder(BorderFactory.createTitledBorder(
                "Observaciones (obligatorias al rechazar)"));
        evaluation.add(new JScrollPane(observations), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        approve.addActionListener(e -> decide(true));
        reject.addActionListener(e -> decide(false));
        buttons.add(approve);
        buttons.add(reject);
        evaluation.add(buttons, BorderLayout.SOUTH);

        JPanel south = new JPanel(new BorderLayout());
        south.add(evaluation, BorderLayout.CENTER);
        message.setBorder(BorderFactory.createEmptyBorder(2, 8, 4, 8));
        south.add(message, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);

        select(null);
        setSize(1100, 760);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        load(null);
    }

    private void load(Long reselectQuestionId) {

        show("Consultando...", false);

        Background.run(
                () -> {
                    List<Item> items = new ArrayList<>();
                    for (ReviewAssignment assignment
                            : api.review().findByReviewer(reviewer.id())) {
                        items.add(new Item(assignment,
                                api.question().find(assignment.questionId())));
                    }
                    return items;
                },
                items -> {
                    model.clear();
                    items.forEach(model::addElement);

                    if (items.isEmpty()) {
                        select(null);
                        show("No tiene preguntas asignadas.", false);
                        return;
                    }

                    show(" ", false);
                    for (int i = 0; reselectQuestionId != null
                            && i < model.size(); i++) {
                        if (model.get(i).assignment().questionId()
                                == reselectQuestionId) {
                            list.setSelectedIndex(i);
                        }
                    }
                },
                error -> show(error.getMessage(), true));
    }

    private void select(Item item) {

        observations.setText("");

        if (item == null) {
            questionText.setText("");
            approve.setEnabled(false);
            reject.setEnabled(false);
            return;
        }

        questionText.setText(QuestionText.format(item.question())
                + QuestionText.formatReviews(item.assignment()));
        questionText.setCaretPosition(0);

        boolean canEvaluate = item.question().status() == QuestionStatus.IN_REVIEW
                && item.assignment().reviewOf(reviewer.id())
                        .map(r -> r.status() == ReviewStatus.PENDING)
                        .orElse(false);

        approve.setEnabled(canEvaluate);
        reject.setEnabled(canEvaluate);

        if (!canEvaluate) {
            show("Esta pregunta ya fue evaluada por usted o no está en revisión.",
                    false);
        } else {
            show(" ", false);
        }
    }

    private void decide(boolean approved) {

        Item item = list.getSelectedValue();
        String text = observations.getText().trim();

        if (item == null) {
            return;
        }

        if (!approved && text.isEmpty()) {
            show("Debe ingresar al menos una observación para que el autor "
                    + "pueda corregir la pregunta.", true);
            return;
        }

        long questionId = item.assignment().questionId();
        approve.setEnabled(false);
        reject.setEnabled(false);

        Background.run(
                () -> api.review().decide(
                        questionId, reviewer.id(), approved, text),
                assignment -> {
                    JOptionPane.showMessageDialog(this,
                            "Evaluación registrada ("
                                    + (approved ? "aprobada" : "rechazada")
                                    + "). Estado de la revisión: "
                                    + assignment.consolidatedStatus().getLabel(),
                            "Evaluación registrada",
                            JOptionPane.INFORMATION_MESSAGE);
                    load(questionId);
                },
                error -> {
                    JOptionPane.showMessageDialog(this, error.getMessage(),
                            "No se pudo registrar", JOptionPane.WARNING_MESSAGE);
                    load(questionId);
                });
    }

    private void show(String text, boolean error) {
        message.setForeground(error ? new Color(180, 30, 30) : Color.DARK_GRAY);
        message.setText(text);
    }
}
