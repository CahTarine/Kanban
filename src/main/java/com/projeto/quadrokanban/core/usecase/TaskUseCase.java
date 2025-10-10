package com.projeto.quadrokanban.core.usecase;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.enums.TaskStatus;
import com.projeto.quadrokanban.core.port.input.TaskInputPort;
import com.projeto.quadrokanban.core.port.output.TaskOutputPort;

@Service
public class TaskUseCase implements TaskInputPort {
	
	private final TaskOutputPort taskOutputPort;
	private final BoardValidatorService boardValidatorService;
	
	public TaskUseCase (TaskOutputPort taskOutputPort, BoardValidatorService boardValidatorService) {
		this.taskOutputPort = taskOutputPort;
		this.boardValidatorService = boardValidatorService;
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
	
	public Task updateTask(Long id, Task task) {
		if (!taskOutputPort.findById(id).isPresent())
		    throw new RuntimeException("Task not found");
	    
		Board board = boardValidatorService.validateBoardExists(task.getBoard().getId());
		
	    task.setId(id);
	    task.setBoard(board);
	    
	    return taskOutputPort.save(task);
	}
	
	public void deleteTask(Long id) {
		taskOutputPort.deleteById(id);
	}
	
	public boolean existsById(Long id) {
	    return taskOutputPort.findById(id).isPresent();
	}
	
	 public Task createTaskWithBoard(Task task, Long id) {
	        Board board = boardValidatorService.validateBoardExists(id);
	        task.setBoard(board);
	        return taskOutputPort.save(task);
	}
	 
	 public List<Task> getByStatus(TaskStatus status){
		 return taskOutputPort.findAllByStatus(status);
	 }
	 
	 public List<Task> getByBoard(Long boardId){
		 return taskOutputPort.findAllByBoard(boardId);
	 }
	 
	 public List<Task> getByBoardAndStatus(Long boardId, TaskStatus status){
		 return taskOutputPort.findByBoardAndStatus(boardId, status);
	 }
	 
	 public Optional<Task> getLastCreatedTask(){
		 return taskOutputPort.findLastCreatedTask();
	 }
	 
	 public List<Task> getByDueDate(LocalDate dueDate){
		 return taskOutputPort.findByDueDate(dueDate);
	 }

}
