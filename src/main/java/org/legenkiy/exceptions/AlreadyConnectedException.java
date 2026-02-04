package org.legenkiy.exceptions;

public class AlreadyConnectedException extends RuntimeException {
    public AlreadyConnectedException(String message) {
        super(message);
    }
}
