package co.edu.unicauca.lisw2t5g02.presentation;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.text.JTextComponent;

import co.edu.unicauca.lisw2t5g02.model.Question;
import co.edu.unicauca.lisw2t5g02.model.QuestionOption;
import co.edu.unicauca.lisw2t5g02.model.QuestionStatus;

/** Form with the ten fields of a question, shared by create and edit views. */
public final class QuestionForm extends JPanel {

    private static final Border ERROR_BORDER =
            BorderFactory.createLineBorder(new Color(200, 30, 30), 2);

    private final JTextField category = new JTextField();
    private final JTextArea context = area();
    private final JTextArea statement = area();
    private final JTextField[] options = {
        new JTextField(), new JTextField(), new JTextField(),
        new JTextField(), new JTextField()
    };
    private final JComboBox<String> correct =
            new JComboBox<>(new String[] {"A", "B", "C", "D", "E"});
    private final JTextArea justification = area();
    private final JTextArea source = area();
    private final JTextField competence = new JTextField();
    private final JTextField topic = new JTextField();
    private final JTextField subtopic = new JTextField();
    private final JComboBox<String> difficulty =
            new JComboBox<>(new String[] {"Fácil", "Media", "Difícil"});

    private final List<JTextComponent> required = new ArrayList<>();
    private final List<String> requiredNames = new ArrayList<>();
    private final List<Component> outlined = new ArrayList<>();
    private final Border defaultBorder = new JTextField().getBorder();

    public QuestionForm() {

        setLayout(new GridBagLayout());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(3, 4, 3, 4);
        g.fill = GridBagConstraints.HORIZONTAL;
        g.anchor = GridBagConstraints.NORTHWEST;

        int row = 0;
        row = addRow(g, row, "Categoría", category, category, "Categoría");
        row = addRow(g, row, "Contexto", new JScrollPane(context),
                context, "Contexto");
        row = addRow(g, row, "Pregunta directa", new JScrollPane(statement),
                statement, "Pregunta directa");

        for (int i = 0; i < options.length; i++) {
            String name = "Opción " + (char) ('A' + i);
            row = addRow(g, row, name, options[i], options[i], name);
        }

        row = addRow(g, row, "Respuesta correcta", correct, null, null);
        row = addRow(g, row, "Justificación", new JScrollPane(justification),
                justification, "Justificación");
        row = addRow(g, row, "Bibliografía", new JScrollPane(source),
                source, "Bibliografía");
        row = addRow(g, row, "Competencia", competence, competence,
                "Competencia");
        row = addRow(g, row, "Tema", topic, topic, "Tema");
        row = addRow(g, row, "Subtema", subtopic, subtopic, "Subtema");
        addRow(g, row, "Nivel de dificultad", difficulty, null, null);
    }

    private static JTextArea area() {
        JTextArea area = new JTextArea(3, 30);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private int addRow(
            GridBagConstraints g,
            int row,
            String label,
            Component input,
            JTextComponent field,
            String fieldName) {

        g.gridy = row;
        g.gridx = 0;
        g.weightx = 0;
        add(new JLabel(label + ":"), g);

        g.gridx = 1;
        g.weightx = 1;
        add(input, g);

        if (field != null) {
            required.add(field);
            requiredNames.add(fieldName);
            outlined.add(input);
        }

        return row + 1;
    }

    /** Names of the required fields left empty; they are outlined in red. */
    public List<String> highlightMissing() {

        List<String> missing = new ArrayList<>();

        for (int i = 0; i < required.size(); i++) {

            boolean empty = required.get(i).getText().isBlank();
            Component component = outlined.get(i);

            if (component instanceof javax.swing.JComponent swingComponent) {
                swingComponent.setBorder(empty ? ERROR_BORDER
                        : component instanceof JScrollPane
                                ? new JScrollPane().getBorder()
                                : defaultBorder);
            }

            if (empty) {
                missing.add(requiredNames.get(i));
            }
        }

        return missing;
    }

    /** Builds the question typed in the form. */
    public Question read(Long id, long authorId, QuestionStatus status) {

        List<QuestionOption> list = new ArrayList<>();
        for (int i = 0; i < options.length; i++) {
            list.add(new QuestionOption(
                    options[i].getText().trim(),
                    i == correct.getSelectedIndex()));
        }

        return new Question(
                id,
                authorId,
                statement.getText().trim(),
                context.getText().trim(),
                (String) difficulty.getSelectedItem(),
                category.getText().trim(),
                topic.getText().trim(),
                subtopic.getText().trim(),
                competence.getText().trim(),
                justification.getText().trim(),
                source.getText().trim(),
                status,
                list);
    }

    public void load(Question q) {

        category.setText(q.category());
        context.setText(q.context());
        statement.setText(q.statement());
        justification.setText(q.justification());
        source.setText(q.source());
        competence.setText(q.competence());
        topic.setText(q.topic());
        subtopic.setText(q.subtopic());

        for (int i = 0; i < options.length; i++) {
            options[i].setText(i < q.options().size()
                    ? q.options().get(i).text() : "");
            if (i < q.options().size() && q.options().get(i).correct()) {
                correct.setSelectedIndex(i);
            }
        }

        boolean known = false;
        for (int i = 0; i < difficulty.getItemCount(); i++) {
            known |= difficulty.getItemAt(i).equalsIgnoreCase(q.difficulty());
        }
        if (!known && !q.difficulty().isBlank()) {
            difficulty.addItem(q.difficulty());
        }
        difficulty.setSelectedItem(q.difficulty());

        highlightMissing();
    }

    public void clear() {

        for (JTextComponent field : required) {
            field.setText("");
        }

        correct.setSelectedIndex(0);
        difficulty.setSelectedIndex(0);
        highlightMissing();
    }

    public void setEditable(boolean editable) {

        for (JTextComponent field : required) {
            field.setEditable(editable);
        }

        correct.setEnabled(editable);
        difficulty.setEnabled(editable);
    }
}
