package co.edu.unicauca.lisw2t5g02.model;

/** Lifecycle states of a question (same names as Question Service). */
public enum QuestionStatus {
    DRAFT("Borrador"),
    PENDING_REVIEW("Pendiente de revisión"),
    IN_REVIEW("En revisión"),
    APPROVED("Aprobada"),
    REJECTED("Rechazada");

    private final String label;

    QuestionStatus(String label) {
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
