package com.projeto.quadrokanban.core.domain.exception;

public class BoardValidationException extends IllegalStateException {
	
	public BoardValidationException (String message) {
		super(message);
	}

}
