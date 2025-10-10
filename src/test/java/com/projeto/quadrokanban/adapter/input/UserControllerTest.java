package com.projeto.quadrokanban.adapter.input;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projeto.quadrokanban.adapter.input.controller.UserController;
import com.projeto.quadrokanban.core.domain.exception.UserNotFoundException;
import com.projeto.quadrokanban.core.domain.model.User;
import com.projeto.quadrokanban.core.port.input.UserInputPort;
import com.projeto.quadrokanban.factory.UserFactoryBot;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import(UserControllerTest.MockConfig.class) // Importa a configuração do Mock
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc; // faz parte do Spring Boot, simula a requisição HTTP

    @Autowired
    private UserInputPort port;

    @Autowired
    private ObjectMapper objectMapper; // Converte objetos Java em Json

    @InjectMocks
    UserController controller;

    @TestConfiguration
    static class MockConfig {
        @Bean // Cria o mock como um bean
        public UserInputPort port() {
            return mock(UserInputPort.class);
        }
    }

    @AfterEach
    public void tearDown() {
        clearInvocations(port); // Remove todas as interações e stubs do mock
    }

    @Test
    void getAllSuccess() throws Exception{

        User user = UserFactoryBot.createdUser();
        List<User> usersList = List.of(user);

        when(port.getAllUsers()).thenReturn(usersList);

        mockMvc.perform(get("/users") // O endpoint que vai ser testado
                        .contentType(MediaType.APPLICATION_JSON)) // Usada no .perform, define o tipo de conteudo que esta sendo enviado ao servidor
                // 1. Verifica se o status HTTP é 200 OK
                .andExpect(status().isOk())
                // 2. Verifica se o tipo de conteúdo é JSON
                .andExpect(content().contentType(MediaType.APPLICATION_JSON)) // usada no .andExpect, representa o tipo de conteudo que o servidor esta retornando
                // 3. Verifica se o JSON retornado tem o tamanho correto
                .andExpect(jsonPath("$.length()").value(usersList.size())) // JSONPath é uma linguagem de consulta para selecionar e extrair dados de uma estrutura JSON.
                // O $ aponta para a lista que vai ser examinada e o .length calcula o tamanho dela.

                // 4. Verifica o valor de um campo específico no primeiro objeto JSON
                .andExpect(jsonPath("$[0].name").value(user.getName()));

        verify(port, times(1)).getAllUsers();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getAllSuccess_ReturnsEmptyList() throws Exception {
        List<User> emptyList = List.of();

        when(port.getAllUsers()).thenReturn(emptyList);

        mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                // Verifica se o JSON retornado é uma lista vazia
                .andExpect(jsonPath("$.length()").value(0));

        verify(port, times(1)).getAllUsers();
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByIdSuccess() throws Exception {
        Long id = 1L;
        User user = UserFactoryBot.createdUser();

        when(port.getUserById(id)).thenReturn(user);

        mockMvc.perform(get("/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()));

        verify(port, times(1)).getUserById(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByIdSuccess_ReturnsEmpty() throws Exception {
        Long id = 100L;

        when(port.getUserById(id)).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(port, times(1)).getUserById(id);
        verifyNoMoreInteractions(port);

    }

    @Test
    void getByNameSuccess() throws Exception {
        String name = "Ana";
        User user = UserFactoryBot.createdUser();
        List<User> usersList = List.of(user);

        when(port.getUserByName(name)).thenReturn(usersList);

        mockMvc.perform(get("/users/name/{name}", name)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(usersList.size()))
                .andExpect(jsonPath("$[0].name").value(user.getName()));

        verify(port, times(1)).getUserByName(name);
        verifyNoMoreInteractions(port);
    }

    @Test
    void getByNameSuccess_ReturnsEmptyList() throws Exception {
        String name = "Ana";
        List<User> emptyList = List.of();

        when(port.getUserByName(name)).thenReturn(emptyList);

        mockMvc.perform(get("/users/name/{name}", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(port, times(1)).getUserByName(name);
        verifyNoMoreInteractions(port);
    }

    @Test
    void postSuccess() throws Exception {
        User userToCreate = UserFactoryBot.validUser(); // Objeto com os dados pra criação
        User createdUser = UserFactoryBot.createdUser(); // User retornado pelo usecase (com ID)

        when(port.createdUser(any(User.class))).thenReturn(createdUser);

        String userJson = objectMapper.writeValueAsString(userToCreate); // Converte o objeto de entrada para Json

        mockMvc.perform(post("/users") // Endpoint POST /users
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson)) // 👈 Adiciona o corpo JSON

                .andExpect(status().isCreated())

                // Verifica se os dados do User criado foram retornados
                .andExpect(jsonPath("$.id").value(createdUser.getId()))
                .andExpect(jsonPath("$.name").value(createdUser.getName()))
                .andExpect(jsonPath("$.email").value(createdUser.getEmail()));

        verify(port, times(1)).createdUser(any(User.class));
        verifyNoMoreInteractions(port);
    }

//    @Test
//    void postInvalidUser_ReturnsBadRequest() throws Exception {
//        User invalidUser = UserFactoryBot.userWithEmptyName();
//
//        String invalidUserJson = objectMapper.writeValueAsString(invalidUser);
//
//        mockMvc.perform(post("/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(invalidUserJson))
//                .andExpect(status().isBadRequest());
//
//        verify(port, never()).createdUser(any(User.class));
//    }

    @Test
    void updateSuccess() throws Exception {
        Long id = 1L;
        User userToUpdate = UserFactoryBot.validUser();
        User updatedUser = UserFactoryBot.updatedUser();

        String updatesJson = objectMapper.writeValueAsString(userToUpdate);

        when(port.updateUser(eq(id), any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatesJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()));

        verify(port, times(1)).updateUser(eq(id), any(User.class));
        verifyNoMoreInteractions(port);
    }

    @Test
    void updateError_NotFound() throws Exception {
        Long id = 100L;
        User userToUpdate = UserFactoryBot.validUser();

        String updatesJson = objectMapper.writeValueAsString(userToUpdate);

        when(port.updateUser(eq(id), any(User.class)))
                .thenThrow(new UserNotFoundException("User not found."));

        mockMvc.perform(put("/users/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatesJson))
                .andExpect(status().isNotFound());

        verify(port, times(1)).updateUser(eq(id), any(User.class));
        verifyNoMoreInteractions(port);
    }

//    @Test
//    void updateInvalidUser_ReturnsBadRequest() throws Exception {
//
//    }

    @Test
    void deleteSuccess() throws Exception {
        Long id = 1L;

        doNothing().when(port).deleteUser(id);

        mockMvc.perform(delete("/users/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(port, times(1)).deleteUser(id);
        verifyNoMoreInteractions(port);
    }

    @Test
    void deleteError_NotFound() throws Exception{
        Long id = 100L;

        doThrow(new UserNotFoundException("User not found.")).when(port).deleteUser(id);

        mockMvc.perform(delete("/users/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(port, times(1)).deleteUser(id);
        verifyNoMoreInteractions(port);
    }
}
