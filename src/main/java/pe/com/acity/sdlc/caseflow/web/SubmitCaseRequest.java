package pe.com.acity.sdlc.caseflow.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SubmitCaseRequest(
        @NotBlank @Size(max = 32) String applicantDocument,
        @NotBlank @Size(max = 160) String applicantName) {
}
