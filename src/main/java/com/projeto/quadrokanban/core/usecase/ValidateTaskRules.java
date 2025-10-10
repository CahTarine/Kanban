package com.projeto.quadrokanban.core.usecase;

import org.springframework.stereotype.Component;

import com.projeto.quadrokanban.core.domain.exception.TaskValidationException;
import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.enums.TaskStatus;
import com.projeto.quadrokanban.core.port.output.TaskOutputPort;

@Component
public class ValidateTaskRules {
		private final TaskOutputPort taskOutputPort;
		
		public ValidateTaskRules(TaskOutputPort taskOutputPort) {
			this.taskOutputPort = taskOutputPort;
		}
		
		public void validateTaskRules(Task task) {
		    // Validação task "DONE" sem responsável
		    if (task.getStatus() == TaskStatus.DONE && task.getUserId() == null) {
		        throw new TaskValidationException("Tasks with 'DONE' status must have a responsible user.");
		    }
		    
		    // Validação limite de 5 tasks "Doing"
		    if (task.getStatus() == TaskStatus.DOING && task.getUserId() != null) {
		        Long doingCount = taskOutputPort.countDoingTasksByUserId(task.getUserId());
		        
		        // Se a tarefa já existe e está no status "Doing", diminua a contagem
		        // Isso evita que a tarefa "conte contra si mesma" na validação
		        if (task.getId() != null) {
		            Task existingTask = taskOutputPort.findById(task.getId()).orElse(null);
		            if (existingTask != null && existingTask.getStatus() == TaskStatus.DOING) {
		                doingCount--;
		            }
		        }
		        
		        if (doingCount >= 5) {
		            throw new TaskValidationException("User already has 5 tasks in 'Doing' status. Limit reached.");
		    }
		}
	}
}
