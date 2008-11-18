package com.mindalliance.channels;

/**
 * Thrown when an object could not be added because one or more of its keys
 * were already defined in another object.
 */
public class DuplicateKeyException extends Exception {

    private static final long serialVersionUID = -5009949833707633582L;

    public DuplicateKeyException() {
    }
}
