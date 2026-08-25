package pe.com.acity.sdlc.caseflow.domain;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class UnifiedCase {

    private final UUID id;
    private final String idempotencyKey;
    private final String requestFingerprint;
    private final String applicantDocument;
    private final String applicantName;
    private final Instant createdAt;
    private final List<CaseTransition> history;
    private CaseStatus status;

    private UnifiedCase(
            UUID id,
            String idempotencyKey,
            String requestFingerprint,
            String applicantDocument,
            String applicantName,
            Instant createdAt,
            String actor) {
        this.id = Objects.requireNonNull(id);
        this.idempotencyKey = requireText(idempotencyKey, "idempotencyKey");
        this.requestFingerprint = requireText(requestFingerprint, "requestFingerprint");
        this.applicantDocument = requireText(applicantDocument, "applicantDocument");
        this.applicantName = requireText(applicantName, "applicantName");
        this.createdAt = Objects.requireNonNull(createdAt);
        this.status = CaseStatus.SUBMITTED;
        this.history = new ArrayList<>();
        this.history.add(new CaseTransition(status, actor, "case submitted", createdAt));
    }

    public static UnifiedCase submit(
            String idempotencyKey,
            String requestFingerprint,
            String applicantDocument,
            String applicantName,
            String actor,
            Instant now) {
        return new UnifiedCase(
                UUID.randomUUID(),
                idempotencyKey,
                requestFingerprint,
                applicantDocument,
                applicantName,
                now,
                actor);
    }

    public void validate(String actor, String reason, Instant now) {
        transition(CaseStatus.SUBMITTED, CaseStatus.VALIDATED, actor, reason, now);
    }

    public void sendToReview(String actor, String reason, Instant now) {
        transition(CaseStatus.VALIDATED, CaseStatus.PENDING_REVIEW, actor, reason, now);
    }

    public void decide(CaseDecision decision, String actor, String reason, Instant now) {
        Objects.requireNonNull(decision, "decision is required");
        CaseStatus target = decision == CaseDecision.APPROVE
                ? CaseStatus.APPROVED
                : CaseStatus.REJECTED;
        transition(CaseStatus.PENDING_REVIEW, target, actor, reason, now);
    }

    private void transition(
            CaseStatus expected,
            CaseStatus target,
            String actor,
            String reason,
            Instant now) {
        if (status != expected) {
            throw new InvalidCaseTransitionException(status, target);
        }
        status = target;
        history.add(new CaseTransition(target, actor, reason, now));
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }

    public UUID id() {
        return id;
    }

    public String idempotencyKey() {
        return idempotencyKey;
    }

    public String requestFingerprint() {
        return requestFingerprint;
    }

    public String applicantDocument() {
        return applicantDocument;
    }

    public String applicantName() {
        return applicantName;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public CaseStatus status() {
        return status;
    }

    public List<CaseTransition> history() {
        return List.copyOf(history);
    }
}
