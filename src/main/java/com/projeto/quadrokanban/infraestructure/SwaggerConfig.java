package com.projeto.quadrokanban.infraestructure;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    OpenAPI springBlogPessoalOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("Projeto Quadro Kanban").description("API RESTful para gerenciamento de um quadro Kanban. Inclui endpoints para usuários, tarefas e quadros.")
                        .version("v0.0.1")
                        .license(new License().name("Camille Tarine")
                                .url("https://github.com/CahTarine/Kanban"))
                        .contact(new Contact().name("Camille Tarine")
                                .url("https://www.linkedin.com/in/camille-tarine/").email("devcamilletarine@gmail.com")))
                .externalDocs(new ExternalDocumentation().description("GitHub").url("https://github.com/CahTarine"));
    }

    @Bean
    OpenApiCustomizer customerGlobalHeaderOpenCustomizer() {

        return openApi -> {
            openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations()
                    .forEach(operation -> {

                        ApiResponses apiResponses = operation.getResponses();

                        apiResponses.addApiResponse("200", createApiResponse("Sucesso!"));
                        apiResponses.addApiResponse("201", createApiResponse("Objeto Persistido!"));
                        apiResponses.addApiResponse("204", createApiResponse("Objeto Excluido!"));
                        apiResponses.addApiResponse("400", createApiResponse("Erro na Requisicao!"));
                        apiResponses.addApiResponse("401", createApiResponse("Acesso Nao Autorizado!"));
                        apiResponses.addApiResponse("403", createApiResponse("Acesso Proibido!"));
                        apiResponses.addApiResponse("404", createApiResponse("Objeto Nao Encontrado!"));
                        apiResponses.addApiResponse("500", createApiResponse("Erro na Aplicacao!"));

                    }));
        };
    }

    private ApiResponse createApiResponse(String message) {
        return new ApiResponse().description(message);
    }
}
//http://localhost:8080/swagger-ui/index.html#/
