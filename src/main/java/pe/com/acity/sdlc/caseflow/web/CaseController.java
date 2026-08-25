package pe.com.acity.sdlc.caseflow.web;

import java.net.URI;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pe.com.acity.sdlc.caseflow.application.CaseApplicationService;
import pe.com.acity.sdlc.caseflow.application.DecideCaseCommand;
import pe.com.acity.sdlc.caseflow.application.SubmitCaseCommand;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping("/api/v1/cases")
public class CaseController {

    private final CaseApplicationService service;

    public CaseController(CaseApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ResponseEntity<CaseResponse>> submit(
            @RequestHeader("Idempotency-Key") @NotBlank @Size(max = 128) String key,
            @RequestHeader("X-Actor-Id") @NotBlank @Size(max = 120) String actor,
            @Valid @RequestBody SubmitCaseRequest request) {
        SubmitCaseCommand command = new SubmitCaseCommand(
                key,
                actor,
                request.applicantDocument(),
                request.applicantName());

        return service.submit(command)
                .map(result -> {
                    CaseResponse body = CaseResponse.from(result.value());
                    if (!result.created()) {
                        return ResponseEntity.ok(body);
                    }
                    URI location = URI.create("/api/v1/cases/" + body.id());
                    return ResponseEntity.created(location).body(body);
                });
    }

    @GetMapping("/{id}")
    public Mono<CaseResponse> find(@PathVariable UUID id) {
        return service.find(id).map(CaseResponse::from);
    }

    @PostMapping("/{id}/decisions")
    public Mono<CaseResponse> decide(
            @PathVariable UUID id,
            @RequestHeader("X-Actor-Id") @NotBlank @Size(max = 120) String actor,
            @Valid @RequestBody DecisionRequest request) {
        DecideCaseCommand command = new DecideCaseCommand(
                id,
                request.decision(),
                actor,
                request.reason());
        return service.decide(command).map(CaseResponse::from);
    }
}
