package pe.com.acity.sdlc.caseflow.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pe.com.acity.sdlc.caseflow.domain.CaseDecision;

public record DecisionRequest(
        @NotNull CaseDecision decision,
        @NotBlank @Size(max = 500) String reason) {
}
