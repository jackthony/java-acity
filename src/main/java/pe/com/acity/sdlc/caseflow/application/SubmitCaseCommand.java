package pe.com.acity.sdlc.caseflow.application;

public record SubmitCaseCommand(
        String idempotencyKey,
        String actor,
        String applicantDocument,
        String applicantName) {

    public SubmitCaseCommand {
        idempotencyKey = requireText(idempotencyKey, "idempotencyKey");
        actor = requireText(actor, "actor");
        applicantDocument = requireText(applicantDocument, "applicantDocument");
        applicantName = requireText(applicantName, "applicantName");
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value.trim();
    }
}
