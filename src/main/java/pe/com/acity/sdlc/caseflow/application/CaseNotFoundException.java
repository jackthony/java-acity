package pe.com.acity.sdlc.caseflow.application;

import java.util.UUID;

public final class CaseNotFoundException extends RuntimeException {

    public CaseNotFoundException(UUID id) {
        super("Case not found: " + id);
    }
}
