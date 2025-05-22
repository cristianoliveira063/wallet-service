package br.com.wallet.api.exceptionhandler;

import br.com.wallet.domain.exception.DuplicateUserWalletException;
import br.com.wallet.domain.exception.DuplicateWalletNameException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(@NonNull EntityNotFoundException ex, @NonNull WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;

        ProblemDetail problemDetail = ProblemDetail.builder()
                .status(status.value())
                .title("Resource not found")
                .detail(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(@NonNull IllegalArgumentException ex, @NonNull WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ProblemDetail problemDetail = ProblemDetail.builder()
                .status(status.value())
                .title("Business rule violation")
                .detail(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Object> handleNullPointer(@NonNull NullPointerException ex, @NonNull WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;

        ProblemDetail problemDetail = ProblemDetail.builder()
                .status(status.value())
                .title("Business rule violation")
                .detail(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(DuplicateWalletNameException.class)
    public ResponseEntity<Object> handleDuplicateWalletName(@NonNull DuplicateWalletNameException ex, @NonNull WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        ProblemDetail problemDetail = ProblemDetail.builder()
                .status(status.value())
                .title("Duplicate wallet name")
                .detail(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @ExceptionHandler(DuplicateUserWalletException.class)
    public ResponseEntity<Object> handleDuplicateUserWallet(@NonNull DuplicateUserWalletException ex, @NonNull WebRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;

        ProblemDetail problemDetail = ProblemDetail.builder()
                .status(status.value())
                .title("Duplicate user-wallet association")
                .detail(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return handleExceptionInternal(ex, problemDetail, new HttpHeaders(), status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ProblemDetail problemDetail = ProblemDetail.builder()
                .status(status.value())
                .title("Validation error")
                .detail("One or more fields are invalid")
                .timestamp(LocalDateTime.now())
                .fields(errors)
                .build();

        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }
}
