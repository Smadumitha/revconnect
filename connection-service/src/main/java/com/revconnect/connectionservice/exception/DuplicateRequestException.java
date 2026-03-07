package com.revconnect.connectionservice.exception;

public class DuplicateRequestException extends RuntimeException {

    public DuplicateRequestException(String message){
        super(message);
    }
}