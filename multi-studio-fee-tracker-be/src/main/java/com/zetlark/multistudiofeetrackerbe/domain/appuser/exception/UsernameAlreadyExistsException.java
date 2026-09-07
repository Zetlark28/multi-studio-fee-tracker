package com.zetlark.multistudiofeetrackerbe.domain.appuser.exception;

public class UsernameAlreadyExistsException extends RuntimeException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
