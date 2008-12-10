package com.mindalliance.channels;

/**
 * Thrown when an object could not be added because one or more of its keys
 * were already defined in another object.
 */
public class DuplicateKeyException extends RuntimeException {

    public DuplicateKeyException() {
    }
}
