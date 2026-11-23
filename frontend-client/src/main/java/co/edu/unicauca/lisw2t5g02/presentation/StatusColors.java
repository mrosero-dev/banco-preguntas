package co.edu.unicauca.lisw2t5g02.presentation;

import java.awt.Color;

import co.edu.unicauca.lisw2t5g02.model.QuestionStatus;

/** Distinctive color of each question state (HU-1.2, scenario 4). */
public final class StatusColors {

    private StatusColors() {
    }

    public static Color of(QuestionStatus status) {
        return switch (status) {
            case DRAFT -> new Color(180, 120, 0);
            case PENDING_REVIEW -> new Color(30, 90, 170);
            case IN_REVIEW -> new Color(110, 60, 160);
            case APPROVED -> new Color(30, 110, 60);
            case REJECTED -> new Color(170, 30, 30);
        };
    }
}
