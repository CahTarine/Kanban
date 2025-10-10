package com.projeto.quadrokanban.adapter.output;

import com.projeto.quadrokanban.adapter.output.entity.BoardEntity;
import com.projeto.quadrokanban.adapter.output.entity.TaskEntity;
import com.projeto.quadrokanban.adapter.output.mapper.TaskMapper;
import com.projeto.quadrokanban.adapter.output.repository.TaskRepository;
import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.enums.TaskStatus;
import com.projeto.quadrokanban.core.port.output.TaskOutputPort;
import com.projeto.quadrokanban.factory.BoardFactoryBot;
import com.projeto.quadrokanban.factory.TaskFactoryBot;
import org.junit.jupiter.api.AfterEach;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class TaskRepositoryTest {

    @Mock
    JdbcTemplate jdbc;

    @Mock
    TaskMapper mapper;


    @InjectMocks
    TaskRepository repo;

    @AfterEach
    public void tearDown() {
        clearInvocations(jdbc, mapper);
    }

    @Test
    void findAllSuccess(){
        Task domainTask = TaskFactoryBot.createdTask();

        TaskEntity entityTask = mock(TaskEntity.class); // simula o que o BeanPropertyRowMapper faria no repository real.
        List<TaskEntity> listEntityTask = List.of(entityTask);

        String sql = "SELECT * FROM get_all_tasks()";

        when(jdbc.query(eq(sql), any(RowMapper.class))).thenReturn(listEntityTask);
        when(mapper.toDomain(entityTask)).thenReturn(domainTask);

        List<Task> result = repo.findAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(domainTask.getTitle(), result.get(0).getTitle());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, times(1)).toDomain(entityTask);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findAlSuccess_ReturnEmptyList(){
        String sql = "SELECT * FROM get_all_tasks()";

//        Simula que o JDBC retorna uma lista vazia de TaskEntity
        List<TaskEntity> emptyList = Collections.emptyList();

        when(jdbc.query(eq(sql), any(RowMapper.class))).thenReturn(emptyList);

        List<Task> result = repo.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findAllError_OnDatabaseFailure(){
        String sql = "SELECT * FROM get_all_tasks()";

        when(jdbc.query(eq(sql), any(RowMapper.class)))
                .thenThrow(new DataAccessException("Database connection error") {});

        assertThrows(DataAccessException.class, () -> repo.findAll());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByIdSuccess(){
        Long id = 1L;
        Task existingTask = TaskFactoryBot.createdTask();
        TaskEntity mockEntity = mock(TaskEntity.class); // Cria um objeto falso e funcional pra representar o dado vindo do BD.
        List<TaskEntity> entityTask = List.of(mockEntity); // Aqui é List porque o metodo .query do jdbc sempre retorna uma lista.

        String sql = "SELECT * FROM get_task_by_id(?)";

        when(jdbc.query(
                eq(sql),
                any(RowMapper.class),
                eq(id)
        )).thenReturn(entityTask);
        when(mapper.toDomain(mockEntity)).thenReturn(existingTask);

        Optional<Task> result = repo.findById(id);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(existingTask.getTitle(), result.get().getTitle());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(id));
        verify(mapper, times(1)).toDomain(mockEntity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByIdError_IdNotFound(){
        Long id = 100L;
        List<TaskEntity> emptyList = Collections.emptyList();

        String sql = "SELECT * FROM get_task_by_id(?)";

        when(jdbc.query(
                eq(sql),
                any(RowMapper.class),
                eq(id)
        )).thenReturn(emptyList);
//        Não precisamos do when pro mapper porque ele nao vai ser chamado.

        Optional<Task> result = repo.findById(id);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(id));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByIdError_OnDatabaseFailure(){
        Long id = 1L;
        String sql = "SELECT * FROM get_task_by_id(?)";

        when(jdbc.query(
                eq(sql),
                any(RowMapper.class),
                eq(id)
        )).thenThrow(new DataAccessException("Database connection error") {});

        assertThrows(DataAccessException.class, () -> repo.findById(id));

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(id));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByTitleSuccess(){
        String title = "Test";
        String searchTitle = "%" + title + "%";
        Task taskDomain = TaskFactoryBot.createdTask();
        TaskEntity mockEntity = mock(TaskEntity.class);
        List<TaskEntity> taskEntity = List.of(mockEntity);

        String sql = "SELECT * FROM get_task_by_title(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(searchTitle))).thenReturn(taskEntity);
        when(mapper.toDomain(mockEntity)).thenReturn(taskDomain);

        List<Task> result = repo.findAllByTitleContainingIgnoreCase(title);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(taskDomain.getTitle(), result.get(0).getTitle());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(searchTitle));
        verify(mapper, times(1)).toDomain(mockEntity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByTitleSuccess_ReturnsEmptyList(){
        String title = "Test";
        String searchTitle = "%" + title + "%";
        List<TaskEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM get_task_by_title(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(searchTitle))).thenReturn(emptyList);

        List<Task> result = repo.findAllByTitleContainingIgnoreCase(title);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(searchTitle));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByTitleError_OnDatabaseFailure(){
        String title = "Test";
        String searchTitle = "%" + title + "%";
        String sql = "SELECT * FROM get_task_by_title(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(searchTitle)))
                .thenThrow(new DataAccessException("Database connection error") {});

        assertThrows(DataAccessException.class, () -> repo.findAllByTitleContainingIgnoreCase(title));

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(searchTitle));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

//    @Test
//    void saveInsertSuccess() throws SQLException {
//        Long newId = 7L;
//        Task taskDomain = TaskFactoryBot.createdTask();
//        taskDomain.setId(null); // Simula um novo Board (Insert)
//        TaskEntity entity = new TaskEntity();
//        entity.setTitle(taskDomain.getTitle());
//        String sql = "{? = call upsert_task(?, ?, ?, ?, ?, ?, ?)}";
//
//        when(mapper.toEntity(taskDomain)).thenReturn(entity);
//        when(mapper.toDomain(entity)).thenReturn(taskDomain);
//
//        ArgumentCaptor<CallableStatementCallback> callbackCaptor =
//                ArgumentCaptor.forClass(CallableStatementCallback.class);
//
//        CallableStatement mockStatement = mock(CallableStatement.class);
//        when(mockStatement.getLong(1)).thenReturn(newId);
//
//        repo.save(taskDomain);
//
//        verify(jdbc, times(1)).execute(eq(sql), callbackCaptor.capture());
//        callbackCaptor.getValue().doInCallableStatement(mockStatement);
//        verify(mockStatement, times(1)).registerOutParameter(1, Types.BIGINT);
//        verify(mockStatement, times(1)).setNull(2, Types.BIGINT);
//        verify(mockStatement, times(1)).setString(3, taskDomain.getTitle());
//        verify(mockStatement, times(1)).execute();
//
//        assertEquals(newId, entity.getId());
//
//        verify(mapper, times(1)).toEntity(taskDomain);
//        verify(mapper, times(1)).toDomain(entity);
//        verifyNoMoreInteractions(jdbc, mapper);
//    }

    @Test
    void deleteSuccess() throws SQLException {
        Long id = 1L;
        String sql = "call pr_delete_task(?)";

        repo.deleteById(id);

        ArgumentCaptor<CallableStatementCallback> callbackCaptor =
                ArgumentCaptor.forClass(CallableStatementCallback.class);
//        ArgumentCaptor captura CallableStatementCallback que foi passado como argumento
//        para o metodo mockado jdbcTemplate.execute(), permitindo que a gente examine e execute posteriormente para testar o código

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
        String sql = "call pr_delete_task(?)";

//        programa o objeto mock jdbc para lançar uma exceção específica quando o metodo for chamado.
        doThrow(new DataIntegrityViolationException("Cannot delete record"))
                .when(jdbc).execute(eq(sql), any(CallableStatementCallback.class));

//        Verifica se o metodo lançou mesmo a exceção esperada.
        assertThrows(DataAccessException.class, () -> repo.deleteById(id));

        verify(jdbc, times(1)).execute(eq(sql), any(CallableStatementCallback.class));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void findByStatusSuccess(){
        TaskStatus status = TaskStatus.TODO;
        String statusArgument = status.name();
        Task taskDomain = TaskFactoryBot.createdTask();
        TaskEntity entity = mock(TaskEntity.class);
        List<TaskEntity> taskEntity = List.of(entity);
        String sql = "SELECT * FROM get_task_by_status(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(statusArgument))).thenReturn(taskEntity);
        when(mapper.toDomain(entity)).thenReturn(taskDomain);

        List<Task> result = repo.findAllByStatus(status);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(taskDomain.getStatus(), result.get(0).getStatus());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(statusArgument));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByStatusSuccess_ReturnsEmptyList(){
        TaskStatus status = TaskStatus.TODO;
        String statusArgument = status.name();
        List<TaskEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM get_task_by_status(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(statusArgument))).thenReturn(emptyList);

        List<Task> result = repo.findAllByStatus(status);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(statusArgument));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByStatusError_OnDatabaseFailure(){
        TaskStatus status = TaskStatus.TODO;
        String statusArgument = status.name();
        String sql = "SELECT * FROM get_task_by_status(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(statusArgument)))
                .thenThrow(new DataAccessException("Database connection error") {});

        assertThrows(DataAccessException.class, () -> repo.findAllByStatus(status));

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(statusArgument));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByBoardSuccess(){
        Long boardId = 1L;
        Task taskDomain = TaskFactoryBot.createdTask();
        TaskEntity entity = mock(TaskEntity.class);
        List<TaskEntity> taskEntity = List.of(entity);
        String sql = "SELECT * FROM get_task_by_board(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(boardId))).thenReturn(taskEntity);
        when(mapper.toDomain(entity)).thenReturn(taskDomain);

        List<Task> result = repo.findAllTaskByBoard(boardId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(taskDomain.getBoard(), result.get(0).getBoard());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(boardId));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByBoardSuccess_ReturnsEmptyList(){
        Long boardId = 1L;
        List<TaskEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM get_task_by_board(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(boardId))).thenReturn(emptyList);

        List<Task> result = repo.findAllTaskByBoard(boardId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(boardId));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByBoardError_OnDatabaseFailure(){
        Long boardId = 1L;
        String sql = "SELECT * FROM get_task_by_board(?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(boardId)))
                .thenThrow(new DataAccessException("Database connection error!") {});

        assertThrows(DataAccessException.class, () -> repo.findAllTaskByBoard(boardId));

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(boardId));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByBoardAndStatusSuccess(){
        Long boardId = 1L;
        TaskStatus status = TaskStatus.TODO;
        String statusArgument = status.name();
        Task taskDomain = TaskFactoryBot.createdTask();
        TaskEntity entity = mock(TaskEntity.class);
        List<TaskEntity> taskEntity = List.of(entity);
        String sql = "SELECT * FROM get_task_by_board_and_status(?, ?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(boardId), eq(statusArgument))).thenReturn(taskEntity);
        when(mapper.toDomain(entity)).thenReturn(taskDomain);

        List<Task> result = repo.findByBoardAndStatus(boardId, status);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(taskDomain.getBoard(), result.get(0).getBoard());
        assertEquals(taskDomain.getStatus(), result.get(0).getStatus());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(boardId), eq(statusArgument));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByBoardAndStatusSuccess_ReturnsEmptyList(){
        Long boardId = 1L;
        TaskStatus status = TaskStatus.TODO;
        String statusArgument = status.name();
        List<TaskEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM get_task_by_board_and_status(?, ?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(boardId), eq(statusArgument))).thenReturn(emptyList);

        List<Task> result = repo.findByBoardAndStatus(boardId, status);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(boardId), eq(statusArgument));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByBoardAndStatusError_OnDatabaseFailure(){
        Long boardId = 1L;
        TaskStatus status = TaskStatus.TODO;
        String statusArgument = status.name();
        String sql = "SELECT * FROM get_task_by_board_and_status(?, ?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(boardId), eq(statusArgument)))
                .thenThrow(new DataAccessException("Database connection error!") {});

        assertThrows(DataAccessException.class, () -> repo.findByBoardAndStatus(boardId, status));

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class), eq(boardId), eq(statusArgument));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findLastTaskSuccess(){
        Task taskDomain = TaskFactoryBot.createdTask();
        TaskEntity entity = mock(TaskEntity.class);
        List<TaskEntity> taskEntity = List.of(entity);
        String sql = "SELECT * FROM get_last_created_task()";

        when(jdbc.query(eq(sql), any(RowMapper.class))).thenReturn(taskEntity);
        when(mapper.toDomain(entity)).thenReturn(taskDomain);

        Optional<Task> result = repo.findLastCreatedTask();

        assertNotNull(result);
        assertFalse(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findLastTaskSuccess_ReturnsEmpty(){
        List<TaskEntity> emptyList = Collections.emptyList();
        String sql = "SELECT * FROM get_last_created_task()";

        when(jdbc.query(eq(sql), any(RowMapper.class))).thenReturn(emptyList);

        Optional<Task> result = repo.findLastCreatedTask();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findLastTaskError_OnDatabseFailure(){
        String sql = "SELECT * FROM get_last_created_task()";

        when(jdbc.query(eq(sql), any(RowMapper.class)))
                .thenThrow(new DataAccessException("Database connection error!") {});

        assertThrows(DataAccessException.class, () -> repo.findLastCreatedTask());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByDueDateSuccess(){
        LocalDate dueDate = LocalDate.of(2025, 10, 9);

        LocalDateTime startOfDay = dueDate.atStartOfDay();
        LocalDateTime endOfDay = dueDate.atTime(23, 59, 59);

        Task taskDomain = TaskFactoryBot.createdTask();
        TaskEntity entity = mock(TaskEntity.class);
        List<TaskEntity> taskEntity = List.of(entity);

        taskDomain.setDueDate(startOfDay);
        String sql = "SELECT * FROM get_task_by_due_date(?, ?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(startOfDay), eq(endOfDay))).thenReturn(taskEntity);
        when(mapper.toDomain(entity)).thenReturn(taskDomain);

        List<Task> result = repo.findByDueDate(dueDate);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(taskDomain.getDueDate(), result.get(0).getDueDate());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class),eq(startOfDay), eq(endOfDay));
        verify(mapper, times(1)).toDomain(entity);
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByDueDateSuccess_ReturnsEmptyList(){
        LocalDate dueDate = LocalDate.of(2025, 10, 9);

        LocalDateTime startOfDay = dueDate.atStartOfDay();
        LocalDateTime endOfDay = dueDate.atTime(23, 59, 59);

        List<TaskEntity> emptyList = Collections.emptyList();

        String sql = "SELECT * FROM get_task_by_due_date(?, ?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(startOfDay), eq(endOfDay))).thenReturn(emptyList);

        List<Task> result = repo.findByDueDate(dueDate);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class),eq(startOfDay), eq(endOfDay));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void findByDueDateError_OnDatabaseFailure(){
        LocalDate dueDate = LocalDate.of(2025, 10, 9);

        LocalDateTime startOfDay = dueDate.atStartOfDay();
        LocalDateTime endOfDay = dueDate.atTime(23, 59, 59);

        String sql = "SELECT * FROM get_task_by_due_date(?, ?)";

        when(jdbc.query(eq(sql), any(RowMapper.class), eq(startOfDay), eq(endOfDay)))
                .thenThrow(new DataAccessException("Database connection error!") {});

        assertThrows(DataAccessException.class, () -> repo.findByDueDate(dueDate));

        verify(jdbc, times(1)).query(eq(sql), any(RowMapper.class),eq(startOfDay), eq(endOfDay));
        verify(mapper, never()).toDomain(any());
        verifyNoMoreInteractions(jdbc, mapper);
    }

    @Test
    void countDoingTasksByUserIdSuccess(){
        Long userId = 1L;
        Long expectedCount = 5L;
        String sql = "SELECT * FROM count_doing_tasks_by_user(?)";

        when(jdbc.queryForObject(eq(sql), eq(Long.class), eq(userId))).thenReturn(expectedCount);

        Long result = repo.countDoingTasksByUserId(userId);

        assertNotNull(result);
        assertEquals(expectedCount, result);

        verify(jdbc, times(1)).queryForObject(eq(sql), eq(Long.class), eq(userId));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void countDoingTasksByUserIdSuccess_WhenNoTasks(){
        Long userId = 1L;
        Long expectedCount = 0L;
        String sql = "SELECT * FROM count_doing_tasks_by_user(?)";

        when(jdbc.queryForObject(eq(sql), eq(Long.class), eq(userId))).thenReturn(expectedCount);

        Long result = repo.countDoingTasksByUserId(userId);

        assertEquals(0L, result);

        verify(jdbc, times(1)).queryForObject(eq(sql), eq(Long.class), eq(userId));
        verifyNoMoreInteractions(jdbc);
    }

    @Test
    void countDoingTasksByUserIdError_OnDatabaseFailure(){
        Long userId = 1L;
        String sql = "SELECT * FROM count_doing_tasks_by_user(?)";

        when(jdbc.queryForObject(eq(sql), eq(Long.class), eq(userId)))
                .thenThrow(new DataAccessException("Database connection error!") {});

        assertThrows(DataAccessException.class, () -> repo.countDoingTasksByUserId(userId));

        verify(jdbc, times(1)).queryForObject(eq(sql), eq(Long.class), eq(userId));
        verifyNoMoreInteractions(jdbc);
    }
}
