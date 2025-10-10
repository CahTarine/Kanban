package com.projeto.quadrokanban.adapter.input.controller;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.adapter.input.swagger.BoardSwagger;
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

import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.port.input.BoardInputPort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/board")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BoardController implements BoardSwagger {
	
	@Autowired
	private BoardInputPort boardInputPort;
	
	@GetMapping
	public ResponseEntity<List<Board>> getAll() {
		return ResponseEntity.ok(boardInputPort.getAllBoards());
	}
	
	
	@GetMapping("/{id}")
	public ResponseEntity<Board> getById(@PathVariable Long id) {
		return ResponseEntity.ok(boardInputPort.getById(id));
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Board>> getByName(@PathVariable String name){
		return ResponseEntity.ok(boardInputPort.getByName(name));
	}
	
	@PostMapping
	public ResponseEntity<Board> post(@Valid @RequestBody Board board) {
		return ResponseEntity.status(HttpStatus.CREATED).body(boardInputPort.createdBoard(board));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Board> put(@PathVariable Long id, @Valid @RequestBody Board board) {
	    return ResponseEntity.ok(boardInputPort.updateBoard(id, board));
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete (@PathVariable Long id) {
		boardInputPort.deleteBoard(id);
	}
	
	@GetMapping("/task-counts/{boardId}")
	public ResponseEntity<Long> countTasks(@PathVariable Long boardId){
        Optional<Long> taskCount = boardInputPort.countTasks(boardId);
        return ResponseEntity.of(taskCount);
	}
	
	@GetMapping("/overdue")
	public ResponseEntity<List<Board>> findOverdueBoards(){
		return ResponseEntity.ok(boardInputPort.getBoadsWithOverdueTasks());
	}
	
	@GetMapping("/status/{status}")
	public ResponseEntity<List<Board>> getByStatus(@PathVariable String status) {
		    return ResponseEntity.ok(boardInputPort.getByStatus(status));
		
	}
	
	 @PostMapping("/{boardId}/finalize")
	    public ResponseEntity<String> finalizedBoard(@PathVariable Long boardId) {
            boardInputPort.finalizedBoard(boardId);
            return ResponseEntity.ok("Board " + boardId + " completed successfully");
	    }

	
}
