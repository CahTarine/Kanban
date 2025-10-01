package com.projeto.quadrokanban.core.domain.exception;

public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException (String message){
        super(message);
    }
}
