package pe.com.acity.sdlc.caseflow.infrastructure;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.core.Single;
import pe.com.acity.sdlc.caseflow.application.VerificationResult;

@Component
public class DeterministicRxIdentityClient implements RxIdentityClient {

    @Override
    public Single<VerificationResult> verify(
            String applicantDocument,
            String applicantName) {
        boolean verified = !applicantDocument.endsWith("0");
        String reason = verified
                ? "verified"
                : "document rejected by deterministic test rule";
        return Single.just(new VerificationResult(
                        verified,
                        "local-rxjava-verifier-v1",
                        reason))
                .delay(5, TimeUnit.MILLISECONDS);
    }
}
