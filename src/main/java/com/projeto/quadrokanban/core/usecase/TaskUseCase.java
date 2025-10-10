package com.projeto.quadrokanban.core.usecase;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.exception.InvalidDateFormatException;
import com.projeto.quadrokanban.core.domain.exception.InvalidStatusException;
import com.projeto.quadrokanban.core.domain.exception.TaskNotFoundException;
import com.projeto.quadrokanban.core.port.output.NotificationOutputPort;
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
	private final ValidateTaskRules validateTaskRules;
    private final NotificationOutputPort notificationPort;
	
	public TaskUseCase (TaskOutputPort taskOutputPort, BoardValidatorService boardValidatorService, ValidateTaskRules validateTaskRules, NotificationOutputPort notificationPort) {
		this.taskOutputPort = taskOutputPort;
		this.boardValidatorService = boardValidatorService;
		this.validateTaskRules = validateTaskRules;
        this.notificationPort = notificationPort;
	}
	
	
	
	public List<Task> getAll(){
		return taskOutputPort.findAll();
	}

	public Task getById(Long id) {
		return taskOutputPort.findById(id)
                .orElseThrow(() -> new TaskNotFoundException("Task not found."));
	}
	
	public List<Task> getByTitle(String title) {
		return taskOutputPort.findAllByTitleContainingIgnoreCase(title);
	}
	
	
	public Task updateTask(Long id, Task task) {
		validateTaskRules.validateTaskRules(task);
		
		Task existingTask = taskOutputPort.findById(id)
				.orElseThrow(() -> new TaskNotFoundException("Task not found."));

        Long oldUserId = existingTask.getUserId(); // Guarda o id do antigo user
		
		Board board = boardValidatorService.validateBoardExists(task.getBoard().getId());
		
		existingTask.setTitle(task.getTitle());
		existingTask.setDescription(task.getDescription());
		existingTask.setStatus(task.getStatus());
		existingTask.setBoard(board);
		existingTask.setDueDate(task.getDueDate());
		existingTask.setUserId(task.getUserId());

        if(task.getUserId() != null && !task.getUserId().equals(oldUserId)) // Se o id do update nao for nulo e for diferente do antigo, notifica o novo user.
            notificationPort.notifyUser(existingTask);
		
	    return taskOutputPort.save(existingTask);
	}
	
	public void deleteTask(Long id) {
		taskOutputPort.findById(id)
                        .orElseThrow(() -> new TaskNotFoundException("Task not found."));
        taskOutputPort.deleteById(id);
	}
	
	 public Task createTaskWithBoard(Task task, Long id) {
		 validateTaskRules.validateTaskRules(task);
			 
	        Board board = boardValidatorService.validateBoardExists(id);
	        task.setBoard(board);

            Task savedTask = taskOutputPort.save(task);

            if(task.getUserId() != null)
                notificationPort.notifyUser(savedTask);

	        return savedTask;
	}
	 
	 public List<Task> getByStatus(String status){
         try {
             TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
             return taskOutputPort.findAllByStatus(taskStatus);
         } catch (IllegalArgumentException e) {
             throw new InvalidStatusException("Invalid status.");
         }
	 }
	 
	 public List<Task> getTaskByBoard(Long boardId){
            boardValidatorService.validateBoardExists(boardId);
            return taskOutputPort.findAllTaskByBoard(boardId);
	 }
	 
	 public List<Task> getByBoardAndStatus(Long boardId, String status){
         boardValidatorService.validateBoardExists(boardId);
         try {
             TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
             return taskOutputPort.findByBoardAndStatus(boardId, taskStatus);
         }  catch (IllegalArgumentException e) {
             throw new InvalidStatusException("Invalid status.");
         }
	 }
	 
	 public Optional<Task> getLastCreatedTask(){
		 return taskOutputPort.findLastCreatedTask();
	 }
	 
	 public List<Task> getByDueDate(String dueDateString){
        try{
//          Define o formato que esperamos na URL
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		    Faz a conversão do formato esperado para o formato LocalDateTime
            LocalDate dueDate = LocalDate.parse(dueDateString, formatter);
            return taskOutputPort.findByDueDate(dueDate);
        } catch (DateTimeParseException e){
            throw new InvalidDateFormatException("Invalid date format. Use yyyy-MM-dd.");
        }

	 }
	 
	 

}
