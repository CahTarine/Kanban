package com.projeto.quadrokanban.core.domain.exception;

public class TaskValidationException extends IllegalStateException{
	
//	IllegalStateException é usado quando o estado atual do objeto é o problema. 
//	A operação não pode ser feita agora.
//	Ex: o board nao pode ser fechado nesse momento porque ainda tem tasks em aberto.
	
	public TaskValidationException(String message) {
		super(message);
	}

}
