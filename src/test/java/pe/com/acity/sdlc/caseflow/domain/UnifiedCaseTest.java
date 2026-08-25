package pe.com.acity.sdlc.caseflow.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;

import org.junit.jupiter.api.Test;

class UnifiedCaseTest {

    private static final Instant NOW = Instant.parse("2026-08-24T12:00:00Z");

    @Test
    void givenASubmittedCase_whenValidatedAndReviewed_thenHistoryIsAuditable() {
        UnifiedCase value = UnifiedCase.submit(
                "key-1",
                "fingerprint",
                "12345678",
                "Ada Lovelace",
                "creator",
                NOW);

        value.validate("validator", "identity verified", NOW.plusSeconds(1));
        value.sendToReview("validator", "rules passed", NOW.plusSeconds(2));
        value.decide(
                CaseDecision.APPROVE,
                "reviewer",
                "evidence accepted",
                NOW.plusSeconds(3));

        assertThat(value.status()).isEqualTo(CaseStatus.APPROVED);
        assertThat(value.history())
                .extracting(CaseTransition::status)
                .containsExactly(
                        CaseStatus.SUBMITTED,
                        CaseStatus.VALIDATED,
                        CaseStatus.PENDING_REVIEW,
                        CaseStatus.APPROVED);
    }

    @Test
    void givenAnApprovedCase_whenDecidedAgain_thenTransitionIsRejected() {
        UnifiedCase value = pendingReviewCase();
        value.decide(CaseDecision.APPROVE, "reviewer", "accepted", NOW);

        assertThatThrownBy(() ->
                value.decide(CaseDecision.REJECT, "reviewer", "changed mind", NOW))
                .isInstanceOf(InvalidCaseTransitionException.class)
                .hasMessageContaining("APPROVED")
                .hasMessageContaining("REJECTED");
    }

    private static UnifiedCase pendingReviewCase() {
        UnifiedCase value = UnifiedCase.submit(
                "key-1",
                "fingerprint",
                "12345678",
                "Ada Lovelace",
                "creator",
                NOW);
        value.validate("validator", "verified", NOW);
        value.sendToReview("validator", "rules passed", NOW);
        return value;
    }
}
