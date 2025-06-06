# Wallet Service

## Introdução

O Wallet Service é uma aplicação Spring Boot que fornece uma API RESTful para gerenciamento de carteiras digitais. A aplicação permite que usuários criem carteiras, associem usuários a essas carteiras e realizem transações financeiras como depósitos, saques e transferências entre carteiras.

O sistema foi projetado com uma arquitetura em camadas, seguindo boas práticas de desenvolvimento e utilizando tecnologias modernas para garantir escalabilidade, manutenção e segurança.

## Tecnologias Utilizadas

- **Java 21**: Linguagem de programação principal
- **Spring Boot 3.4.5**: Framework para desenvolvimento de aplicações Java
- **Spring Data JPA**: Facilita o acesso a dados com JPA
- **Spring Web**: Para desenvolvimento de APIs RESTful
- **Spring Validation**: Para validação de dados
- **PostgreSQL**: Banco de dados relacional
- **Flyway**: Ferramenta para migração de banco de dados
- **Lombok**: Biblioteca para redução de código boilerplate
- **MapStruct**: Framework para mapeamento de objetos
- **UUID Creator**: Biblioteca para geração de UUIDs
- **JUnit**: Framework para testes unitários

## Funcionalidades

### Gerenciamento de Carteiras (Wallets)
- Criação, consulta, atualização e exclusão de carteiras
- Cada carteira possui um nome único

### Gerenciamento de Usuários e Carteiras (User Wallets)
- Associação de usuários a carteiras
- Consulta de carteiras por usuário
- Consulta de usuários por carteira
- Gerenciamento de saldo por usuário em cada carteira

### Transações Financeiras
- **Depósito**: Adiciona fundos à carteira de um usuário
- **Saque**: Remove fundos da carteira de um usuário
- **Transferência**: Transfere fundos entre carteiras de usuários

## API Endpoints

### Carteiras (Wallets)

#### Listar todas as carteiras
```
GET /api/wallets
```

Resposta:
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Carteira Principal",
    "createdAt": "2023-01-01T12:00:00"
  },
  {
    "id": "223e4567-e89b-12d3-a456-426614174001",
    "name": "Carteira Secundária",
    "createdAt": "2023-01-02T12:00:00"
  }
]
```

#### Obter carteira por ID
```
GET /api/wallets/{id}
```

Resposta:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Carteira Principal",
  "createdAt": "2023-01-01T12:00:00"
}
```

#### Criar nova carteira
```
POST /api/wallets
```

Payload:
```json
{
  "name": "Nova Carteira"
}
```

Resposta:
```json
{
  "id": "323e4567-e89b-12d3-a456-426614174002",
  "name": "Nova Carteira",
  "createdAt": "2023-01-03T12:00:00"
}
```

#### Atualizar carteira
```
PUT /api/wallets/{id}
```

Payload:
```json
{
  "name": "Carteira Atualizada"
}
```

Resposta:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Carteira Atualizada",
  "createdAt": "2023-01-01T12:00:00"
}
```

#### Excluir carteira
```
DELETE /api/wallets/{id}
```

### Usuários e Carteiras (User Wallets)

#### Listar todas as associações de usuários e carteiras
```
GET /api/user-wallets
```

Resposta:
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "walletId": "123e4567-e89b-12d3-a456-426614174000",
    "walletName": "Carteira Principal",
    "balance": 1000.00,
    "createdAt": "2023-01-01T12:00:00",
    "updatedAt": "2023-01-01T12:00:00"
  }
]
```

#### Obter associação de usuário e carteira por ID
```
GET /api/user-wallets/{id}
```

Resposta:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "walletId": "123e4567-e89b-12d3-a456-426614174000",
  "walletName": "Carteira Principal",
  "balance": 1000.00,
  "createdAt": "2023-01-01T12:00:00",
  "updatedAt": "2023-01-01T12:00:00"
}
```

#### Listar carteiras de um usuário
```
GET /api/user-wallets/user/{userId}
```

#### Listar usuários de uma carteira
```
GET /api/user-wallets/wallet/{walletId}
```

#### Obter associação específica de usuário e carteira
```
GET /api/user-wallets/user/{userId}/wallet/{walletId}
```

#### Criar nova associação de usuário e carteira
```
POST /api/user-wallets
```

Payload:
```json
{
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "walletId": "123e4567-e89b-12d3-a456-426614174000"
}
```

Resposta:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "walletId": "123e4567-e89b-12d3-a456-426614174000",
  "walletName": "Carteira Principal",
  "balance": 0.00,
  "createdAt": "2023-01-01T12:00:00",
  "updatedAt": "2023-01-01T12:00:00"
}
```

### Transações (Transactions)

#### Listar todas as transações
```
GET /api/transactions
```

Resposta:
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "walletId": "123e4567-e89b-12d3-a456-426614174000",
    "walletName": "Carteira Principal",
    "fromUserId": null,
    "toUserId": "123e4567-e89b-12d3-a456-426614174000",
    "type": "DEPOSIT",
    "amount": 1000.00,
    "description": "Depósito inicial",
    "relatedTransactionId": null,
    "createdAt": "2023-01-01T12:00:00"
  }
]
```

#### Obter transação por ID
```
GET /api/transactions/{id}
```

Resposta:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "walletId": "123e4567-e89b-12d3-a456-426614174000",
  "walletName": "Carteira Principal",
  "fromUserId": null,
  "toUserId": "123e4567-e89b-12d3-a456-426614174000",
  "type": "DEPOSIT",
  "amount": 1000.00,
  "description": "Depósito inicial",
  "relatedTransactionId": null,
  "createdAt": "2023-01-01T12:00:00"
}
```

#### Realizar depósito
```
POST /api/transactions/deposit
```

Payload:
```json
{
  "walletId": "123e4567-e89b-12d3-a456-426614174000",
  "toUserId": "123e4567-e89b-12d3-a456-426614174000",
  "amount": 1000.00,
  "description": "Depósito inicial"
}
```

Resposta:
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "walletId": "123e4567-e89b-12d3-a456-426614174000",
  "walletName": "Carteira Principal",
  "fromUserId": null,
  "toUserId": "123e4567-e89b-12d3-a456-426614174000",
  "type": "DEPOSIT",
  "amount": 1000.00,
  "description": "Depósito inicial",
  "relatedTransactionId": null,
  "createdAt": "2023-01-01T12:00:00"
}
```

#### Realizar saque
```
POST /api/transactions/withdraw
```

Payload:
```json
{
  "walletId": "123e4567-e89b-12d3-a456-426614174000",
  "fromUserId": "123e4567-e89b-12d3-a456-426614174000",
  "amount": 500.00,
  "description": "Saque para despesas"
}
```

Resposta:
```json
{
  "id": "223e4567-e89b-12d3-a456-426614174001",
  "walletId": "123e4567-e89b-12d3-a456-426614174000",
  "walletName": "Carteira Principal",
  "fromUserId": "123e4567-e89b-12d3-a456-426614174000",
  "toUserId": null,
  "type": "WITHDRAW",
  "amount": 500.00,
  "description": "Saque para despesas",
  "relatedTransactionId": null,
  "createdAt": "2023-01-02T12:00:00"
}
```

#### Realizar transferência
```
POST /api/transactions/transfer
```

Payload:
```json
{
  "walletId": "123e4567-e89b-12d3-a456-426614174000",
  "destinationWalletId": "223e4567-e89b-12d3-a456-426614174001",
  "fromUserId": "123e4567-e89b-12d3-a456-426614174000",
  "toUserId": "223e4567-e89b-12d3-a456-426614174001",
  "amount": 300.00,
  "description": "Transferência para amigo"
}
```

Resposta:
```json
{
  "id": "323e4567-e89b-12d3-a456-426614174002",
  "walletId": "123e4567-e89b-12d3-a456-426614174000",
  "walletName": "Carteira Principal",
  "fromUserId": "123e4567-e89b-12d3-a456-426614174000",
  "toUserId": "223e4567-e89b-12d3-a456-426614174001",
  "type": "TRANSFER",
  "amount": 300.00,
  "description": "Transferência para amigo",
  "relatedTransactionId": "423e4567-e89b-12d3-a456-426614174003",
  "createdAt": "2023-01-03T12:00:00"
}
```

## Configuração e Execução

### Pré-requisitos
- Java 21
- PostgreSQL 12+
- Gradle 8+

### Configuração do Banco de Dados
1. Crie um banco de dados PostgreSQL chamado `wallet`
2. Configure as credenciais no arquivo `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/wallet
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### Executando a Aplicação
```bash
./gradlew bootRun
```

### Executando os Testes
```bash
./gradlew test
```

## Considerações de Segurança

Este projeto é uma demonstração e não implementa autenticação e autorização. Em um ambiente de produção, seria necessário adicionar:

- Autenticação de usuários (OAuth2, JWT)
- Autorização baseada em papéis
- HTTPS para comunicação segura
- Validação adicional de entradas
- Auditoria de transações

## Contribuição

Contribuições são bem-vindas! Sinta-se à vontade para abrir issues ou enviar pull requests com melhorias.
