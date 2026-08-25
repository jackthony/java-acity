package pe.com.acity.sdlc.caseflow.web;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import pe.com.acity.sdlc.caseflow.application.CaseApplicationService;
import pe.com.acity.sdlc.caseflow.application.IdentityVerifier;
import pe.com.acity.sdlc.caseflow.application.VerificationResult;
import pe.com.acity.sdlc.caseflow.infrastructure.InMemoryCaseRepository;
import reactor.core.publisher.Mono;

class CaseControllerTest {

    private WebTestClient client;

    @BeforeEach
    void setUp() {
        IdentityVerifier verifier = (document, name) ->
                Mono.just(new VerificationResult(true, "test-verifier", "verified"));
        CaseApplicationService service = new CaseApplicationService(
                new InMemoryCaseRepository(),
                verifier,
                Clock.fixed(
                        Instant.parse("2026-08-24T12:00:00Z"),
                        ZoneOffset.UTC));
        client = WebTestClient.bindToController(new CaseController(service))
                .controllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void postCasesCreatesAndReplaysAnIdempotentRequest() {
        String body = """
                {
                  "applicantDocument": "12345678",
                  "applicantName": "Ada Lovelace"
                }
                """;

        client.post()
                .uri("/api/v1/cases")
                .header("Idempotency-Key", "key-1")
                .header("X-Actor-Id", "creator")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("Location")
                .expectBody()
                .jsonPath("$.status").isEqualTo("PENDING_REVIEW")
                .jsonPath("$.history.length()").isEqualTo(3);

        client.post()
                .uri("/api/v1/cases")
                .header("Idempotency-Key", "key-1")
                .header("X-Actor-Id", "creator")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void postCasesReturnsProblemDetailsForAnIdempotencyConflict() {
        submit("key-1", "Ada Lovelace").expectStatus().isCreated();

        submit("key-1", "Grace Hopper")
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.title").isEqualTo("Case conflict")
                .jsonPath("$.status").isEqualTo(409);
    }

    private WebTestClient.ResponseSpec submit(String key, String name) {
        return client.post()
                .uri("/api/v1/cases")
                .header("Idempotency-Key", key)
                .header("X-Actor-Id", "creator")
                .bodyValue(new SubmitCaseRequest("12345678", name))
                .exchange();
    }
}
