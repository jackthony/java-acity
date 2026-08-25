package pe.com.acity.sdlc.caseflow.application;

public final class IdempotencyConflictException extends RuntimeException {

    public IdempotencyConflictException(String key) {
        super("Idempotency key already belongs to a different request: " + key);
    }
}
