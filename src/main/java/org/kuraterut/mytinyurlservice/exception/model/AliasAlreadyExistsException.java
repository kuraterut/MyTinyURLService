package org.kuraterut.mytinyurlservice.exception.model;

public class AliasAlreadyExistsException extends RuntimeException {
    public AliasAlreadyExistsException(String message) {
        super(message);
    }
}
