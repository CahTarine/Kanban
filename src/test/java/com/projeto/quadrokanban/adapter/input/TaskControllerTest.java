package com.projeto.quadrokanban.adapter.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.quadrokanban.adapter.input.controller.TaskController;
import com.projeto.quadrokanban.core.domain.exception.TaskNotFoundException;
import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.port.input.TaskInputPort;
import com.projeto.quadrokanban.factory.TaskFactoryBot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TaskController.class)
@Import(TaskControllerTest.MockConfig.class)
class TaskControllerTest {

    @Autowired
    private TaskInputPort port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @InjectMocks
    TaskController controller;

    @TestConfiguration
    static class MockConfig {
        @Bean // Cria o mock como um bean
        public TaskInputPort port() {
            return mock(TaskInputPort.class);
        }
    }

    @AfterEach
    public void tearDown() {
        clearInvocations(port); // Remove todas as interações e stubs do mock
    }

    @Test
    void getAllSuccess() throws Exception{
        Task task = TaskFactoryBot.createdTask();
        List<Task> listTask = List.of(task);

        when(port.getAll()).thenReturn(listTask);

        mockMvc.perform(get("/task")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(listTask.size()))
                .andExpect(jsonPath("$[0].title").value(task.getTitle()));

        verify(port, times(1)).getAll();
        verifyNoMoreInteractions(port);

    }

    @Test
    void getAllSuccess_ReturnsEmptyList() throws Exception {
        List<Task> emptyList = List.of();

        when(port.getAll()).thenReturn(emptyList);

        mockMvc.perform(get("/task")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(port, times(1)).getAll();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByIdSuccess() throws Exception {
        Long id = 1L;
        Task task = TaskFactoryBot.createdTask();

        when(port.getById(id)).thenReturn(task);

        mockMvc.perform(get("/task/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value(task.getTitle()));

        verify(port, times(1)).getById(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByIdSuccess_ReturnsEmpty() throws Exception {
        Long id = 100L;

        when(port.getById(id)).thenThrow(new TaskNotFoundException("Task not found."));

        mockMvc.perform(get("/task/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(port, times(1)).getById(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByTitleSuccess() throws Exception{
        String title = "Test";
        Task task = TaskFactoryBot.createdTask();
        List<Task> listTask = List.of(task);

        when(port.getByTitle(title)).thenReturn(listTask);

        mockMvc.perform(get("/task/title/{title}", title)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(listTask.size()))
                .andExpect(jsonPath("$[0].title").value(task.getTitle()));

        verify(port, times(1)).getByTitle(title);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByTitleSuccess_ReturnsEmptyList() throws Exception {
        String title = "Test";
        List<Task> emptyList = List.of();

        when(port.getByTitle(title)).thenReturn(emptyList);

        mockMvc.perform(get("/task/title/{title}", title)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(port, times(1)).getByTitle(title);
        verifyNoMoreInteractions(port);
    }

    @Test
    void postSuccess() throws Exception{
        Long boardId = 1L;
        Task task = TaskFactoryBot.validTask();
        Task createdTask = TaskFactoryBot.createdTask();

        String taskJson = objectMapper.writeValueAsString(task);

        when(port.createTaskWithBoard(any(Task.class), eq(boardId))).thenReturn(createdTask);

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(createdTask.getId()))
                .andExpect(jsonPath("$.title").value(createdTask.getTitle()))
                .andExpect(jsonPath("$.description").value(createdTask.getDescription()))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.board.id").value(boardId))
                .andExpect(jsonPath("$.dueDate").value("2025-10-20T00:00:00"))
                .andExpect(jsonPath("$.userId").value(createdTask.getUserId()));

        verify(port, times(1)).createTaskWithBoard(any(Task.class), eq(boardId));
        verifyNoMoreInteractions(port);
    }


//    @Test
//    void postInvalidUser_ReturnsBadRequest() throws Exception

    @Test
    void updateSuccess() throws Exception{
        Long id = 1L;
        Long boardId = 1L;
        Task task = TaskFactoryBot.validTask();
        Task updatedTask = TaskFactoryBot.updatedTask(1L);

        String taskJson = objectMapper.writeValueAsString(task);

        when(port.updateTask(eq(id), any(Task.class))).thenReturn(updatedTask);

        mockMvc.perform(put("/task/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(updatedTask.getId()))
                .andExpect(jsonPath("$.title").value(updatedTask.getTitle()))
                .andExpect(jsonPath("$.description").value(updatedTask.getDescription()))
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.board.id").value(boardId))
                .andExpect(jsonPath("$.dueDate").value("2025-10-20T00:00:00"))
                .andExpect(jsonPath("$.userId").value(updatedTask.getUserId()));

        verify(port, times(1)).updateTask(eq(id), any(Task.class));
        verifyNoMoreInteractions(port);
    }

    @Test
    void updateError_NotFound() throws Exception{
        Long id = 100L;
        Task taskToUpdate = TaskFactoryBot.validTask();

        String updatesJson = objectMapper.writeValueAsString(taskToUpdate);

        when(port.updateTask(eq(id), any(Task.class)))
                .thenThrow(new TaskNotFoundException("Task not found."));

        mockMvc.perform(put("/task/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatesJson))
                .andExpect(status().isNotFound());

        verify(port, times(1)).updateTask(eq(id), any(Task.class));
        verifyNoMoreInteractions(port);
    }

    @Test
    void deleteSuccess() throws Exception{
        Long id = 1L;

        doNothing().when(port).deleteTask(id);

        mockMvc.perform(delete("/task/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(port, times(1)).deleteTask(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void deleteError_NotFound() throws Exception{
        Long id = 1L;

        doThrow(new TaskNotFoundException("Task not found.")).when(port).deleteTask(id);

        mockMvc.perform(delete("/task/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(port, times(1)).deleteTask(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByStatusSuccess() throws Exception{
        String status = "TODO";
        Task task = TaskFactoryBot.createdTask();
        List<Task> listTask = List.of(task);

        when(port.getByStatus(status)).thenReturn(listTask);

        mockMvc.perform(get("/task/status/{status}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(listTask.size()))
                .andExpect(jsonPath("$[0].status").value(status));

        verify(port, times(1)).getByStatus(status);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByStatusSuccess_ReturnsEmptyList() throws Exception{
        String status = "ACTIVE";
        List<Task> emptyList = List.of();

        when(port.getByStatus(status)).thenReturn(emptyList);

        mockMvc.perform(get("/task/status/{status}", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(port, times(1)).getByStatus(status);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByBoardSuccess() throws Exception{
        Long boardId = 1L;
        Task task = TaskFactoryBot.createdTask();
        List<Task> listTask = List.of(task);

        when(port.getTaskByBoard(boardId)).thenReturn(listTask);

        mockMvc.perform(get("/task/board/{boardId}", boardId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(listTask.size()))
                .andExpect(jsonPath("$[0].board.id").value(boardId));

        verify(port, times(1)).getTaskByBoard(boardId);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByBoardSuccess_ReturnsEmptyList() throws Exception{
        Long boardId = 1L;
        List<Task> emptyList = List.of();

        when(port.getTaskByBoard(boardId)).thenReturn(emptyList);

        mockMvc.perform(get("/task/board/{boardId}", boardId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(port, times(1)).getTaskByBoard(boardId);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByBoardAndStatusSuccess() throws Exception{
        Long boardId = 1L;
        String status = "TODO";

        Task task = TaskFactoryBot.createdTask();
        List<Task> listTask = List.of(task);

        when(port.getByBoardAndStatus(eq(boardId), eq(status))).thenReturn(listTask);

        mockMvc.perform(get("/task/board-status/{boardId}/{status}", boardId, status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(listTask.size()))
                .andExpect(jsonPath("$[0].board.id").value(boardId))
                .andExpect(jsonPath("$[0].status").value(status));

        verify(port, times(1)).getByBoardAndStatus(eq(boardId), eq(status));
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByBoardAndStatusSuccess_ReturnsEmptyList() throws Exception{
        Long boardId = 1L;
        String status = "TODO";

        List<Task> emptyList = List.of();

        when(port.getByBoardAndStatus(eq(boardId), eq(status))).thenReturn(emptyList);

        mockMvc.perform(get("/task/board-status/{boardId}/{status}", boardId, status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(port, times(1)).getByBoardAndStatus(eq(boardId), eq(status));
        verifyNoMoreInteractions(port);
    }

    @Test
    void getLastTaskSuccess() throws Exception{
        Task task = TaskFactoryBot.createdTask();

        when(port.getLastCreatedTask()).thenReturn(Optional.of(task));

        mockMvc.perform(get("/task/last-task")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value(task.getTitle()));

        verify(port, times(1)).getLastCreatedTask();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getLastTaskSuccess_ReturnsEmpty() throws Exception{
        when(port.getLastCreatedTask()).thenReturn(Optional.empty());

        mockMvc.perform(get("/task/last-task"))
                .andExpect(status().isNotFound());

        verify(port, times(1)).getLastCreatedTask();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByDueDateSuccess() throws Exception{
        Task task = TaskFactoryBot.createdTask();
        List<Task> listTask = List.of(task);
        String dueDate = "2025-10-20T00:00:00";

        when(port.getByDueDate(dueDate)).thenReturn(listTask);

        mockMvc.perform(get("/task/duedate/{duedate}", dueDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(listTask.size()))
                .andExpect(jsonPath("$[0].dueDate").value(dueDate));

        verify(port, times(1)).getByDueDate(dueDate);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByDueDateSuccess_ReturnsEmptyList() throws Exception{
        Task task = TaskFactoryBot.createdTask();
        List<Task> emptyList = List.of();
        String dueDate = task.getDueDate().toString();

        when(port.getByDueDate(dueDate)).thenReturn(emptyList);

        mockMvc.perform(get("/task/duedate/{duedate}", dueDate)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(0));

        verify(port, times(1)).getByDueDate(dueDate);
        verifyNoMoreInteractions(port);
    }
}
