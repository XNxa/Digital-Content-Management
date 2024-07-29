package com.dcm.backend.exceptions;

/**
 * Exception thrown when a user is not found
 */
public class UserNotFoundException extends Exception {

    public UserNotFoundException(String message) {
        super(message);
    }

}
