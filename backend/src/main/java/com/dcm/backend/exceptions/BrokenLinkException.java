package com.dcm.backend.exceptions;

public class BrokenLinkException extends Exception {

    public BrokenLinkException() {
        super();
    }

    public BrokenLinkException(String message) {
        super(message);
    }
}
