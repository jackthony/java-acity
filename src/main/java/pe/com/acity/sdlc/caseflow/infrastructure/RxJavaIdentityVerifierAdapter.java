package pe.com.acity.sdlc.caseflow.infrastructure;

import org.springframework.stereotype.Component;

import io.reactivex.rxjava3.disposables.Disposable;
import pe.com.acity.sdlc.caseflow.application.IdentityVerifier;
import pe.com.acity.sdlc.caseflow.application.VerificationResult;
import reactor.core.publisher.Mono;

@Component
public class RxJavaIdentityVerifierAdapter implements IdentityVerifier {

    private final RxIdentityClient client;

    public RxJavaIdentityVerifierAdapter(RxIdentityClient client) {
        this.client = client;
    }

    @Override
    public Mono<VerificationResult> verify(
            String applicantDocument,
            String applicantName) {
        return Mono.create(sink -> {
            Disposable subscription = client.verify(applicantDocument, applicantName)
                    .subscribe(sink::success, sink::error);
            sink.onCancel(subscription::dispose);
        });
    }
}
