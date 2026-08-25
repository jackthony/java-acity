package pe.com.acity.sdlc.caseflow.application;

import reactor.core.publisher.Mono;

public interface IdentityVerifier {

    Mono<VerificationResult> verify(String applicantDocument, String applicantName);
}
