package pe.com.acity.sdlc.caseflow.domain;

public final class InvalidCaseTransitionException extends RuntimeException {

    public InvalidCaseTransitionException(CaseStatus current, CaseStatus target) {
        super("Cannot transition case from " + current + " to " + target);
    }
}
