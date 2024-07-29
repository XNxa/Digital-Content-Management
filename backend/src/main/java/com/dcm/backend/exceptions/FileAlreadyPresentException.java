package com.dcm.backend.exceptions;

/**
 * Exception thrown when a file is already present in the system
 */
public class FileAlreadyPresentException extends Exception {

    public FileAlreadyPresentException(String message) {
        super(message);
    }

    public FileAlreadyPresentException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileAlreadyPresentException(Throwable cause) {
        super(cause);
    }

    public FileAlreadyPresentException() {
        super();
    }

}
