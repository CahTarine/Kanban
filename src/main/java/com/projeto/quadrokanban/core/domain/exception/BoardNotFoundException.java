package com.projeto.quadrokanban.core.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class BoardNotFoundException extends RuntimeException{
	
//	RuntimeException é usado em casos onde a causa do problema não é o estado do objeto, 
//	mas sim uma falha de lógica, uma regra de negócio violada 
//	ou um recurso que não existe.
	
	public BoardNotFoundException (String message) {
		super(message);
	}

}
