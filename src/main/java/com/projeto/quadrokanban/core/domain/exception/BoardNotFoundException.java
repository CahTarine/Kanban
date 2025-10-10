package com.projeto.quadrokanban.core.domain.exception;

public class BoardNotFoundException extends RuntimeException{
	
//	RuntimeException é usado em casos onde a causa do problema não é o estado do objeto, 
//	mas sim uma falha de lógica, uma regra de negócio violada 
//	ou um recurso que não existe.
	
	public BoardNotFoundException (String message) {
		super(message);
	}

}
