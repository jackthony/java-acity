package pe.com.acity.sdlc.caseflow.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import pe.com.acity.sdlc.caseflow.domain.CaseStatus;
import pe.com.acity.sdlc.caseflow.infrastructure.InMemoryCaseRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class CaseApplicationServiceTest {

    private final AtomicInteger verificationCalls = new AtomicInteger();
    private CaseApplicationService service;

    @BeforeEach
    void setUp() {
        IdentityVerifier verifier = (document, name) -> {
            verificationCalls.incrementAndGet();
            return Mono.just(new VerificationResult(true, "test-verifier", "verified"));
        };
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-24T12:00:00Z"),
                ZoneOffset.UTC);
        service = new CaseApplicationService(
                new InMemoryCaseRepository(),
                verifier,
                clock);
    }

    @Test
    void givenANewRequest_whenSubmitted_thenItAwaitsHumanReview() {
        StepVerifier.create(service.submit(command("key-1", "Ada Lovelace")))
                .assertNext(result -> {
                    assertThat(result.created()).isTrue();
                    assertThat(result.value().status()).isEqualTo(CaseStatus.PENDING_REVIEW);
                    assertThat(result.value().history()).hasSize(3);
                })
                .verifyComplete();

        assertThat(verificationCalls).hasValue(1);
    }

    @Test
    void givenTheSameKeyAndPayload_whenReplayed_thenTheOriginalIsReturned() {
        CreateCaseResult first = service.submit(command("key-1", "Ada Lovelace")).block();

        StepVerifier.create(service.submit(command("key-1", "  ada   lovelace ")))
                .assertNext(replay -> {
                    assertThat(replay.created()).isFalse();
                    assertThat(replay.value().id()).isEqualTo(first.value().id());
                })
                .verifyComplete();

        assertThat(verificationCalls).hasValue(1);
    }

    @Test
    void givenTheSameKeyAndDifferentPayload_whenSubmitted_thenItConflicts() {
        service.submit(command("key-1", "Ada Lovelace")).block();

        StepVerifier.create(service.submit(command("key-1", "Grace Hopper")))
                .expectError(IdempotencyConflictException.class)
                .verify();
    }

    private static SubmitCaseCommand command(String key, String name) {
        return new SubmitCaseCommand(key, "creator", "12345678", name);
    }
}
