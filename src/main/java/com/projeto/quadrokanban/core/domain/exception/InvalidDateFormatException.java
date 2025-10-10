package com.projeto.quadrokanban.core.domain.exception;

public class InvalidDateFormatException extends RuntimeException{

    public InvalidDateFormatException(String message){
        super(message);
    }
}
