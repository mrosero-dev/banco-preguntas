package co.edu.unicauca.lisw2t5g02.presentation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import co.edu.unicauca.lisw2t5g02.model.Question;
import co.edu.unicauca.lisw2t5g02.model.Review;
import co.edu.unicauca.lisw2t5g02.model.ReviewAssignment;
import co.edu.unicauca.lisw2t5g02.model.ReviewStatus;
import co.edu.unicauca.lisw2t5g02.model.VersionedQuestion;

/**
 * HU-2.3: review history of a question, one entry per version.
 *
 * Each entry shows the version number, the review round that evaluated it,
 * every reviewer decision with date and observations, and the exact content
 * of the question that was evaluated. Everything is read-only: previous
 * versions cannot be edited.
 */
public class ReviewHistoryDialog extends JDialog {

    /** One row of the list: a version and the reviews of its round. */
    private record Entry(int version, boolean current, Question content,
            List<Review> reviews) {

        String label() {
            ReviewStatus status = reviews.isEmpty()
                    ? null
                    : consolidated(reviews);
            return "Versión " + version + " · Ronda " + version
                    + (current ? " (vigente)" : "")
                    + " · " + (status == null ? "Sin evaluaciones"
                            : status.getLabel());
        }
    }

    private final Map<Long, String> reviewerNames;
    private final JTextArea detail = new JTextArea();

    public ReviewHistoryDialog(
            JFrame owner,
            Question currentQuestion,
            List<VersionedQuestion> previousVersions,
            ReviewAssignment assignment,
            Map<Long, String> reviewerNames) {

        super(owner, "Historial de revisiones · pregunta #"
                + currentQuestion.id(), true);
        this.reviewerNames = reviewerNames;

        int currentVersion = previousVersions.size() + 1;

        List<Review> allReviews = new ArrayList<>(assignment.history());
        allReviews.addAll(assignment.reviews());

        DefaultListModel<Entry> model = new DefaultListModel<>();

        for (int version = currentVersion; version >= 1; version--) {
            final int number = version;
            Question content = version == currentVersion
                    ? currentQuestion
                    : previousVersions.stream()
                            .filter(v -> v.version() == number)
                            .map(VersionedQuestion::content)
                            .findFirst()
                            .orElse(null);

            List<Review> reviews = allReviews.stream()
                    .filter(review -> review.roundNumber() == number)
                    .toList();

            model.addElement(new Entry(
                    version, version == currentVersion, content, reviews));
        }

        JList<Entry> list = new JList<>(model);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(
                    JList<?> source, Object value, int index,
                    boolean selected, boolean focus) {
                return super.getListCellRendererComponent(source,
                        ((Entry) value).label(), index, selected, focus);
            }
        });
        list.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && list.getSelectedValue() != null) {
                detail.setText(describe(list.getSelectedValue()));
                detail.setCaretPosition(0);
            }
        });

        detail.setEditable(false);
        detail.setLineWrap(true);
        detail.setWrapStyleWord(true);

        JScrollPane left = new JScrollPane(list);
        left.setPreferredSize(new Dimension(280, 400));

        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, left, new JScrollPane(detail));
        split.setDividerLocation(290);

        add(split, BorderLayout.CENTER);
        setSize(980, 620);
        setLocationRelativeTo(owner);

        list.setSelectedIndex(0);
    }

    private String describe(Entry entry) {

        StringBuilder text = new StringBuilder();

        text.append("VERSIÓN ").append(entry.version())
                .append("  (evaluada en la ronda de revisión ")
                .append(entry.version()).append(")\n");

        if (!entry.current()) {
            text.append("Versión anterior: solo lectura. Sus datos no se "
                    + "modifican al guardar una versión nueva.\n");
        }

        text.append("\n--- Evaluaciones ---\n");

        if (entry.reviews().isEmpty()) {
            text.append("Esta versión todavía no tiene evaluaciones.\n");
        }

        for (Review review : entry.reviews()) {
            text.append("\nRevisor: ")
                    .append(reviewerNames.getOrDefault(review.reviewerId(),
                            "Revisor #" + review.reviewerId()))
                    .append("\nDecisión: ").append(review.status().getLabel())
                    .append("\nFecha: ").append(review.reviewedAt() == null
                            || review.reviewedAt().isBlank()
                            ? "Sin fecha" : review.reviewedAt())
                    .append("\nObservaciones: ").append(review.observations() == null
                            || review.observations().isBlank()
                            ? "Sin observaciones" : review.observations())
                    .append('\n');
        }

        text.append("\n--- Contenido de la pregunta evaluada ---\n");

        if (entry.content() == null) {
            text.append("No hay copia del contenido de esta versión.");
        } else {
            text.append(QuestionText.format(entry.content()));
        }

        return text.toString();
    }

    private static ReviewStatus consolidated(List<Review> reviews) {
        if (reviews.stream().anyMatch(r -> r.status() == ReviewStatus.PENDING)) {
            return ReviewStatus.PENDING;
        }
        return reviews.stream().anyMatch(r -> r.status() == ReviewStatus.REJECTED)
                ? ReviewStatus.REJECTED
                : ReviewStatus.APPROVED;
    }
}
