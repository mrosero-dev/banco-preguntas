package co.edu.unicauca.lisw2t5g02.presentation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import co.edu.unicauca.lisw2t5g02.client.QuestionClient;
import co.edu.unicauca.lisw2t5g02.model.QuestionStatus;
import co.edu.unicauca.lisw2t5g02.model.User;

/** HU-1.1: create a question; it is stored as a draft by Question Service. */
public final class CreateQuestionView extends JFrame {

    private final QuestionClient questions;
    private final User author;
    private final Runnable afterSave;

    private final QuestionForm form = new QuestionForm();
    private final JLabel message = new JLabel(" ");
    private final JButton save = new JButton("GUARDAR");

    public CreateQuestionView(
            QuestionClient questions, User author, Runnable afterSave) {

        super("HU-1.1 - Crear pregunta");
        this.questions = questions;
        this.author = author;
        this.afterSave = afterSave;

        save.addActionListener(e -> save());
        message.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        setLayout(new BorderLayout());
        add(new JScrollPane(form), BorderLayout.CENTER);

        javax.swing.JPanel bottom = new javax.swing.JPanel(new BorderLayout());
        bottom.add(message, BorderLayout.CENTER);
        bottom.add(save, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);

        setSize(900, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void save() {

        List<String> missing = form.highlightMissing();

        if (!missing.isEmpty()) {
            show(missing.contains("Contexto") && missing.size() == 1
                    ? "El contexto es obligatorio."
                    : "Campos obligatorios vacíos: "
                            + String.join(", ", missing), true);
            return;
        }

        save.setEnabled(false);
        show("Guardando...", false);

        Background.run(
                () -> questions.create(
                        form.read(null, author.id(), QuestionStatus.DRAFT)),
                created -> {
                    save.setEnabled(true);
                    form.clear();
                    show("Pregunta #" + created.id()
                            + " guardada en estado BORRADOR.", false);
                    if (afterSave != null) {
                        afterSave.run();
                    }
                },
                error -> {
                    save.setEnabled(true);
                    show(error.getMessage(), true);
                });
    }

    private void show(String text, boolean error) {
        message.setForeground(error ? new Color(180, 30, 30)
                : new Color(30, 110, 60));
        message.setText(text);
    }
}
