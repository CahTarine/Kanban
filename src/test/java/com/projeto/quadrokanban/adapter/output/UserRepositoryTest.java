package com.projeto.quadrokanban.adapter.output;

import com.projeto.quadrokanban.adapter.output.entity.UserEntity;
import com.projeto.quadrokanban.adapter.output.mapper.UserMapper;
import com.projeto.quadrokanban.adapter.output.repository.UserRepository;
import com.projeto.quadrokanban.core.domain.model.User;
import com.projeto.quadrokanban.factory.UserFactoryBot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserRepositoryTest {

    @Mock
    JdbcTemplate jdbc;

    @Mock
    UserMapper mapper;

    @InjectMocks
    UserRepository repo;

    @Test
    void findAllSuccess(){
        User userDomain = UserFactoryBot.createdUser();
        UserEntity entity = mock(UserEntity.class);
        List<UserEntity> userEntity = List.of(entity);
        String sql = "SELECT * FROM users.get_all_users()";

        when(jdbc.query(eq(sql), any(RowMapper.class))).thenReturn(userEntity);
        when(mapper.toDomain(entity)).thenReturn(userDomain);

        List<User> result = repo.findAllUsers();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(userDomain.getName(), result.get(0).getName());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findAllSuccess_ReturnsEmptyList(){
        List<UserEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM users.get_all_users()";

        when(jdbc.query(eq(sql), any(RowMapper.class))).thenReturn(emptyList);

        List<User> result = repo.findAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findAllError_OnDatabaseFailure(){
        String sql = "SELECT * FROM users.get_all_users()";

        when(jdbc.query(eq(sql), any(RowMapper.class)))
                .thenThrow(new DataAccessException("Database connection error.") {});

        assertThrows(DataAccessException.class, () -> repo.findAllUsers());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void findByIdSuccess(){
        Long id = 1L;
        User userDomain = UserFactoryBot.createdUser();
        UserEntity entity = mock(UserEntity.class);
        List<UserEntity> userEntity = List.of(entity);
        String sql = "SELECT * FROM users.get_user_by_id(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(id))).thenReturn(userEntity);
        when(mapper.toDomain(entity)).thenReturn(userDomain);

        Optional<User> result = repo.findUserById(id);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(userDomain.getName(), result.get().getName());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(id));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByIdSuccess_ReturnsEmpty(){
        Long id = 1L;
        List<UserEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM users.get_user_by_id(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(id))).thenReturn(emptyList);

        Optional<User> result = repo.findUserById(id);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(id));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByIdError_OnDatabaseFailure(){
        Long id = 1L;
        String sql = "SELECT * FROM users.get_user_by_id(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(id)))
                .thenThrow(new DataAccessException("Database connection error.") {});

        assertThrows(DataAccessException.class, () -> repo.findUserById(id));

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(id));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void findByNameSuccess(){
        User userDomain = UserFactoryBot.createdUser();
        String name = userDomain.getName();
        String searchName = "%" + name + "%";
        UserEntity entity = mock(UserEntity.class);
        List<UserEntity> userEntity = List.of(entity);
        String sql = "SELECT * FROM users.get_user_by_name(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(searchName))).thenReturn(userEntity);
        when(mapper.toDomain(entity)).thenReturn(userDomain);

        List<User> result = repo.findUserByName(name);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(userDomain.getName(), result.get(0).getName());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(searchName));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByNameSuccess_ReturnsEmptyList(){
        String name = "Ana";
        String searchName = "%" + name + "%";
        List<UserEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM users.get_user_by_name(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(name))).thenReturn(emptyList);

        List<User> result = repo.findUserByName(name);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(searchName));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByNameError_OnDatabaseFailure(){
        String name = "Ana";
        String searchName = "%" + name + "%";
        String sql = "SELECT * FROM users.get_user_by_name(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(searchName)))
                .thenThrow(new DataAccessException("Database connection error.") {});

        assertThrows(DataAccessException.class, () -> repo.findUserByName(name));

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(searchName));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }
//    ************************************************************

//    ************************************************************

    @Test
    void deleteSuccess() throws SQLException {
        Long id = 1L;
        String sql = "call pr_delete_user(?)";

        repo.deleteUserById(id);

        ArgumentCaptor<CallableStatementCallback> callbackCaptor =
                ArgumentCaptor.forClass(CallableStatementCallback.class);

        CallableStatement mockStatement = mock(CallableStatement.class);

        verify(jdbc, times(1)).execute(eq(sql), callbackCaptor.capture());
        callbackCaptor.getValue().doInCallableStatement(mockStatement);
        verify(mockStatement, times(1)).setLong(1, id);
        verify(mockStatement, times(1)).execute();
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void deleteError_OnDatabaseFailure(){
        Long id = 1L;
        String sql = "call pr_delete_user(?)";

        doThrow(new DataIntegrityViolationException("Cannot delete record"))
                .when(jdbc).execute(eq(sql), any(CallableStatementCallback.class));

        assertThrows(DataAccessException.class, () -> repo.deleteUserById(id));

        verify(jdbc, times(1)).execute(eq(sql), any(CallableStatementCallback.class));
        verifyNoMoreInteractions(jdbc);
    }
}
