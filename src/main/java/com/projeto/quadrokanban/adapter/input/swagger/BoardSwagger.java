package com.projeto.quadrokanban.adapter.input.swagger;

import com.projeto.quadrokanban.core.domain.model.Board;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Board Controller", description = "Gerencia todas as operações de CRUD para Boards.")
public interface BoardSwagger {

    @Operation(summary = "Lista todos os Boards", description = "Retorna uma lista de todos os boards registrados.")
    @GetMapping
    ResponseEntity<List<Board>> getAll();

    @Operation(summary = "Busca Board por ID", description = "Retorna um board específico pelo seu ID.")
    @GetMapping("/{id}")
    ResponseEntity<Board> getById(@PathVariable Long id);

    @Operation(summary = "Busca Board por Nome", description = "Retorna uma lista de boards que contêm o nome especificado.")
    @GetMapping("/name/{name}")
    ResponseEntity<List<Board>> getByName(@PathVariable String name);

    @Operation(summary = "Cria um novo Board", description = "Cria e persiste um novo objeto Board no banco de dados.")
    @PostMapping
    ResponseEntity<Board> post(@Valid @RequestBody Board board);

    @Operation(summary = "Atualiza um Board", description = "Atualiza completamente as informações de um board existente pelo seu ID.")
    @PutMapping("/{id}")
    ResponseEntity<Board> put(@PathVariable Long id, @Valid @RequestBody Board board);

    @Operation(summary = "Deleta um Board", description = "Remove um board do sistema pelo seu ID.")
    @DeleteMapping("/{id}")
    void delete (@PathVariable Long id);

    @Operation(summary = "Conta Tasks", description = "Retorna o número de tasks que o board possui por seu ID.")
    @GetMapping("/task-counts/{boardId}")
    ResponseEntity<Long> countTasks(@PathVariable Long boardId);

    @Operation(summary = "Busca Boards com Tasks expiradas", description = "Retorna uma lista de boards que contém tasks expiradas.")
    @GetMapping("/overdue")
    ResponseEntity<List<Board>> findOverdueBoards();

    @Operation(summary = "Busca Board por status", description = "Retorna uma lista de boards que contém o status especificado.")
    @GetMapping("/status/{status}")
    ResponseEntity<List<Board>> getByStatus(@PathVariable String status);

    @Operation(summary = "Finaliza um Board", description = "Busca um board por seu ID, verifica se todas as tasks inseridas nele contém o status DONE e altera o status do board para COMPLETED.")
    @PostMapping("/{boardId}/finalize")
    ResponseEntity<String> finalizedBoard(@PathVariable Long boardId);


}
