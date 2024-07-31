package com.dcm.backend.exceptions;

/**
 * Exception thrown when the application is in an incoherent state.
 */
public class IncoherentStateException extends RuntimeException {
    public IncoherentStateException(String message) {
        super(message);
    }
}
