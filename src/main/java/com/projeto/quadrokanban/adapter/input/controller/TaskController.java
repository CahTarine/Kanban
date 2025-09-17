package com.projeto.quadrokanban.adapter.input.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.enums.TaskStatus;
import com.projeto.quadrokanban.core.port.input.TaskInputPort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/task")
@CrossOrigin(origins = "*", allowedHeaders = "*") //Anotação que libera o CORS, permitindo requisições de outras origens.
public class TaskController {
	
	private TaskInputPort taskInputPort;
	
	public TaskController(TaskInputPort taskInputPort) {
		this.taskInputPort = taskInputPort;
	}

	@GetMapping
	public ResponseEntity<List<Task>> getAll() {
		return ResponseEntity.ok(taskInputPort.getAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Task> getById(@PathVariable Long id) {
		Optional<Task> task = taskInputPort.getById(id);
		return task.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@GetMapping("/title/{title}")
	public ResponseEntity<List<Task>> getByTitle(@PathVariable String title) {
		return ResponseEntity.ok(taskInputPort.getByTitle(title));
	}
	
	@PostMapping
	public ResponseEntity<Task> post(@Valid @RequestBody Task task) {
	    Task savedTask = taskInputPort.createTaskWithBoard(task, task.getBoard().getId());
	    return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
	}


	
	@PutMapping("/{id}")
	public ResponseEntity<Task> put(@PathVariable Long id, @Valid @RequestBody Task task) {
	    try {
	        Task updatedTask = taskInputPort.updateTask(id, task);
	        return ResponseEntity.ok(updatedTask);
	    } catch (RuntimeException e) {
	        if (e.getMessage().contains("Not found")) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	        } else if (e.getMessage().contains("Board does not exist")) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
	        }
	        throw e;
	    }
	}

	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT) //Define Http 204 como padrão se der tudo certo, este método não tem corpo.
	public void delete(@PathVariable Long id) {
		Optional<Task> task = taskInputPort.getById(id);
		
		if (task.isEmpty()) 
			throw new ResponseStatusException(HttpStatus.NOT_FOUND); // Throw new = diz para o Java parar a execução normal do programa e lançar uma exceção.
		
		taskInputPort.deleteTask(id);
	}
	
	
	@GetMapping("/status/{status}")
	public ResponseEntity<List<Task>> getByStatus(@PathVariable String status){
		try {
	        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
	        return ResponseEntity.ok(taskInputPort.getByStatus(taskStatus));
	    } catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(null); // Retorne um erro 400 se o status for inválido
	    }
	}
	
	@GetMapping("/board/{boardId}")
	public ResponseEntity<List<Task>> getByBoard(@PathVariable Long boardId) {
		return ResponseEntity.ok(taskInputPort.getByBoard(boardId));
	}
		
	@GetMapping("/board-status/{boardId}/{status}")
	public ResponseEntity<List<Task>> getByBoardIdAndStatus(@PathVariable Long boardId, @PathVariable String status){
		try {
			TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
			return ResponseEntity.ok(taskInputPort.getByBoardAndStatus(boardId, taskStatus));
		}  catch (IllegalArgumentException e) {
	        return ResponseEntity.badRequest().body(null); // Retorne um erro 400 se o status for inválido
	    }
	}
	
	@GetMapping("/last-task")
	public ResponseEntity<Optional<Task>> getLastCreatedTask(){
		return ResponseEntity.ok(taskInputPort.getLastCreatedTask());
	}
	
	@GetMapping("/duedate/{dueDateString}")
	public ResponseEntity<List<Task>> getByDueDate(@PathVariable String dueDateString){
		
//		Define o formato que esperamos na URL
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//		Faz a conversão do formato esperado para o formato LocalDateTime
		LocalDate dueDate = LocalDate.parse(dueDateString, formatter);
		
		return ResponseEntity.ok(taskInputPort.getByDueDate(dueDate));
	}
}
