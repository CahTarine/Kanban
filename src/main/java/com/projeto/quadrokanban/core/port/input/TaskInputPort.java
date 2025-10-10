package com.projeto.quadrokanban.core.port.input;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.model.Task;

public interface TaskInputPort {
	
	List<Task> getAll();
	
	Optional<Task> getById(Long id);
	 
	List<Task> getByTitle(String title);
	
	Task updateTask(Long id, Task task);
	
	void deleteTask(Long id);
	
	boolean existsById(Long id);
	
	Task createTaskWithBoard(Task task, Long id);

}
