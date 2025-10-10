package com.projeto.quadrokanban.core.port.output;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.enums.TaskStatus;

public interface TaskOutputPort {

	List<Task> findAll();
	
	Optional<Task> findById(Long id);
	
	List<Task> findAllByTitleContainingIgnoreCase(String title);
	
	Task save(Task task);
	
	void deleteById(Long id);
	
	List<Task> findAllByStatus(TaskStatus status);
	
	List<Task> findAllByBoard(Long boardId);
	
	List<Task> findByBoardAndStatus(Long boardId, TaskStatus status);
	
	Optional<Task> findLastCreatedTask();
	
	List<Task> findByDueDate(LocalDate dueDate);
}
