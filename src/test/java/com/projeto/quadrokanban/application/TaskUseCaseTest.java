package com.projeto.quadrokanban.application;

import com.projeto.quadrokanban.core.domain.exception.BoardNotFoundException;
import com.projeto.quadrokanban.core.domain.exception.InvalidDateFormatException;
import com.projeto.quadrokanban.core.domain.exception.InvalidStatusException;
import com.projeto.quadrokanban.core.domain.exception.TaskNotFoundException;
import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.enums.TaskStatus;
import com.projeto.quadrokanban.core.port.output.TaskOutputPort;
import com.projeto.quadrokanban.core.usecase.BoardValidatorService;
import com.projeto.quadrokanban.core.usecase.TaskUseCase;
import com.projeto.quadrokanban.core.usecase.ValidateTaskRules;
import com.projeto.quadrokanban.factory.TaskFactoryBot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
public class TaskUseCaseTest {

    @Mock
    TaskOutputPort port;

    @Mock
    BoardValidatorService boardValidator;

    @Mock
    ValidateTaskRules taskRules;

    @InjectMocks
    TaskUseCase useCase;

    @Test
    void getAllSuccess_ReturnsList(){
        List<Task> listTask = List.of(
                TaskFactoryBot.createdTask(),
                TaskFactoryBot.createdTask()
        );

        when(port.findAll()).thenReturn(listTask);

        List<Task> result = useCase.getAll();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(listTask.size(), result.size());

        verify(port, times(1)).findAll();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getAllSuccess_ReturnsEmptyList(){
        List<Task> emptyList = List.of();

        when(port.findAll()).thenReturn(emptyList);

        List<Task> result = useCase.getAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(port, times(1)).findAll();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByIdSuccess(){
        Task existingTask = TaskFactoryBot.createdTask();

        when(port.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));

        Task result = useCase.getById(existingTask.getId());

        assertNotNull(result);
        assertEquals(existingTask.getId(), result.getId());
        assertEquals(existingTask.getTitle(), result.getTitle());

        verify(port, times(1)).findById(existingTask.getId());
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByIdError_WhenIdNotFound(){
        Long id = 10L;

        when(port.findById(id)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> useCase.getById(id));

        verify(port, times(1)).findById(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByTitleSuccess_ReturnsList(){
        String title = "Test";
        List<Task> listTask = List.of(
                TaskFactoryBot.createdTask(),
                TaskFactoryBot.createdTask()
        );

        when(port.findAllByTitleContainingIgnoreCase(title)).thenReturn(listTask);

        List<Task> result = useCase.getByTitle(title);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(listTask.size(), result.size());

        verify(port, times(1)).findAllByTitleContainingIgnoreCase(title);
        verifyNoMoreInteractions(port);

    }

    @Test
    void getByTitleSuccess_ReturnsEmptyList(){
        String title = "Test";
        List<Task> listTask = List.of();

        when(port.findAllByTitleContainingIgnoreCase(title)).thenReturn(listTask);

        List<Task> result = useCase.getByTitle(title);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(port, times(1)).findAllByTitleContainingIgnoreCase(title);
        verifyNoMoreInteractions(port);

    }

    @Test
    void updateTaskSuccess(){
        Task existingTask = TaskFactoryBot.createdTask();
        Task updateTask = TaskFactoryBot.updatedTask();
        Board board = existingTask.getBoard();

        when(port.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));
        when(boardValidator.validateBoardExists(updateTask.getBoard().getId())).thenReturn(board);
        when(port.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));
//        thenAnswer captura o objeto recebido e o retorna.
//        invocation é um objeto que representa a chamada do metodo mockado.
//        invocation.getArgument(0): metodo que acessa o primeiro argumento que foi passado para a chamada do save, a Task, modificada pelo useCase.
//        A linha diz: Quando o metodo port.save for chamado com qualquer objeto Task, retorne esse mesmo objeto Task que foi passado como argumento.

        Task result = useCase.updateTask(existingTask.getId(), updateTask);

        assertNotNull(result);
        assertEquals("Task Atualizada", result.getTitle());
        assertEquals("Task para testes unitários", result.getDescription());
        assertEquals(TaskStatus.DONE, result.getStatus());
        assertEquals(board, result.getBoard());
        assertEquals(1L, result.getUserId());

        verify(port, times(1)).findById(existingTask.getId());
        verify(taskRules, times(1)).validateTaskRules(updateTask);
        verify(boardValidator, times(1)).validateBoardExists(updateTask.getBoard().getId());
        verify(port, times(1)).save(result);
        verifyNoMoreInteractions(port);

    }

    @Test
    void updateTaskError_WhenTaskNotFound(){
        Task existingTask = TaskFactoryBot.createdTask();
        Task updateTask = TaskFactoryBot.updatedTask();

        when(port.findById(existingTask.getId())).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> useCase.updateTask(existingTask.getId(), updateTask));

        verify(port, times(1)).findById(existingTask.getId());
        verify(port, never()).save(any(Task.class));
        verifyNoMoreInteractions(port);
    }

    @Test
    void deleteTaskSuccess(){
        Task existingTask = TaskFactoryBot.createdTask();

        when(port.findById(existingTask.getId())).thenReturn(Optional.of(existingTask));

        useCase.deleteTask(existingTask.getId());

        verify(port, times(1)).findById(existingTask.getId());
        verify(port, times(1)).deleteById(existingTask.getId());
        verifyNoMoreInteractions(port);
    }

    @Test
    void deleteTaskError_WhenTaskNotFound(){
        Long id = 10L;

        when(port.findById(id)).thenReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> useCase.deleteTask(id));

        verify(port, times(1)).findById(id);
        verify(port, never()).deleteById(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void createTaskSuccess(){
        Task task = TaskFactoryBot.createdTask();
        Board board = task.getBoard();

        when(boardValidator.validateBoardExists(task.getBoard().getId())).thenReturn(board);
        when(port.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Task result = useCase.createTaskWithBoard(task, board.getId());

        assertNotNull(result);
        assertNotNull(result.getBoard());
        assertEquals(board, result.getBoard());
        assertEquals(task.getTitle(), result.getTitle());

        verify(taskRules, times(1)).validateTaskRules(task);
        verify(boardValidator, times(1)).validateBoardExists(task.getBoard().getId());
        verify(port, times(1)).save(result);
        verifyNoMoreInteractions(port);
    }

    @Test
    void createTaskError_WhenBoardNotFound(){
        Task task = TaskFactoryBot.createdTaskWithoutBoard();
        Long boardId = 100L;

        when(boardValidator.validateBoardExists(boardId)).thenThrow(BoardNotFoundException.class);

        assertThrows(BoardNotFoundException.class, () -> useCase.createTaskWithBoard(task, boardId));

        verify(port, never()).save(any(Task.class));
        verifyNoMoreInteractions(port);

    }

    @Test
    void getByStatusSuccess_ReturnsList(){
        String statusString = "TODO";
        TaskStatus statusEnum = TaskStatus.TODO;
        List<Task> listTask = List.of(
                TaskFactoryBot.createdTask(),
                TaskFactoryBot.createdTask()
        );

        when(port.findAllByStatus(statusEnum)).thenReturn(listTask);

        List<Task> result = useCase.getByStatus(statusString);

        assertNotNull(result);
        assertFalse(result.isEmpty()); // verifica se o usecase manda uma lista vazia, mesmo que não seja nula.
        assertEquals(listTask.size(), result.size());

        verify(port, times(1)).findAllByStatus(statusEnum);
        verifyNoMoreInteractions(port); // verifica se nao teve interação desnecessária com outros metodos.
    }

    @Test
    void getByStatusSuccess_ReturnsEmptyList(){
        String statusString = "TODO";
        TaskStatus statusEnum = TaskStatus.TODO;
        List<Task> listTask = List.of();

        when(port.findAllByStatus(statusEnum)).thenReturn(listTask);

        List<Task> result = useCase.getByStatus(statusString);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(port, times(1)).findAllByStatus(statusEnum);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByStatusError_WhenInvalidStatus(){
        String statusString = "invalid_status";

//        não precisamos do when porque o teste falha antes de chegar no mock(port).

        assertThrows(InvalidStatusException.class, () -> useCase.getByStatus(statusString));

        verify(port, never()).findAllByStatus(any(TaskStatus.class));
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByBoardSuccess_ReturnsList(){
        Long boardId = 1L;
        List<Task> listTask = List.of(
                TaskFactoryBot.createdTask(),
                TaskFactoryBot.createdTask()
        );
//        garante que o board existe e retorna um board para simular sucesso na validação.
        when(boardValidator.validateBoardExists(boardId)).thenReturn(TaskFactoryBot.createdTask().getBoard());
        when(port.findAllTaskByBoard(boardId)).thenReturn(listTask);

        List<Task> result = useCase.getTaskByBoard(boardId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(listTask.size(), result.size());

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verify(port, times(1)).findAllTaskByBoard(boardId);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByBoardSuccess_ReturnsEmptyList(){
        Long boardId = 1L;
        List<Task> listTask = List.of();

        when(boardValidator.validateBoardExists(boardId)).thenReturn(TaskFactoryBot.createdTask().getBoard());
        when(port.findAllTaskByBoard(boardId)).thenReturn(listTask);

        List<Task> result = useCase.getTaskByBoard(boardId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verify(port, times(1)).findAllTaskByBoard(boardId);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByBoardError_WhenBoardNotFound(){
        Long boardId = 100L;

        when(boardValidator.validateBoardExists(boardId)).thenThrow(BoardNotFoundException.class);

        assertThrows(BoardNotFoundException.class, () -> useCase.getTaskByBoard(boardId));

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verify(port, never()).findAllTaskByBoard(boardId);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByBoardAndStatusSuccess_ReturnsList(){
        String statusString = "TODO";
        TaskStatus statusEnum = TaskStatus.TODO;
        Long boardId = 1L;
        List<Task> listTask = List.of(
                TaskFactoryBot.createdTask(),
                TaskFactoryBot.createdTask()
        );

        when(port.findByBoardAndStatus(boardId, statusEnum)).thenReturn(listTask);

        List<Task> result = useCase.getByBoardAndStatus(boardId, statusString);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(listTask.size(), result.size());

        verify(port, times(1)).findByBoardAndStatus(boardId, statusEnum);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByBoardAndStatusSuccess_ReturnsEmptyList(){
        String statusString = "TODO";
        TaskStatus statusEnum = TaskStatus.TODO;
        Long boardId = 1L;
        List<Task> listTask = List.of();

        when(port.findByBoardAndStatus(boardId, statusEnum)).thenReturn(listTask);

        List<Task> result = useCase.getByBoardAndStatus(boardId, statusString);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(port, times(1)).findByBoardAndStatus(boardId, statusEnum);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByBoardAndStatusError_WhenBoardNotFound(){
        Long boardId = 100L;
        String status = "TODO";

        when(boardValidator.validateBoardExists(boardId)).thenThrow(BoardNotFoundException.class);

        assertThrows(BoardNotFoundException.class, () -> useCase.getByBoardAndStatus(boardId, status));

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verify(port, never()).findByBoardAndStatus(any(Long.class), any(TaskStatus.class));
        verifyNoMoreInteractions(port);

    }

    @Test
    void getByBoardAndStatusError_WhenInvalidStatus(){
        Long boardId = 1L;
        String statusString = "invalid_status";

        when(boardValidator.validateBoardExists(boardId))
                .thenReturn(TaskFactoryBot.createdTask().getBoard());

        assertThrows(InvalidStatusException.class, () -> useCase.getByBoardAndStatus(boardId, statusString));

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verify(port, never()).findByBoardAndStatus(any(Long.class), any(TaskStatus.class));
        verifyNoMoreInteractions(port);
    }

    @Test
    void getLastTaskSuccess(){
        Task task = TaskFactoryBot.createdTask();

        when(port.findLastCreatedTask()).thenReturn(Optional.of(task));

        Optional<Task> result = useCase.getLastCreatedTask();

        assertNotNull(result);
        assertEquals(task.getId(), result.get().getId());

        verify(port, times(1)).findLastCreatedTask();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getLastTaskSuccess_NoTaskFound(){
        when(port.findLastCreatedTask()).thenReturn(Optional.empty());

        Optional<Task> result = useCase.getLastCreatedTask();

        assertFalse(result.isPresent());

        verify(port, times(1)).findLastCreatedTask();
        verifyNoMoreInteractions(port);

    }

    @Test
    void getByDueDateSuccess_ReturnsList(){
        String dueDateString = "2025-10-09";
        LocalDate dueDate = LocalDate.parse(dueDateString); // .parse faz a formatação da data normal que usamos, para o tipo LocalDate do Java.
        List<Task> listTask = List.of(
                TaskFactoryBot.createdTask(),
                TaskFactoryBot.createdTask()
        );

        when(port.findByDueDate(dueDate)).thenReturn(listTask);

        List<Task> result = useCase.getByDueDate(dueDateString);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(listTask.size(), result.size());

        verify(port, times(1)).findByDueDate(dueDate);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByDueDateSuccess_ReturnsEmptyList(){
        String dueDateString = "2025-10-09";
        LocalDate dueDate = LocalDate.parse(dueDateString);
        List<Task> listTask = List.of();

        when(port.findByDueDate(dueDate)).thenReturn(listTask);

        List<Task> result = useCase.getByDueDate(dueDateString);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(port, times(1)).findByDueDate(dueDate);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getBuDueDateError_WhenInvalidDateFormat(){
        String dueDateString = "09/10/2025";

        assertThrows(InvalidDateFormatException.class, () -> useCase.getByDueDate(dueDateString));

        verify(port, never()).findByDueDate(any(LocalDate.class));
        verifyNoMoreInteractions(port);
    }
}
