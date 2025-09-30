package com.projeto.quadrokanban.application;

import com.projeto.quadrokanban.core.domain.exception.BoardNotFoundException;
import com.projeto.quadrokanban.core.domain.exception.BoardValidationException;
import com.projeto.quadrokanban.core.domain.exception.InvalidStatusException;
import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.enums.BoardStatus;
import com.projeto.quadrokanban.core.enums.TaskStatus;
import com.projeto.quadrokanban.core.port.output.BoardOutputPort;
import com.projeto.quadrokanban.core.usecase.BoardUseCase;
import com.projeto.quadrokanban.core.usecase.BoardValidatorService;
import com.projeto.quadrokanban.factory.BoardFactoryBot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

//@SpringBootTest
@ExtendWith(SpringExtension.class) // ativa integração com spring, permitindo injeções de beans e outros componentes.
class BoardUseCaseTest {

    @Mock
    BoardValidatorService boardValidator;

    @Mock
    BoardOutputPort port;
//    BoardOutputPort boardPort = mock(BoardOutputPort.class); - forma mais verbosa de fazer, sem anotação.

    @InjectMocks
    BoardUseCase useCase;
//    BoardUseCase board = new BoardUseCase(boardPort);

    @Test
    void getAllSuccess_ReturnsList(){
        List<Board> listBoard = List.of(
                BoardFactoryBot.createdBoard(),
                BoardFactoryBot.createdBoard()
        );

        when(port.findAll()).thenReturn(listBoard);

        List<Board> result = useCase.getAllBoards();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(listBoard.size(), result.size());

        verify(port, times(1)).findAll();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getAllSuccess_ReturnsEmptyList(){
        List<Board> emptyList = List.of();

        when(port.findAll()).thenReturn(emptyList);

        List<Board> result = useCase.getAllBoards();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(port, times(1)).findAll();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getBoardByIdSuccess(){
        Long boardId = 1L;
        Board existingBoard = BoardFactoryBot.createdBoard();

        when(boardValidator.validateBoardExists(boardId)).thenReturn(existingBoard);

        Board result = useCase.getById(existingBoard.getId());

        assertNotNull(result);
        assertEquals(existingBoard.getId(), result.getId());
        assertEquals(existingBoard.getName(), result.getName());

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verifyNoMoreInteractions(port);

    }

    @Test
    void getByIdError_WhenBoardNotFound(){
        Long boardId = 100L;

        when(boardValidator.validateBoardExists(boardId)).thenThrow(BoardNotFoundException.class);

        assertThrows(BoardNotFoundException.class, () -> useCase.getById(boardId));

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByNameSuccess_ReturnsList(){
        String name = "Test";
        List<Board> listBoard = List.of(
                BoardFactoryBot.createdBoard(),
                BoardFactoryBot.createdBoard()
        );

        when(port.findAllByNameContainingIgnoreCase(name)).thenReturn(listBoard);

        List<Board> result = useCase.getByName(name);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(listBoard.size(), result.size());

        verify(port, times(1)).findAllByNameContainingIgnoreCase(name);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByNameSuccess_ReturnsEmptyList(){
        String name = "Test";
        List<Board> emptyList = List.of();

        when(port.findAllByNameContainingIgnoreCase(name)).thenReturn(emptyList);

        List<Board> result = useCase.getByName(name);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(port, times(1)).findAllByNameContainingIgnoreCase(name);
        verifyNoMoreInteractions(port);

    }

    @Test
    void createBoardSuccess(){
        Board board = BoardFactoryBot.createdBoard();

        when(port.save(board)).thenReturn(board);

        Board result = useCase.createdBoard(board);

        assertNotNull(result);
        assertEquals(board.getName(), result.getName());
        assertEquals(board.getStatus(), result.getStatus());

        verify(port, times(1)).save(board);
        verifyNoMoreInteractions(port);
    }

    @Test
    void updateBoardSuccess(){
//        Organiza os dados do teste
        Board existingBoard = BoardFactoryBot.createdBoard();
        Board updateBoard = BoardFactoryBot.updateBoard();

//        Define o comportamento do Mock
        when(boardValidator.validateBoardExists(existingBoard.getId())).thenReturn(existingBoard);
        when(port.save(any(Board.class))).thenAnswer(invocation -> invocation.getArgument(0)); //any(Board.class) - independente de qual objeto do tipo Board for passado como argumento...

//        Chama o metodo pra ser testado
        Board result = useCase.updateBoard(existingBoard.getId(), updateBoard);

//        Confere se o resultado foi como o esperado
        assertNotNull(result);
        assertEquals(updateBoard.getName(), result.getName());
        assertEquals(updateBoard.getStatus(), result.getStatus());

        verify(boardValidator, times(1)).validateBoardExists(existingBoard.getId());
//        Verifica se o metodo save foi chamado com o objeto certo
        verify(port, times(1)).save(result);
        verifyNoMoreInteractions(port);
    }

    @Test
    void updateBoardError_WhenBoardNotFound(){
        Board updateBoard = BoardFactoryBot.updateBoard();

        when(boardValidator.validateBoardExists(updateBoard.getId())).thenThrow(BoardNotFoundException.class);

        assertThrows(BoardNotFoundException.class, () -> useCase.updateBoard(updateBoard.getId(), updateBoard));

        verify(boardValidator, times(1)).validateBoardExists(updateBoard.getId());
        verify(port, never()).save(any(Board.class));
        verifyNoMoreInteractions(port);
    }

    @Test
    void deleteBoardSuccess() {
        Long boardId = 1L;
        Board existingBoard = BoardFactoryBot.createdBoard();

        when(boardValidator.validateBoardExists(boardId)).thenReturn(existingBoard);

        useCase.deleteBoard(boardId);

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verify(port, times(1)).deleteById(boardId);
        verifyNoMoreInteractions(port);

    }

    @Test
    void deleteBoardError_WhenBoardNotFound(){
        Long boardId = 100L;

        when(boardValidator.validateBoardExists(boardId)).thenThrow(BoardNotFoundException.class);

        assertThrows(BoardNotFoundException.class, () -> useCase.deleteBoard(boardId));

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verify(port, never()).deleteById(boardId);
        verifyNoMoreInteractions(port);
    }

    @Test
    void countTasksSuccess(){
        Long boardId = 1L;
        Optional<Long> count = Optional.of(5L); // simula a contagem

        when(boardValidator.validateBoardExists(boardId)).thenReturn(BoardFactoryBot.createdBoard());
        when(port.countTasksByBoard(boardId)).thenReturn(count);

        Optional<Long> result = useCase.countTasks(boardId);

        assertTrue(result.isPresent());
        assertEquals(count.get(), result.get());

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verify(port, times(1)).countTasksByBoard(boardId);
        verifyNoMoreInteractions(port);
    }

    @Test
    void countTasksError_WhenBoardNotFound(){
        Long boardId = 100L;

        when(boardValidator.validateBoardExists(boardId)).thenThrow(BoardNotFoundException.class);

        assertThrows(BoardNotFoundException.class, () -> useCase.countTasks(boardId));

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verify(port, never()).countTasksByBoard(boardId);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getBoardOverdueSuccess_ReturnsList(){
        List<Board> listBoard = List.of(
                BoardFactoryBot.createdBoard(),
                BoardFactoryBot.createdBoard()
        );

        when(port.findBoadsWithOverdueTasks()).thenReturn(listBoard);

        List<Board> result = useCase.getBoadsWithOverdueTasks();

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(listBoard.size(), result.size());

        verify(port, times(1)).findBoadsWithOverdueTasks();
        verifyNoMoreInteractions(port);

    }

    @Test
    void getBoardOverdueSuccess_ReturnsEmptyList(){
        List<Board> listEmpty = List.of();

        when(port.findBoadsWithOverdueTasks()).thenReturn(listEmpty);

        List<Board> result = useCase.getBoadsWithOverdueTasks();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(port, times(1)).findBoadsWithOverdueTasks();
        verifyNoMoreInteractions(port);

    }

    @Test
    void getByStatusSuccess(){
        String statusString = "ACTIVE";
        BoardStatus statusEnum = BoardStatus.ACTIVE;
        List<Board> listBoard = List.of(
                BoardFactoryBot.createdBoard(),
                BoardFactoryBot.createdBoard()
        );

        when(port.findByStatus(statusEnum)).thenReturn(listBoard);

        List<Board> result = useCase.getByStatus(statusString);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(listBoard.size(), result.size());

        verify(port, times(1)).findByStatus(statusEnum);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByStatusError_WhenInvalidStatus(){
        String statusString = "Invalid_status";

        assertThrows(InvalidStatusException.class, () -> useCase.getByStatus(statusString));

        verify(port, never()).findByStatus(any(BoardStatus.class));
        verifyNoMoreInteractions(port);
    }

    @Test
    void finalizedBoardSuccess() {
        Long boardId = 1L;

        when(port.areAllTasksDone(boardId)).thenReturn(true);

        useCase.finalizedBoard(boardId);

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verify(port, times(1)).areAllTasksDone(boardId);
        verify(port, times(1)).updateBoardStatus(boardId, BoardStatus.COMPLETED);
        verifyNoMoreInteractions(port);
    }

    @Test
    void finalizedBoardError(){
        Long boardId = 1L;

        when(port.areAllTasksDone(boardId)).thenReturn(false);

        assertThrows(BoardValidationException.class, () -> useCase.finalizedBoard(boardId));

        verify(port, never()).updateBoardStatus(anyLong(),any(BoardStatus.class));
    }

    @Test
    void finalizedBoardError_WhenBoardNotFound(){
        Long boardId = 100L;

        when(boardValidator.validateBoardExists(boardId)).thenThrow(BoardNotFoundException.class);

        assertThrows(BoardNotFoundException.class, () -> useCase.finalizedBoard(boardId));

        verify(boardValidator, times(1)).validateBoardExists(boardId);
        verify(port, never()).areAllTasksDone(boardId);
        verifyNoMoreInteractions(port);
    }


}
