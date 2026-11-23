package co.edu.unicauca.lisw2t5g02.model;

/** Roles of the question bank (same names as Identity Service). */
public enum Role {
    ADMIN("Administrador"),
    AUTHOR("Autor de preguntas"),
    REVIEWER("Revisor");

    private final String label;

    Role(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }
}
