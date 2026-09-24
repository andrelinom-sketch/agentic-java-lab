package dev.agenticlab.common.web;

import dev.agenticlab.account.service.AccountNotFoundException;
import dev.agenticlab.account.service.DestinationAccountNotFoundException;
import dev.agenticlab.account.service.InsufficientFundsException;
import dev.agenticlab.account.service.SourceAccountNotFoundException;
import dev.agenticlab.transfer.service.SameAccountException;
import dev.agenticlab.transfer.service.TransferNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Único tratador global de erros (AD-7, AD-1 "common" é o único pacote
 * transversal). Corpo em Problem Details (RFC 9457) com um código estável
 * na propriedade {@code code}.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    private static final String ACCOUNT_NOT_FOUND = "ACCOUNT_NOT_FOUND";
    private static final String SOURCE_ACCOUNT_NOT_FOUND = "SOURCE_ACCOUNT_NOT_FOUND";
    private static final String DESTINATION_ACCOUNT_NOT_FOUND = "DESTINATION_ACCOUNT_NOT_FOUND";
    private static final String INSUFFICIENT_FUNDS = "INSUFFICIENT_FUNDS";
    private static final String SAME_ACCOUNT = "SAME_ACCOUNT";
    private static final String TRANSFER_NOT_FOUND = "TRANSFER_NOT_FOUND";

    @ExceptionHandler(AccountNotFoundException.class)
    public ProblemDetail handleAccountNotFound(AccountNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage(), ACCOUNT_NOT_FOUND);
    }

    @ExceptionHandler(TransferNotFoundException.class)
    public ProblemDetail handleTransferNotFound(TransferNotFoundException ex) {
        return problem(HttpStatus.NOT_FOUND, ex.getMessage(), TRANSFER_NOT_FOUND);
    }

    @ExceptionHandler(SourceAccountNotFoundException.class)
    public ProblemDetail handleSourceAccountNotFound(SourceAccountNotFoundException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), SOURCE_ACCOUNT_NOT_FOUND);
    }

    @ExceptionHandler(DestinationAccountNotFoundException.class)
    public ProblemDetail handleDestinationAccountNotFound(DestinationAccountNotFoundException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), DESTINATION_ACCOUNT_NOT_FOUND);
    }

    @ExceptionHandler(InsufficientFundsException.class)
    public ProblemDetail handleInsufficientFunds(InsufficientFundsException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), INSUFFICIENT_FUNDS);
    }

    @ExceptionHandler(SameAccountException.class)
    public ProblemDetail handleSameAccount(SameAccountException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), SAME_ACCOUNT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Validation failed", VALIDATION_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleMalformedBody(HttpMessageNotReadableException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Malformed request body", VALIDATION_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return problem(
                HttpStatus.BAD_REQUEST,
                "Invalid value for parameter '" + ex.getName() + "'",
                VALIDATION_ERROR);
    }

    private ProblemDetail problem(HttpStatus status, String detail, String code) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setProperty("code", code);
        return problemDetail;
    }
}
