package com.projeto.quadrokanban.core.port.output;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.model.User;

public interface UserOutputPort {
	
	List<User> findAllUsers();

	Optional<User> findUserById(Long id);
	
	List<User> findUserByName(String name);
	
	User savedUser(User user);
	
	void deleteUserById(Long id);

}
