<h1 align="center">🕹️ Projeto Kanban </h1>

<p align="center">
  <img src="https://img.shields.io/badge/status-em%20desenvolvimento-purple?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Java-17-purple?style=for-the-badge&logo=java&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-3.5.5-purple?style=for-the-badge&logo=spring&logoColor=white" />
  <img src="https://img.shields.io/badge/Maven-3.8.6-purple?style=for-the-badge&logo=apachemaven&logoColor=white" />
</p>

<p align="center">
  <a href="#📝-descrição-do-projeto">Descrição</a> • 
  <a href="#🚀-tecnologias-utilizadas">Tecnologias</a> • 
  <a href="#⚙️-funcionalidades">Funcionalidades</a> •
  <a href="#📁-estrutura-do-projeto">Estrutura</a> • 
  <a href="#🛠️-como-executar">Excutar Localmente</a> • 
  <a href="#💡-melhorias-futuras">Melhorias</a>
</p>

---

## 📝 Descrição do Projeto

Projeto de um quadro de organização de tarefas criado para aplicar conhecimentos adquiridos sobre **Arquitetura Hexagonal (Ports & Adapters)**, Microsserviços e Testes.

*Obs:* O projeto conta com diversos comentários explicativos, já que a intenção é consolidar novos conhecimentos.

---

## 🚀 Tecnologias Utilizadas

| Categoria | Tecnologia | Versão |
| :--- | :--- | :--- |
| Linguagem | **Java** | 17 |
| Framework | **Spring Boot** | 3.5.5 |
| Gerenciador de Dependência | **Maven** | - |
| Banco de Dados | **PostgreSQL** | - |
| Persistência | **JDBCTemplate** | - |
| Mapeamento | **MapStruct** | 1.5.5.Final |
| Testes | **JUnit** | - |
| Outras | **Lombok**, Functions e Procedures em **plpgsql**, **Insomnia** (para testes de API), **SonarQube** | - |

---

## ⚙️ Funcionalidades

O projeto oferece as seguintes funcionalidades via API REST:

* Cadastro de tasks, boards e users.
* Atualização e remoção de tasks, boards e users.
* **Endpoints de Listagem Avançada**: Busca de boards com tasks atrasadas, busca de tasks por data de expiração, busca por status, etc.
* Validações básicas nos campos via `spring-boot-starter-validation`.

---

## 📁 Estrutura do Projeto

O projeto segue a estrutura padrão da **Arquitetura Hexagonal**, com as seguintes camadas principais:
````
quadrokanban/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com.projeto.quadrokanban/
│   │           ├── adapter/      
│   │           │   ├── input/
│   │           │   │   ├── controller/
│   │           │   │   └── request/
│   │           │   └── output/
│   │           │       ├── entity/
│   │           │       ├── mapper/
│   │           │       └── repository/
│   │           ├── core/         
│   │           │   ├── domain/
│   │           │   │   ├── exception/
│   │           │   │   ├── model/
│   │           │   │── enums/
│   │           │   ├── port/     
│   │           │   │   ├── input/
│   │           │   │   └── output/
│   │           │   └── usecase/ 
│   │           ├── infrastructure/
│   │           └── util/
│   └── resources/
└── test/                
````
---

## 🛠️ Como Executar

### 1. Pré-requisitos

* **Java Development Kit (JDK) 17** ou superior.
* **Maven** para gerenciamento de dependências.
* Um servidor **PostgreSQL** em execução.

### 2. Configuração do Banco de Dados

O projeto utiliza **PostgreSQL**. Você deve configurar as credenciais de acesso no arquivo de propriedades (`src/main/resources/application.properties`).

```properties
# Configurações do PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/nome_do_seu_banco
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.datasource.driver-class-name=org.postgresql.Driver
````

### 3. Execução Local

Clone o repositório:
```

git clone https://github.com/CahTarine/Kanban.git
cd quadrokanban

```

Compile o projeto:
Este comando baixa as dependências e gera o código necessário com MapStruct e Lombok:
```

mvn clean install

```

### 4. Execute a aplicação:
- Via IDE: Execute a classe principal QuadrokanbanApplication.java.

- Via Terminal (Maven):
````

mvn spring-boot:run

````

### 4. Testes e Acesso
- Rodar Testes: Para executar os testes unitários e de integração:
```

mvn test

```

- Acesse a Aplicação:
A API estará disponível em http://localhost:8080.

## 💡 Melhorias Futuras

- Autenticação com Spring Security

- Deploy em nuvem (Heroku, Render ou Railway)

- Integração com frontend Angular ou React

##

## 🤝 Contribuições

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou pull requests.

##

## 👩🏻‍💻 Desenvolvedora

Feito com 💜 por Camille Tarine!


