package pe.com.acity.sdlc.caseflow.infrastructure;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import pe.com.acity.sdlc.caseflow.application.CaseRepository;
import pe.com.acity.sdlc.caseflow.domain.UnifiedCase;
import reactor.core.publisher.Mono;

@Repository
@Profile("!jpa")
public class InMemoryCaseRepository implements CaseRepository {

    private final Map<UUID, UnifiedCase> byId = new HashMap<>();
    private final Map<String, UnifiedCase> byIdempotencyKey = new HashMap<>();

    @Override
    public Mono<UnifiedCase> findById(UUID id) {
        return Mono.defer(() -> Mono.justOrEmpty(findByIdNow(id)));
    }

    @Override
    public Mono<UnifiedCase> findByIdempotencyKey(String key) {
        return Mono.defer(() -> Mono.justOrEmpty(findByKeyNow(key)));
    }

    @Override
    public Mono<InsertResult> insertIfAbsent(UnifiedCase value) {
        return Mono.fromSupplier(() -> insertNow(value));
    }

    @Override
    public Mono<UnifiedCase> save(UnifiedCase value) {
        return Mono.fromSupplier(() -> saveNow(value));
    }

    private synchronized UnifiedCase findByIdNow(UUID id) {
        return byId.get(id);
    }

    private synchronized UnifiedCase findByKeyNow(String key) {
        return byIdempotencyKey.get(key);
    }

    private synchronized InsertResult insertNow(UnifiedCase value) {
        UnifiedCase existing = byIdempotencyKey.get(value.idempotencyKey());
        if (existing != null) {
            return new InsertResult(existing, false);
        }
        byId.put(value.id(), value);
        byIdempotencyKey.put(value.idempotencyKey(), value);
        return new InsertResult(value, true);
    }

    private synchronized UnifiedCase saveNow(UnifiedCase value) {
        if (!byId.containsKey(value.id())) {
            throw new IllegalStateException("Cannot update an unknown case: " + value.id());
        }
        byId.put(value.id(), value);
        byIdempotencyKey.put(value.idempotencyKey(), value);
        return value;
    }
}
