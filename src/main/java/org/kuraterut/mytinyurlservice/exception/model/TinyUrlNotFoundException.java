package org.kuraterut.mytinyurlservice.exception.model;

public class TinyUrlNotFoundException extends RuntimeException {
    public TinyUrlNotFoundException(String message) {
        super(message);
    }
}
