package com.projeto.quadrokanban.core.usecase;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.port.output.TaskOutputPort;

public class TaskUseCase {
	
	private final TaskOutputPort taskOutputPort;
	
	public TaskUseCase (TaskOutputPort taskOutputPort) {
		this.taskOutputPort = taskOutputPort;
	}
	
	
	
	public List<Task> getAll(){
		return taskOutputPort.findAll();
	}

	public Optional<Task> getById(Long id) {
		return taskOutputPort.findById(id);
	}
	
	public List<Task> getByTitle(String title) {
		return taskOutputPort.findAllByTitleContainingIgnoreCase(title);
	}
	
	public Task createdTask(Task task) {
		return taskOutputPort.save(task);
	}
	
	public Optional<Task> updateTask(Long id, Task task){
		 return taskOutputPort.findById(id).map(existing -> {
			task.setId(id);
			return taskOutputPort.save(task);
		 });
	}
	
	public void deleteTask(Long id) {
		taskOutputPort.deleteById(id);
	}
	
	public boolean existsById(Long id) {
	    return taskOutputPort.findById(id).isPresent();
	}

}
