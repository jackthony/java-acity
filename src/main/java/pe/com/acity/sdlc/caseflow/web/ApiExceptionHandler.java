package pe.com.acity.sdlc.caseflow.web;

import java.net.URI;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

import pe.com.acity.sdlc.caseflow.application.CaseNotFoundException;
import pe.com.acity.sdlc.caseflow.application.IdempotencyConflictException;
import pe.com.acity.sdlc.caseflow.application.IdentityVerificationException;
import pe.com.acity.sdlc.caseflow.domain.InvalidCaseTransitionException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(CaseNotFoundException.class)
    ProblemDetail handleNotFound(CaseNotFoundException exception) {
        return problem(HttpStatus.NOT_FOUND, "Case not found", exception.getMessage());
    }

    @ExceptionHandler({IdempotencyConflictException.class, InvalidCaseTransitionException.class})
    ProblemDetail handleConflict(RuntimeException exception) {
        return problem(HttpStatus.CONFLICT, "Case conflict", exception.getMessage());
    }

    @ExceptionHandler(IdentityVerificationException.class)
    ProblemDetail handleUnprocessable(IdentityVerificationException exception) {
        return problem(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Identity verification failed",
                exception.getMessage());
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            ConstraintViolationException.class,
            WebExchangeBindException.class,
            ServerWebInputException.class
    })
    ProblemDetail handleBadRequest(Exception exception) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", exception.getMessage());
    }

    private static ProblemDetail problem(HttpStatus status, String title, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create("https://acity.example/problems/" + status.value()));
        return problem;
    }
}
