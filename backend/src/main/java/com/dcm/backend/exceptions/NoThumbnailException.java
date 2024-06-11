package com.dcm.backend.exceptions;

/**
 * Exception thrown when a client tries to access a thumbnail on a file that does not have
 * one.
 */
public class NoThumbnailException extends Exception {

    public NoThumbnailException(String message) {
        super(message);
    }

    public NoThumbnailException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoThumbnailException(Throwable cause) {
        super(cause);
    }

    public NoThumbnailException() {
        super();
    }

}
