package com.projeto.quadrokanban.adapter.output;

import com.projeto.quadrokanban.adapter.output.entity.BoardEntity;
import com.projeto.quadrokanban.adapter.output.mapper.BoardMapper;
import com.projeto.quadrokanban.adapter.output.repository.BoardRepository;
import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.enums.BoardStatus;
import com.projeto.quadrokanban.factory.BoardFactoryBot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class BoardRepositoryTest {

    @Mock
    JdbcTemplate jdbc;

    @Mock
    BoardMapper mapper;

    @InjectMocks
    BoardRepository repo;

    @AfterEach
    public void tearDown() {
        clearInvocations(jdbc, mapper);
    }

    @Test
    void findAllSuccess(){
        Board boardDomain = BoardFactoryBot.createdBoard();
        BoardEntity entity = mock(BoardEntity.class);
        List<BoardEntity> boardEntity = List.of(entity);
        String sql = "SELECT * FROM board.get_all_boards()";

        when(jdbc.query(eq(sql), any(RowMapper.class))).thenReturn(boardEntity);
        when(mapper.toDomain(entity)).thenReturn(boardDomain);

        List<Board> result = repo.findAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(boardDomain.getName(), result.get(0).getName());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findAllSuccess_ReturnsEmptyList(){
        List<BoardEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM board.get_all_boards()";

        when(jdbc.query(eq(sql), any(RowMapper.class))).thenReturn(emptyList);

        List<Board> result = repo.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void findAllError_OnDatabaseFailure(){
        String sql = "SELECT * FROM board.get_all_boards()";

        when(jdbc.query(eq(sql), any(RowMapper.class)))
                .thenThrow(new DataAccessException("Database connection error") {});

        assertThrows(DataAccessException.class, () -> repo.findAll());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void findByIdSuccess(){
        Long id = 1L;
        Board boardDomain = BoardFactoryBot.createdBoard();
        BoardEntity entity = mock(BoardEntity.class);
        List<BoardEntity> boardEntity = List.of(entity);
        String sql = "SELECT * FROM board.get_board_by_id(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(id))).thenReturn(boardEntity);
        when(mapper.toDomain(entity)).thenReturn(boardDomain);

        Optional<Board> result = repo.findById(id);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(boardDomain.getName(), result.get().getName());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(id));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByIdError_IdNotFound(){
        Long id = 100L;
        List<BoardEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM board.get_board_by_id(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(id))).thenReturn(emptyList);

        Optional<Board> result = repo.findById(id);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(id));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);

    }

    @Test
    void findByIdError_OnDatabaseFailure(){
        Long id = 1L;
        List<BoardEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM board.get_board_by_id(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(id)))
                .thenThrow(new DataAccessException("Database connection error") {});

        assertThrows(DataAccessException.class, () -> repo.findById(id));

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(id));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByNameSuccess(){
        String name = "Test";
        String searchName = "%" + name + "%";
        Board boardDomain = BoardFactoryBot.createdBoard();
        BoardEntity entity = mock(BoardEntity.class);
        List<BoardEntity> boardEntity = List.of(entity);
        String sql = "SELECT * FROM board.get_board_by_name(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(searchName))).thenReturn(boardEntity);
        when(mapper.toDomain(entity)).thenReturn(boardDomain);

        List<Board> result = repo.findAllByNameContainingIgnoreCase(name);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(boardDomain.getName(), result.get(0).getName());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(searchName));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByNameSuccess_ReturnsEmptyList(){
        String name = "Test";
        String searchName = "%" + name + "%";
        List<BoardEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM board.get_board_by_name(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(searchName))).thenReturn(emptyList);

        List<Board> result = repo.findAllByNameContainingIgnoreCase(name);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(searchName));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByNameError_OnDatabaseFailure(){
        String name = "Test";
        String searchName = "%" + name + "%";
        String sql = "SELECT * FROM board.get_board_by_name(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(searchName)))
                .thenThrow(new DataAccessException("Database connection error") {});

        assertThrows(DataAccessException.class, () -> repo.findAllByNameContainingIgnoreCase(name));

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(searchName));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }
//*************************************************************************************************
    @Test
    // significa que não trata exceções, e delega a responsabilidade para o metodo que o chama.
    void saveInsertSuccess() throws SQLException  {
        Long newId = 7L;
        Board boardDomain = BoardFactoryBot.createdBoard();
        boardDomain.setId(null); // Simula um novo Board (Insert)
        BoardEntity entity = new BoardEntity();
        entity.setName(boardDomain.getName());
        String sql = "{? = call board.upsert_board(?, ?)}";

        when(mapper.toEntity(boardDomain)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(boardDomain);

        // Captura o callback
        ArgumentCaptor<CallableStatementCallback> callbackCaptor =
                ArgumentCaptor.forClass(CallableStatementCallback.class);

        // Mocka o CallableStatement que será executado dentro do callback
        CallableStatement mockStatement = mock(CallableStatement.class);
        // Configura o mock para retornar o novo ID quando o cs.getLong(1) for chamado
        when(mockStatement.getLong(1)).thenReturn(newId);

        repo.save(boardDomain);

        verify(jdbc, times(1)).execute(eq(sql), callbackCaptor.capture());

        // Executa a lógica interna do callback capturado
        callbackCaptor.getValue().doInCallableStatement(mockStatement);
        // 1. Verifica se o callback registrou o parâmetro de retorno
        verify(mockStatement, times(1)).registerOutParameter(1, Types.BIGINT);
        // 2. Verifica se o ID foi setado como NULL (Insert)
        verify(mockStatement, times(1)).setNull(2, Types.BIGINT);
        // 3. Verifica se o nome foi setado
        verify(mockStatement, times(1)).setString(3, boardDomain.getName());
        // 4. Verifica se o execute foi chamado
        verify(mockStatement, times(1)).execute();

        // 5. Verifica se o novo ID foi setado na entidade para retorno
        assertEquals(newId, entity.getId());

        verify(mapper, times(1)).toEntity(boardDomain);
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void saveUpdateSuccess() throws SQLException {
        Long existingId = 7L;
        String sql = "{? = call board.upsert_board(?, ?)}";
        Board boardDomain = BoardFactoryBot.createdBoard();
        boardDomain.setId(existingId);

        BoardEntity entity = new BoardEntity();
        entity.setId(existingId);
        entity.setName(boardDomain.getName());

        when(mapper.toEntity(boardDomain)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(boardDomain);

        ArgumentCaptor<CallableStatementCallback> callbackCaptor =
                ArgumentCaptor.forClass(CallableStatementCallback.class);

        // Mockamos o CallableStatement
        CallableStatement mockStatement = mock(CallableStatement.class);

        repo.save(boardDomain);

        verify(jdbc, times(1)).execute(eq(sql), callbackCaptor.capture());

        // Executa a lógica interna do callback capturado
        callbackCaptor.getValue().doInCallableStatement(mockStatement);

        // 1. Verifica se o callback registrou o parâmetro de retorno
        verify(mockStatement, times(1)).registerOutParameter(1, Types.BIGINT);
        // 2. Verifica se o ID foi setado com o valor (Update)
        verify(mockStatement, times(1)).setLong(2, existingId);
        // 3. Verifica se o nome foi setado
        verify(mockStatement, times(1)).setString(3, boardDomain.getName());
        // 4. Verifica se o execute foi chamado
        verify(mockStatement, times(1)).execute();

        // Verifica que não houve chamadas para obter o ID de retorno, pois é um update
        verify(mockStatement, never()).getLong(anyInt());

        verify(mapper, times(1)).toEntity(boardDomain);
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }
    //*************************************************************************************************
    @Test
    void deleteSuccess()  throws SQLException {
        Long id = 1L;
        String sql = "call pr_delete_board(?)";

       repo.deleteById(id);

        ArgumentCaptor<CallableStatementCallback> callbackCaptor =
                ArgumentCaptor.forClass(CallableStatementCallback.class);

        CallableStatement mockStatement = mock(CallableStatement.class);

        verify(jdbc, times(1)).execute(eq(sql), callbackCaptor.capture());
        // 3. Executa a lógica interna do callback capturado
        callbackCaptor.getValue().doInCallableStatement(mockStatement);

        // 4. VERIFICAÇÃO: Garante que o ID foi setado corretamente
        verify(mockStatement, times(1)).setLong(1, id);
        verify(mockStatement, times(1)).execute();
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void deleteError_OnDatabaseFailure(){
        Long id = 1L;
        String sql = "call pr_delete_board(?)";

        doThrow(new DataIntegrityViolationException("Cannot delete record"))
                .when(jdbc).execute(eq(sql), any(CallableStatementCallback.class));

        assertThrows(DataAccessException.class, () -> repo.deleteById(id));

        verify(jdbc, times(1)).execute(eq(sql), any(CallableStatementCallback.class));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void countTasksByBoardSuccess(){
        Long id = 1L;
        Long expectedCount = 5L;
        String sql = "SELECT * FROM board.count_tasks_by_board(?)";

        when(jdbc.queryForObject(eq(sql), eq(Long.class), eq(id))).thenReturn(expectedCount);

        Optional<Long> result = repo.countTasksByBoard(id);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(expectedCount, result.get());

        verify(jdbc, times(1)).queryForObject(eq(sql), eq(Long.class), eq(id));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void countTasksByBoardSuccess_OnEmptyResult(){
        Long id = 1L;
        String sql = "SELECT * FROM board.count_tasks_by_board(?)";

        when(jdbc.queryForObject(eq(sql), eq(Long.class), eq(id)))
                .thenThrow(new EmptyResultDataAccessException(1));

        Optional<Long> result = repo.countTasksByBoard(id);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(0L, result.get());

        verify(jdbc, times(1)).queryForObject(eq(sql), eq(Long.class), eq(id));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void countTasksByBoardError_OnDatabaseFailure(){
        Long id = 1L;
        String sql = "SELECT * FROM board.count_tasks_by_board(?)";

        when(jdbc.queryForObject(eq(sql), eq(Long.class), eq(id)))
                .thenThrow(new DataAccessException("Database connection error") {});

        assertThrows(DataAccessException.class, () -> repo.countTasksByBoard(id));

        verify(jdbc, times(1)).queryForObject(eq(sql), eq(Long.class), eq(id));
        verifyNoMoreInteractions(jdbc);

    }

    @Test
    void findByOverdueTasksSuccess(){
        Board boardDomain = BoardFactoryBot.createdBoard();
        BoardEntity entity = mock(BoardEntity.class);
        List<BoardEntity> boardEntity = List.of(entity);
        String sql = "SELECT * FROM board.get_boards_with_overdue_tasks()";

        when(jdbc.query(eq(sql), any(RowMapper.class))).thenReturn(boardEntity);
        when(mapper.toDomain(entity)).thenReturn(boardDomain);

        List<Board> result = repo.findBoadsWithOverdueTasks();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(boardDomain.getName(), result.get(0).getName());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByOverdueTasksSuccess_ReturnsEmptyList(){
        List<BoardEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM board.get_boards_with_overdue_tasks()";

        when(jdbc.query(eq(sql), any(RowMapper.class))).thenReturn(emptyList);

        List<Board> result = repo.findBoadsWithOverdueTasks();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByOverdueTasksError_OnDatabaseFailure(){
        String sql = "SELECT * FROM board.get_boards_with_overdue_tasks()";

        when(jdbc.query(eq(sql), any(RowMapper.class)))
                .thenThrow(new DataAccessException("Database connection error") {});

        assertThrows(DataAccessException.class, () -> repo.findBoadsWithOverdueTasks());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void findByStatusSuccess(){
        BoardStatus status = BoardStatus.ACTIVE;
        String searchStatus = status.name();
        Board boardDomain = BoardFactoryBot.createdBoard();
        BoardEntity entity = mock(BoardEntity.class);
        List<BoardEntity> boardEntity = List.of(entity);
        String sql = "SELECT * FROM board.get_board_by_status(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(searchStatus))).thenReturn(boardEntity);
        when(mapper.toDomain(entity)).thenReturn(boardDomain);

        List<Board> result = repo.findByStatus(status);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(boardDomain.getStatus(), result.get(0).getStatus());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class),  eq(searchStatus));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByStatusSuccess_ReturnsEmptyList(){
        BoardStatus status = BoardStatus.ACTIVE;
        String searchStatus = status.name();
        List<BoardEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM board.get_board_by_status(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(searchStatus))).thenReturn(emptyList);

        List<Board> result = repo.findByStatus(status);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class),  eq(searchStatus));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByStatusError_OnDatabaseFailure(){
        BoardStatus status = BoardStatus.ACTIVE;
        String searchStatus = status.name();
        String sql = "SELECT * FROM board.get_board_by_status(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(searchStatus)))
                .thenThrow(new DataAccessException("Database connection error") {});

        assertThrows(DataAccessException.class, () -> repo.findByStatus(status));

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(searchStatus));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void areAllTasksDoneSuccess(){
        Long id = 1L;
        Boolean expectedBoolean = Boolean.TRUE;
        String sql = "SELECT board.check_if_board_is_complete(?)";

        when(jdbc.queryForObject(eq(sql), eq(Boolean.class), eq(id))).thenReturn(expectedBoolean);

        Boolean result = repo.areAllTasksDone(id);

        assertNotNull(result);
        assertEquals(expectedBoolean, result);

        verify(jdbc, times(1)).queryForObject(eq(sql), eq(Boolean.class), eq(id));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void areAllTasksDoneError_OnDatabaseFailure(){
        Long id = 1L;
        String sql = "SELECT board.check_if_board_is_complete(?)";

        when(jdbc.queryForObject(eq(sql), eq(Boolean.class), eq(id)))
                .thenThrow(new DataAccessException("Database connection error") {});

        assertThrows(DataAccessException.class, () -> repo.areAllTasksDone(id));

        verify(jdbc, times(1)).queryForObject(eq(sql), eq(Boolean.class), eq(id));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void updateBoardStatusSuccess(){
        Long id = 1L;
        BoardStatus status = BoardStatus.ACTIVE;
        String searchStatus = status.name();
        String sql = "call update_board_status(?, ?)";

        repo.updateBoardStatus(id, status);

        verify(jdbc, times(1)).update(eq(sql), eq(id), eq(searchStatus));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void updateBoardStatusError_OnDatabaseFailure(){
        Long id = 1L;
        BoardStatus status = BoardStatus.ACTIVE;
        String searchStatus = status.name();
        String sql = "call update_board_status(?, ?)";

        when(jdbc.update(eq(sql), eq(id), eq(searchStatus)))
                .thenThrow(new DataAccessException("Database connection error") {});

        assertThrows(DataAccessException.class, () -> repo.updateBoardStatus(id, status));

        verify(jdbc, times(1)).update(eq(sql), eq(id), eq(searchStatus));
        verifyNoMoreInteractions(jdbc);
    }
}
