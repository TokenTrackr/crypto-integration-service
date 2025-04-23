package com.tokentrackr.crypto_integration_service.exception;

public class ApiIntegrationException extends RuntimeException {

    public ApiIntegrationException(String message) {
        super(message);
    }

    public ApiIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
