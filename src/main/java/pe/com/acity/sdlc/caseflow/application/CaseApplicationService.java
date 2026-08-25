package pe.com.acity.sdlc.caseflow.application;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Clock;
import java.time.Instant;
import java.util.HexFormat;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Service;

import pe.com.acity.sdlc.caseflow.domain.UnifiedCase;
import reactor.core.publisher.Mono;

@Service
public class CaseApplicationService {

    private final CaseRepository repository;
    private final IdentityVerifier verifier;
    private final Clock clock;

    public CaseApplicationService(
            CaseRepository repository,
            IdentityVerifier verifier,
            Clock clock) {
        this.repository = repository;
        this.verifier = verifier;
        this.clock = clock;
    }

    public Mono<CreateCaseResult> submit(SubmitCaseCommand command) {
        String fingerprint = fingerprint(command);
        return repository.findByIdempotencyKey(command.idempotencyKey())
                .map(existing -> replay(existing, fingerprint))
                .switchIfEmpty(Mono.defer(() -> create(command, fingerprint)));
    }

    public Mono<UnifiedCase> find(UUID id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new CaseNotFoundException(id)));
    }

    public Mono<UnifiedCase> decide(DecideCaseCommand command) {
        return find(command.caseId())
                .flatMap(value -> {
                    value.decide(
                            command.decision(),
                            command.actor(),
                            command.reason(),
                            clock.instant());
                    return repository.save(value);
                });
    }

    private Mono<CreateCaseResult> create(SubmitCaseCommand command, String fingerprint) {
        Instant now = clock.instant();
        UnifiedCase candidate = UnifiedCase.submit(
                command.idempotencyKey(),
                fingerprint,
                command.applicantDocument(),
                command.applicantName(),
                command.actor(),
                now);

        return repository.insertIfAbsent(candidate)
                .flatMap(result -> {
                    if (!result.inserted()) {
                        return Mono.just(replay(result.value(), fingerprint));
                    }
                    return verifyAndAdvance(candidate, command.actor());
                });
    }

    private Mono<CreateCaseResult> verifyAndAdvance(UnifiedCase value, String actor) {
        return verifier.verify(value.applicantDocument(), value.applicantName())
                .flatMap(result -> {
                    if (!result.verified()) {
                        return Mono.error(new IdentityVerificationException(result.reason()));
                    }
                    value.validate(actor, "identity verified by " + result.source(), clock.instant());
                    value.sendToReview(actor, "automatic validation completed", clock.instant());
                    return repository.save(value).map(saved -> new CreateCaseResult(saved, true));
                });
    }

    private CreateCaseResult replay(UnifiedCase existing, String fingerprint) {
        if (!existing.requestFingerprint().equals(fingerprint)) {
            throw new IdempotencyConflictException(existing.idempotencyKey());
        }
        return new CreateCaseResult(existing, false);
    }

    private static String fingerprint(SubmitCaseCommand command) {
        String canonical = normalize(command.applicantDocument())
                + "\n"
                + normalize(command.applicantName());
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(canonical.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }

    private static String normalize(String value) {
        return value.trim().replaceAll("\\s+", " ").toUpperCase(Locale.ROOT);
    }
}
