package co.edu.unicauca.lisw2t5g02.model;

/** State of one review or of the consolidated review of a question. */
public enum ReviewStatus {
    PENDING("Pendiente"),
    APPROVED("Aprobada"),
    REJECTED("Rechazada");

    private final String label;

    ReviewStatus(String label) {
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
