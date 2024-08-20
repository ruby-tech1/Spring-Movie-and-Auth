package com.ruby_dev.movieapi.exceptions;

public class FileExistsException extends RuntimeException {

    public FileExistsException(String message) {
        super(message);
    }
}
