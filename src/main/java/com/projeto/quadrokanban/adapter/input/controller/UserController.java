package com.projeto.quadrokanban.adapter.input.controller;

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

import com.projeto.quadrokanban.core.domain.model.User;
import com.projeto.quadrokanban.core.port.input.UserInputPort;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {
	
	private UserInputPort userInputPort;
	
	public UserController(UserInputPort userInputPort) {
		super();
		this.userInputPort = userInputPort;
	}

	@GetMapping
	public ResponseEntity<List<User>> getAll(){
		return ResponseEntity.ok(userInputPort.getAllUsers());
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Long id){
		Optional<User> user = userInputPort.getUserById(id);
		return user.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}
	
	@GetMapping("/name/{name}")
	public ResponseEntity<List<User>> getUserByName(@PathVariable String name){
		return ResponseEntity.ok(userInputPort.getUserByName(name));
	}
	
	@PostMapping
	public ResponseEntity<User> post(@Valid @RequestBody User user){
		return ResponseEntity.status(HttpStatus.CREATED).body(userInputPort.createdUser(user));
	}
	
	// *******************
	@PutMapping("/{id}")
	public ResponseEntity<User> put(@Valid @RequestBody User userUpdates, @PathVariable Long id){
             User updatedUser = userInputPort.updateUser(id, userUpdates);
                
            return ResponseEntity.ok(updatedUser);
	}
	
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		Optional<User> user = userInputPort.getUserById(id);
		
		if(user.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		userInputPort.deleteUser(id);
	}
	
	

}
