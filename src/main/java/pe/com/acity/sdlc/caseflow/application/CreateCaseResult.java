package pe.com.acity.sdlc.caseflow.application;

import pe.com.acity.sdlc.caseflow.domain.UnifiedCase;

public record CreateCaseResult(UnifiedCase value, boolean created) {
}
