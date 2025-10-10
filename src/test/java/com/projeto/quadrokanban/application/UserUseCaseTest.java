package com.projeto.quadrokanban.application;

import com.projeto.quadrokanban.core.domain.exception.UserNotFoundException;
import com.projeto.quadrokanban.core.domain.model.User;
import com.projeto.quadrokanban.core.port.output.UserOutputPort;
import com.projeto.quadrokanban.core.usecase.UserUseCase;
import com.projeto.quadrokanban.factory.UserFactoryBot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserUseCaseTest {

    @Mock
    UserOutputPort port;

    @InjectMocks
    UserUseCase useCase;

    @AfterEach
    public void tearDown() {
        clearInvocations(port); // Remove todas as interações e stubs do mock
    }

    @Test
    void getAllSuccess_ReturnsList(){
        List<User> listUser = List.of(
                UserFactoryBot.createdUser(),
                UserFactoryBot.createdUser()
        );

        when(port.findAllUsers()).thenReturn(listUser);

        List<User> result = useCase.getAllUsers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(listUser.size(), result.size());

        verify(port, times(1)).findAllUsers();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getAllSuccess_ReturnsEmptyList() {
        List<User> listUser = List.of();

        when(port.findAllUsers()).thenReturn(listUser);

        List<User> result = useCase.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(port, times(1)).findAllUsers();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByIdSuccess(){
        Long id = 1L;
        User user = UserFactoryBot.createdUser();

        when(port.findUserById(id)).thenReturn(Optional.of(user));

        User result = useCase.getUserById(id);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());

        verify(port, times(1)).findUserById(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByIdError_WhenUserNotFound(){
        Long id = 100L;

        when(port.findUserById(id)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> useCase.getUserById(id));

        verify(port, times(1)).findUserById(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByNameSuccess_ReturnsList(){
        String name = "Ana";
        List<User> listUser = List.of(
                UserFactoryBot.createdUser(),
                UserFactoryBot.createdUser()
        );

        when(port.findUserByName(name)).thenReturn(listUser);

        List<User> result = useCase.getUserByName(name);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(listUser.size(), result.size());

        verify(port, times(1)).findUserByName(name);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByNameSuccess_ReturnsEmptyList(){
        String name = "Ana";
        List<User> listUser = List.of();

        when(port.findUserByName(name)).thenReturn(listUser);

        List<User> result = useCase.getUserByName(name);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(port, times(1)).findUserByName(name);
        verifyNoMoreInteractions(port);
    }

    @Test
    void createUserSuccess(){
        User user = UserFactoryBot.createdUser();

        when(port.savedUser(user)).thenAnswer(invocation -> invocation.getArgument(0));

        User result = useCase.createdUser(user);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());

        verify(port, times(1)).savedUser(user);
        verifyNoMoreInteractions(port);
    }

    @Test
    void deleteUserSuccess(){
        Long id = 1L;
        User user = UserFactoryBot.createdUser();

        when(port.findUserById(id)).thenReturn(Optional.of(user));

        useCase.deleteUser(id);

        verify(port, times(1)).findUserById(id);
        verify(port, times(1)).deleteUserById(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void deleteUserError_WhenUserNotFound(){
        Long id = 100L;
        when(port.findUserById(id)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> useCase.deleteUser(id));

        verify(port, times(1)).findUserById(id);
        verify(port, never()).deleteUserById(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void updateUserSuccess(){
        Long id = 1L;
        User updateUser = UserFactoryBot.createdUser();

        when(port.findUserById(id)).thenReturn(Optional.of(updateUser));
        when(port.savedUser(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = useCase.updateUser(id, updateUser);

        assertNotNull(result);
        assertEquals(updateUser.getName(), result.getName());
        assertEquals(updateUser.getEmail(), result.getEmail());

        verify(port, times(1)).findUserById(id);
        verify(port, times(1)).savedUser(updateUser);
        verifyNoMoreInteractions(port);
    }

    @Test
    void updateUserError_WhenUserNotFound(){
        Long id = 100L;
        User updateUser = UserFactoryBot.createdUser();

        when(port.findUserById(id)).thenThrow(UserNotFoundException.class);

        assertThrows(UserNotFoundException.class, () -> useCase.updateUser(id, updateUser));

        verify(port, times(1)).findUserById(id);
        verify(port, never()).savedUser(updateUser);
        verifyNoMoreInteractions(port);
    }
}
