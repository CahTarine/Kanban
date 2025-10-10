package com.projeto.quadrokanban.core.usecase;

import java.util.List;
import java.util.Optional;

import com.projeto.quadrokanban.core.domain.exception.UserNotFoundException;
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

	public List<User> getAllUsers(){ return userOutputPort.findAllUsers();}
	
	public User getUserById(Long id){
        return userOutputPort.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
	}
	
	public List<User> getUserByName(String name){ return userOutputPort.findUserByName(name);}
	
	public User createdUser(User user) {
		return userOutputPort.savedUser(user);
	}
	
	
	public void deleteUser(Long id) {
        userOutputPort.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
        userOutputPort.deleteUserById(id);
	}

	public User updateUser(Long id, User user) {
        User existingUser = userOutputPort.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found."));
		existingUser.setName(user.getName());
        existingUser.setEmail(user.getEmail());
		return userOutputPort.savedUser(existingUser);
	}

}
