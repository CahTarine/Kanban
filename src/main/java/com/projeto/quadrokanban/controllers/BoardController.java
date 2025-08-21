package com.projeto.quadrokanban.controllers;

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

import com.projeto.quadrokanban.models.Board;
import com.projeto.quadrokanban.repositories.BoardRepository;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/board")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BoardController {
	
	@Autowired
	private BoardRepository boardRepository;

	
	
	@GetMapping
	public ResponseEntity<List<Board>> getAll() {
		return ResponseEntity.ok(boardRepository.findAll());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Board> getById(@PathVariable Long id) {
		Optional<Board> board = boardRepository.findById(id);
		return board.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<Board>> getByName(@PathVariable String name){
		return ResponseEntity.ok(boardRepository.findAllByNameContainingIgnoreCase(name));
	}
	
	@PostMapping
	public ResponseEntity<Board> post(@Valid @RequestBody Board board) {
		return ResponseEntity.status(HttpStatus.CREATED).body(boardRepository.save(board));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Board> put(@PathVariable Long id, @Valid @RequestBody Board board) {
	    return boardRepository.findById(id)
	            .map(existingBoard -> {
	                board.setId(id); 
	                Board updatedBoard = boardRepository.save(board);
	                return ResponseEntity.ok(updatedBoard);
	            })
	            .orElse(ResponseEntity.notFound().build());
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete (@PathVariable Long id) {
		Optional<Board> board = boardRepository.findById(id);
		
		if(board.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		boardRepository.deleteById(id);
	}
	
}
