package pe.com.acity.sdlc.caseflow.domain;

import java.time.Instant;
import java.util.Objects;

public record CaseTransition(
        CaseStatus status,
        String actor,
        String reason,
        Instant occurredAt) {

    public CaseTransition {
        Objects.requireNonNull(status, "status is required");
        Objects.requireNonNull(occurredAt, "occurredAt is required");
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
