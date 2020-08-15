package com.github.zachcloud.exceptions;

public class ReadingException extends RuntimeException {

    public ReadingException() {
        super();
    }

    public ReadingException(Exception ex) {
        super(ex);
    }

    public ReadingException(String message) {
        super(message);
    }
}
