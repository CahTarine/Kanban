package com.projeto.quadrokanban.core.domain.exception;

public class InvalidStatusException extends IllegalArgumentException{

    public InvalidStatusException(String message){
        super(message);
    }
}
