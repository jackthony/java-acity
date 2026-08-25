package pe.com.acity.sdlc.caseflow.infrastructure;

import io.reactivex.rxjava3.core.Single;
import pe.com.acity.sdlc.caseflow.application.VerificationResult;

public interface RxIdentityClient {

    Single<VerificationResult> verify(String applicantDocument, String applicantName);
}
