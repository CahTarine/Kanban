package com.projeto.quadrokanban.adapter.input;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.repositories.BoardRepository;
import com.projeto.quadrokanban.repositories.TaskRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/tasks")
@CrossOrigin(origins = "*", allowedHeaders = "*") //Pesquisar
public class TaskController {
	
	@Autowired // pesquisar
	private TaskRepository taskRepository;
	
	@Autowired
	private BoardRepository boardRepository;

	@GetMapping
	public ResponseEntity<List<Task>> getAll() {
		return ResponseEntity.ok(taskRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Task> getById(@PathVariable Long id) {
		Optional<Task> task = taskRepository.findById(id);
		return task.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@GetMapping("/title/{title}")
	public ResponseEntity<List<Task>> getByTitle(@PathVariable String title) {
		return ResponseEntity.ok(taskRepository.findAllByTitleContainingIgnoreCase(title));
	}
	
	@PostMapping
	public ResponseEntity<Task> post(@Valid @RequestBody Task task){

	    // Busca o Board no banco pelo ID enviado no JSON
	    Board board = boardRepository.findById(
	            task.getBoard() != null ? task.getBoard().getId() : null
	    ).orElseThrow(() -> 
	            new ResponseStatusException(HttpStatus.BAD_REQUEST, "Board does not exist")
	    );

	    // Associa o Board à Task
	    task.setBoard(board);

	    Task savedTask = taskRepository.save(task);

	    return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);
	}

	
	@PutMapping("/{id}")
	public ResponseEntity<Task> put(@PathVariable Long id, @Valid @RequestBody Task task) {
		if (!taskRepository.existsById(id))
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
			
		if (!boardRepository.existsById(task.getBoard().getId()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Board does not exist");
			
			
		task.setId(id); //Para garantir que estamos atualizando a task certa;
		Task updatedTask = taskRepository.save(task);
		
		return ResponseEntity.ok(updatedTask);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT) //Define Http 204 como padrão se der tudo certo, este método não tem corpo.
	public void delete(@PathVariable Long id) {
		Optional<Task> task = taskRepository.findById(id);
		
		if (task.isEmpty()) 
			throw new ResponseStatusException(HttpStatus.NOT_FOUND); // Throw new = diz para o Java parar a execução normal do programa e lançar uma exceção.
		
		taskRepository.deleteById(id);
	}
	
}
