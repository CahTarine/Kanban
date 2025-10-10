package com.projeto.quadrokanban.adapter.input.swagger;

import com.projeto.quadrokanban.core.domain.model.Task;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Task Controller", description = "Gerencia todas as operações de CRUD para Tasks.")
public interface TaskSwagger {

    @Operation(summary = "Lista todas as Tasks", description = "Retorna uma lista de todos as tasks registrados.")
    @GetMapping
    ResponseEntity<List<Task>> getAll();

    @Operation(summary = "Busca Task por ID", description = "Retorna uma task específica pelo seu ID.")
    @GetMapping("/{id}")
    ResponseEntity<Task> getById(@PathVariable Long id);

    @Operation(summary = "Busca Task por Titulo", description = "Retorna uma lista de tasks que contêm o titulo especificado.")
    @GetMapping("/title/{title}")
    ResponseEntity<List<Task>> getByTitle(@PathVariable String title);

    @Operation(summary = "Cria uma nova Task", description = "Cria e persiste um novo objeto Task no banco de dados.")
    @PostMapping
    ResponseEntity<Task> post(@Valid @RequestBody Task task);

    @Operation(summary = "Atualiza uma Task", description = "Atualiza completamente as informações de uma task existente pelo seu ID.")
    @PutMapping("/{id}")
    ResponseEntity<Task> put(@PathVariable Long id, @Valid @RequestBody Task task);

    @Operation(summary = "Deleta uma Task", description = "Remove uma task do sistema pelo seu ID.")
    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id);

    @Operation(summary = "Busca Task por Status", description = "Retorna uma lista de tasks que contém o status especificado.")
    @GetMapping("/status/{status}")
    ResponseEntity<List<Task>> getByStatus(@PathVariable String status);

    @Operation(summary = "Busca Task por Board", description = "Retorna uma lista de tasks que estão registradas no board especificado.")
    @GetMapping("/board/{boardId}")
    ResponseEntity<List<Task>> getByBoard(@PathVariable Long boardId);

    @Operation(summary = "Busca Task por Board e Status", description = "Retorna uma lista de tasks que contém o board e status especificados.")
    @GetMapping("/board-status/{boardId}/{status}")
    ResponseEntity<List<Task>> getByBoardIdAndStatus(@PathVariable Long boardId, @PathVariable String status);

    @Operation(summary = "Busca última Task", description = "Retorna a última task criada.")
    @GetMapping("/last-task")
    ResponseEntity<Task> getLastCreatedTask();

    @Operation(summary = "Busca Task por data de expiração", description = "Retorna uma lista de tasks que expiram/expiraram na data especificada.")
    @GetMapping("/duedate/{dueDate}")
    ResponseEntity<List<Task>> getByDueDate(@PathVariable String dueDate);
}
