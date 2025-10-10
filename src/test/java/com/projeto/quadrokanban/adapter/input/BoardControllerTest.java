package com.projeto.quadrokanban.adapter.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.quadrokanban.adapter.input.controller.BoardController;
import com.projeto.quadrokanban.core.domain.exception.BoardNotFoundException;
import com.projeto.quadrokanban.core.domain.exception.UserNotFoundException;
import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.port.input.BoardInputPort;
import com.projeto.quadrokanban.factory.BoardFactoryBot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BoardController.class)
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BoardInputPort port;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    BoardController controller;

    @TestConfiguration
    static class MockConfig {
        @Bean // Cria o mock como um bean
        public BoardInputPort port() {
            return mock(BoardInputPort.class);
        }
    }

    @AfterEach
    public void tearDown() {
        clearInvocations(port); // Remove todas as interações e stubs do mock
    }

    @Test
    void getAllSuccess() throws Exception {
        Board board = BoardFactoryBot.createdBoard();
        List<Board> listBoard = List.of(board);

        when(port.getAllBoards()).thenReturn(listBoard);

        mockMvc.perform(get("/board")
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(listBoard.size()))
                .andExpect(jsonPath("$[0].name").value(board.getName()));

        verify(port, times(1)).getAllBoards();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getAllSuccess_ReturnsEmptyList() throws Exception {
        List<Board> emptyList = List.of();

        when(port.getAllBoards()).thenReturn(emptyList);

        mockMvc.perform(get("/board")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(port, times(1)).getAllBoards();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByIdSuccess() throws Exception {
        Long id = 1L;
        Board board = BoardFactoryBot.createdBoard();

        when(port.getById(id)).thenReturn(board);

        mockMvc.perform(get("/board/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(board.getId()))
                .andExpect(jsonPath("$.name").value(board.getName()));

        verify(port, times(1)).getById(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByIdSuccess_ReturnsEmpty() throws Exception {
        Long id = 100L;

        when(port.getById(id)).thenThrow(new BoardNotFoundException("Board not found."));

        mockMvc.perform(get("/board/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isNotFound());

        verify(port, times(1)).getById(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByNameSuccess() throws Exception{
        String name = "Test";
        Board board = BoardFactoryBot.createdBoard();
        List<Board> listBoard = List.of(board);

        when(port.getByName(name)).thenReturn(listBoard);

        mockMvc.perform(get("/board/name/{name}", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(listBoard.size()))
                .andExpect(jsonPath("$[0].name").value(board.getName()));

        verify(port, times(1)).getByName(name);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByNameSuccess_ReturnsEmptyList() throws Exception {
        String name = "Test";
        List<Board> emptyList = List.of();

        when(port.getByName(name)).thenReturn(emptyList);

        mockMvc.perform(get("/board/name/{name}", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(port, times(1)).getByName(name);
        verifyNoMoreInteractions(port);
    }

    @Test
    void postSuccess() throws Exception{
        Board board = BoardFactoryBot.validBoard();
        Board createdBoard = BoardFactoryBot.createdBoard();

        String boardJson = objectMapper.writeValueAsString(board);

        when(port.createdBoard(any(Board.class))).thenReturn(createdBoard);

        mockMvc.perform(post("/board")
                    .contentType(MediaType.APPLICATION_JSON)
                .content(boardJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createdBoard.getId()))
                .andExpect(jsonPath("$.name").value(createdBoard.getName()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(port, times(1)).createdBoard(any(Board.class));
        verifyNoMoreInteractions(port);
    }

//    @Test
//    void postInvalidUser_ReturnsBadRequest() throws Exception

    @Test
    void updateSuccess() throws Exception{
        Long id = 1L;
        Board board = BoardFactoryBot.validBoard();
        Board updatedBoard = BoardFactoryBot.updateBoard();

        String updatesJson = objectMapper.writeValueAsString(board);

        when(port.updateBoard(eq(id), any(Board.class))).thenReturn(updatedBoard);

        mockMvc.perform(put("/board/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatesJson))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(updatedBoard.getId()))
                .andExpect(jsonPath("$.name").value(updatedBoard.getName()))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        verify(port, times(1)).updateBoard(eq(id), any(Board.class));
        verifyNoMoreInteractions(port);
    }

    @Test
    void updateError_NotFound() throws Exception{
        Long id = 100L;
        Board boardToUpdate = BoardFactoryBot.validBoard();

        String updatesJson = objectMapper.writeValueAsString(boardToUpdate);

        when(port.updateBoard(eq(id), any(Board.class)))
                .thenThrow(new BoardNotFoundException("Board not found."));

        mockMvc.perform(put("/board/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatesJson))
                .andExpect(status().isNotFound());

        verify(port, times(1)).updateBoard(eq(id), any(Board.class));
        verifyNoMoreInteractions(port);
    }

//    @Test
//    void updateInvalidUser_ReturnsBadRequest() throws Exception

    @Test
    void deleteSuccess() throws Exception{
        Long id = 1L;

        doNothing().when(port).deleteBoard(id);

        mockMvc.perform(delete("/board/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(port, times(1)).deleteBoard(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void deleteError_NotFound() throws Exception{
        Long id = 1L;

        doThrow(new BoardNotFoundException("Board not found.")).when(port).deleteBoard(id);

        mockMvc.perform(delete("/board/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(port, times(1)).deleteBoard(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void countTasksSuccess() throws Exception{
        Long id = 1L;
        Long expectedCount = 5L;

        when(port.countTasks(id)).thenReturn(Optional.of(expectedCount));

        mockMvc.perform(get("/board/task-counts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedCount)));

        verify(port, times(1)).countTasks(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void countTasksSuccess_ReturnsZero() throws Exception {
        Long id = 1L;
        Long expectedCount = 0L;

        when(port.countTasks(id)).thenReturn(Optional.of(expectedCount));

        mockMvc.perform(get("/board/task-counts/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string(String.valueOf(expectedCount)));

        verify(port, times(1)).countTasks(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void countTasksError_NotFound() throws Exception{
        Long id = 100L;

        when(port.countTasks(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/board/task-counts/{id}", id))
                .andExpect(status().isNotFound());

        verify(port, times(1)).countTasks(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getOverdueBoardsSuccess() throws Exception{
        Board board = BoardFactoryBot.createdBoard();
        List<Board> listBoard = List.of(board);

        when(port.getBoadsWithOverdueTasks()).thenReturn(listBoard);

        mockMvc.perform(get("/board/overdue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(listBoard.size()));

        verify(port, times(1)).getBoadsWithOverdueTasks();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getOverdueBoardsSuccess_ReturnsEmptyList() throws Exception{
        List<Board> emptyList = List.of();

        when(port.getBoadsWithOverdueTasks()).thenReturn(emptyList);

        mockMvc.perform(get("/board/overdue")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(port, times(1)).getBoadsWithOverdueTasks();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByStatusSuccess() throws Exception{
        String status = "ACTIVE";
        Board board = BoardFactoryBot.createdBoard();
        List<Board> listBoard = List.of(board);

        when(port.getByStatus(status)).thenReturn(listBoard);

        mockMvc.perform(get("/board/status/{status}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(listBoard.size()))
                .andExpect(jsonPath("$[0].status").value(status));

        verify(port, times(1)).getByStatus(status);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByStatusSuccess_ReturnsEmptyList() throws Exception{
        String status = "ACTIVE";
        List<Board> emptyList = List.of();

        when(port.getByStatus(status)).thenReturn(emptyList);

        mockMvc.perform(get("/board/status/{status}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));;

        verify(port, times(1)).getByStatus(status);
        verifyNoMoreInteractions(port);
    }

    @Test
    void finalizedBoardSuccess() throws Exception{
        Long id = 1L;
        String expectedMessage = "Board " + id + " completed successfully";

        doNothing().when(port).finalizedBoard(id);

        mockMvc.perform(post("/board/{id}/finalize", id))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedMessage));

        verify(port, times(1)).finalizedBoard(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void finalizedBoardError_NotFound() throws Exception{
        Long id = 100L;

        doThrow(new BoardNotFoundException("Board not found.")).when(port).finalizedBoard(id);

        mockMvc.perform(post("/board/{id}/finalize", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(port, times(1)).finalizedBoard(id);
        verifyNoMoreInteractions(port);
    }
}
