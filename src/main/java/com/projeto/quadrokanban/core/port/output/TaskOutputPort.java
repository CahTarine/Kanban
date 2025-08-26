package com.projeto.quadrokanban.core.port.output;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.model.Task;

public interface TaskOutputPort {

	List<Task> findAll();
	
	Optional<Task> findById(Long id);
	
	List<Task> findAllByTitleContainingIgnoreCase(String title);
	
	Task save(Task task);
	
	void deleteById(Long id);
}
