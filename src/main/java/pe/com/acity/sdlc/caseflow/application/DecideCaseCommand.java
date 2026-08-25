package pe.com.acity.sdlc.caseflow.application;

import java.util.Objects;
import java.util.UUID;

import pe.com.acity.sdlc.caseflow.domain.CaseDecision;

public record DecideCaseCommand(
        UUID caseId,
        CaseDecision decision,
        String actor,
        String reason) {

    public DecideCaseCommand {
        Objects.requireNonNull(caseId, "caseId is required");
        Objects.requireNonNull(decision, "decision is required");
        actor = requireText(actor, "actor");
        reason = requireText(reason, "reason");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }
}
