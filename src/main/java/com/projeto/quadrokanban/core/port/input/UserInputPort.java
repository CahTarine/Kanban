package com.projeto.quadrokanban.core.port.input;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.model.User;

public interface UserInputPort {
	
	List<User> getAllUsers();

	Optional<User> getUserById(Long id);
	
	List<User> getUserByName(String name);
	
	User createdUser(User user);
	
	User updateUser(Long id, User user);
	
	void deleteUser(Long id);
	
	
}
