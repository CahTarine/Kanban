package com.projeto.quadrokanban.adapter.input.swagger;

import com.projeto.quadrokanban.core.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User Controller", description = "Gerencia todas as operações de CRUD para Usuários.")
public interface UserSwagger {

    @Operation(summary = "Lista todos os Usuários", description = "Retorna uma lista de todos os usuários registrados.")
    @GetMapping
    ResponseEntity<List<User>> getAll();

    @Operation(summary = "Busca Usuário por ID", description = "Retorna um usuário específico pelo seu ID.")
    @GetMapping("/{id}")
    ResponseEntity<User> getUserById(@PathVariable Long id);

    @Operation(summary = "Busca Usuário por Nome", description = "Retorna uma lista de usuários que contêm o nome especificado.")
    @GetMapping("/name/{name}")
    ResponseEntity<List<User>> getUserByName(@PathVariable String name);

    @Operation(summary = "Cria um novo Usuário", description = "Cria e persiste um novo objeto Usuário no banco de dados.")
    @PostMapping
    ResponseEntity<User> post(@Valid @RequestBody User user);

    @Operation(summary = "Atualiza um Usuário", description = "Atualiza completamente as informações de um usuário existente pelo seu ID.")
    @PutMapping("/{id}")
    ResponseEntity<User> put(@Valid @RequestBody User userUpdates, @PathVariable Long id);

    @Operation(summary = "Deleta um Usuário", description = "Remove um usuário do sistema pelo seu ID.")
    @DeleteMapping("/{id}")
    void delete(@PathVariable Long id);
}
