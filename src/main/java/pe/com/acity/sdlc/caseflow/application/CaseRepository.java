package pe.com.acity.sdlc.caseflow.application;

import java.util.UUID;

import pe.com.acity.sdlc.caseflow.domain.UnifiedCase;
import reactor.core.publisher.Mono;

public interface CaseRepository {

    Mono<UnifiedCase> findById(UUID id);

    Mono<UnifiedCase> findByIdempotencyKey(String key);

    Mono<InsertResult> insertIfAbsent(UnifiedCase value);

    Mono<UnifiedCase> save(UnifiedCase value);

    record InsertResult(UnifiedCase value, boolean inserted) {
    }
}
