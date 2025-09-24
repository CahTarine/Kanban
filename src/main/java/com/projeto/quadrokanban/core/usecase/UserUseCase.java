package com.projeto.quadrokanban.core.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.projeto.quadrokanban.core.domain.model.User;
import com.projeto.quadrokanban.core.port.input.UserInputPort;
import com.projeto.quadrokanban.core.port.output.UserOutputPort;

@Service
public class UserUseCase implements UserInputPort{
	
	private final UserOutputPort userOutputPort;
	
	public UserUseCase(UserOutputPort userOutputPort) {
		this.userOutputPort = userOutputPort;
	}

	public List<User> getAllUsers(){
		return userOutputPort.findAllUsers();
	}
	
	public Optional<User> getUserById(Long id){
		return userOutputPort.findUserById(id);
	}
	
	public List<User> getUserByName(String name){
		return userOutputPort.findUserByName(name);
	}
	
	public User createdUser(User user) {
		return userOutputPort.savedUser(user);
	}
	
	
	public void deleteUser(Long id) {
		userOutputPort.deleteUserById(id);
	}

	public User updateUser(Long id, User user) {
		user.setId(id);
		return userOutputPort.savedUser(user);
	}

}
