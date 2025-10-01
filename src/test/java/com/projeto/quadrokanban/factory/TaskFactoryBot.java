package com.projeto.quadrokanban.factory;

import com.projeto.quadrokanban.core.domain.model.Board;
import com.projeto.quadrokanban.core.domain.model.Task;
import com.projeto.quadrokanban.core.domain.model.User;
import com.projeto.quadrokanban.core.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskFactoryBot {

    public static Task createdTask(){
        Board board = new Board(1L, "Board Teste", null, null);

        LocalDateTime createdAt = LocalDate.of(2025, 9, 10).atStartOfDay();
        LocalDateTime updatedAt = LocalDate.of(2025, 9, 15).atStartOfDay();
        LocalDateTime dueDate = LocalDate.of(2025, 10, 20).atStartOfDay();

        return new Task(1L, "Task Teste", "Task para testes unitários",
                createdAt, updatedAt, TaskStatus.TODO, board, dueDate, 1L);
    }

    public static Task updatedTask(Long userId){
        Board board = new Board(1L, "Board Teste", null, null);

        LocalDateTime createdAt = LocalDate.of(2025, 9, 10).atStartOfDay(); // atStarOfDay converte para LocalDateTime usando o inicio do dia (00:00:00)
        LocalDateTime updatedAt = LocalDate.of(2025, 9, 15).atStartOfDay();
        LocalDateTime dueDate = LocalDate.of(2025, 10, 20).atStartOfDay();

        return new Task(null, "Task Atualizada", "Task para testes unitários",
                createdAt, updatedAt, TaskStatus.DONE, board, dueDate, userId);
    }

    public static Task createdTaskWithoutBoard(){
        LocalDateTime createdAt = LocalDate.of(2025, 9, 10).atStartOfDay(); // atStarOfDay converte para LocalDateTime usando o inicio do dia (00:00:00)
        LocalDateTime updatedAt = LocalDate.of(2025, 9, 15).atStartOfDay();
        LocalDateTime dueDate = LocalDate.of(2025, 10, 20).atStartOfDay();

        return new Task(null, "Task Atualizada", "Task para testes unitários",
                createdAt, updatedAt, TaskStatus.DONE, null, dueDate, 1L);

    }

    public static Task createdTaskWithoutUser(){
        Board board = new Board(1L, "Board Teste", null, null);

        LocalDateTime createdAt = LocalDate.of(2025, 9, 10).atStartOfDay(); // atStarOfDay converte para LocalDateTime usando o inicio do dia (00:00:00)
        LocalDateTime updatedAt = LocalDate.of(2025, 9, 15).atStartOfDay();
        LocalDateTime dueDate = LocalDate.of(2025, 10, 20).atStartOfDay();

        return new Task(1L, "Task Atualizada", "Task para testes unitários",
                createdAt, updatedAt, TaskStatus.DONE, board, dueDate, null);
    }
}
