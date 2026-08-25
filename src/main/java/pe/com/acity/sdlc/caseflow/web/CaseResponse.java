package pe.com.acity.sdlc.caseflow.web;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import pe.com.acity.sdlc.caseflow.domain.CaseStatus;
import pe.com.acity.sdlc.caseflow.domain.CaseTransition;
import pe.com.acity.sdlc.caseflow.domain.UnifiedCase;

public record CaseResponse(
        UUID id,
        String applicantDocument,
        String applicantName,
        CaseStatus status,
        Instant createdAt,
        List<TransitionResponse> history) {

    static CaseResponse from(UnifiedCase value) {
        return new CaseResponse(
                value.id(),
                value.applicantDocument(),
                value.applicantName(),
                value.status(),
                value.createdAt(),
                value.history().stream().map(TransitionResponse::from).toList());
    }

    public record TransitionResponse(
            CaseStatus status,
            String actor,
            String reason,
            Instant occurredAt) {

        static TransitionResponse from(CaseTransition value) {
            return new TransitionResponse(
                    value.status(),
                    value.actor(),
                    value.reason(),
                    value.occurredAt());
        }
    }
}
