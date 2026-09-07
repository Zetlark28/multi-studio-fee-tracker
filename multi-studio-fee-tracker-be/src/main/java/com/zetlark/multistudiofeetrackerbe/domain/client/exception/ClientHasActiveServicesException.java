package com.zetlark.multistudiofeetrackerbe.domain.client.exception;

public class ClientHasActiveServicesException extends RuntimeException {
    public ClientHasActiveServicesException(String message) {
        super(message);
    }
}
