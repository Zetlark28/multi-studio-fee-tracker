package com.zetlark.multistudiofeetrackerbe.application.config;

import com.zetlark.multistudiofeetrackerbe.application.common.exception.ResponseError;
import com.zetlark.multistudiofeetrackerbe.application.common.exception.UpdateEntityNotValid;
import com.zetlark.multistudiofeetrackerbe.domain.client.exception.ClientHasActiveServicesException;
import com.zetlark.multistudiofeetrackerbe.domain.clientbillableservice.exception.DuplicateServiceNameException;
import com.zetlark.multistudiofeetrackerbe.domain.activity.exception.InvalidActivityException;
import com.zetlark.multistudiofeetrackerbe.domain.appuser.exception.UsernameAlreadyExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleException(Exception exception) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseError(HttpStatus.INTERNAL_SERVER_ERROR.value(), exception.getMessage()));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ResponseError(HttpStatus.NOT_FOUND.value(), e.getMessage()));
    }


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        StringBuilder errorMessage = new StringBuilder();
        exception.getBindingResult().getFieldErrors()
                .forEach(fieldError -> errorMessage.append(fieldError.getField())
                        .append(" ").append(fieldError.getDefaultMessage()));

        ResponseError responseError = new ResponseError(exception.getStatusCode().value(), errorMessage.toString());
        return ResponseEntity.status(exception.getStatusCode().value()).body(responseError);
    }


    @ExceptionHandler(UpdateEntityNotValid.class)
    public ResponseEntity<Object> handleUpdateEntityNotValidException(UpdateEntityNotValid e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(DuplicateServiceNameException.class)
    public ResponseEntity<Object> handleDuplicateServiceName(DuplicateServiceNameException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseError(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler(ClientHasActiveServicesException.class)
    public ResponseEntity<Object> handleClientHasActiveServices(ClientHasActiveServicesException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseError(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<Object> handleUsernameAlreadyExists(UsernameAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ResponseError(HttpStatus.CONFLICT.value(), e.getMessage()));
    }

    @ExceptionHandler(InvalidActivityException.class)
    public ResponseEntity<Object> handleInvalidActivity(InvalidActivityException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied(AccessDeniedException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ResponseError(HttpStatus.FORBIDDEN.value(), e.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ResponseError(HttpStatus.UNAUTHORIZED.value(), "Credenziali non valide."));
    }
}