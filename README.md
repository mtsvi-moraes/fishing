# Sistema Anti-Fraude Itaú

Sistema para detecção e prevenção de fraudes bancárias, desenvolvido como projeto para o Itaú. O projeto é composto por uma API principal em Java (Spring Boot) e um backend auxiliar em Python que utiliza IA para análise de e-mails suspeitos.

## Estrutura do Projeto

- **Java (Spring Boot)**: API principal para gerenciamento e exposição dos endpoints REST com documentação Swagger/OpenAPI.
- **Python Backend**: Serviço Flask que utiliza o modelo Gemini AI para detectar spam em e-mails.
- **PostgreSQL**: Banco de dados relacional para persistência dos dados.
- **Docker**: Facilita a execução dos serviços em containers.

## Como Executar

### 1. Pré-requisitos

- Docker e Docker Compose instalados
- Java 21+ (caso queira rodar sem Docker)
- Python 3.12+ (caso queira rodar o backend Python localmente)

### 2. Configuração de Variáveis de Ambiente

- Edite o arquivo `.env` na raiz para configurar o banco de dados.
- O backend Python utiliza `src/python-backend/.env` para a chave da API Gemini.

### 3. Subindo com Docker

```sh
docker compose up
```

- A aplicação Java estará disponível em `http://localhost:8080`
- O backend Python (se rodar manualmente) estará em `http://localhost:5000`
- O banco de dados PostgreSQL estará em `localhost:5432`
- Documentação Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### 4. Rodando o Backend Python manualmente

Entre na pasta `src/python-backend` e execute:

```sh
pip install -r requirements.txt
python app.py
```

### 5. Testando a API

- Acesse `http://localhost:8080/` para verificar se a API Java está rodando.
- Acesse `http://localhost:5000/` para testar a análise de e-mails via IA.
- Acesse `http://localhost:8080/swagger-ui/index.html` para a documentação interativa da API.

## Funcionalidades da API

### Endpoints de Análise
- **POST /api/alerts/fetch**: Busca análise de email de exemplo do serviço Python
- **POST /api/alerts/analyze**: Analisa conteúdo de email fornecido para detectar spam/phishing

### Endpoints de Consulta
- **GET /api/alerts**: Lista todos os alertas cadastrados
- **GET /api/alerts/{id}**: Busca alerta específico por ID
- **GET /api/alerts/spam**: Lista apenas alertas identificados como spam
- **GET /api/alerts/legitimate**: Lista apenas alertas legítimos
- **GET /api/alerts/high-confidence**: Lista alertas com alta confiança (>0.8)
- **GET /api/alerts/count**: Retorna contagem total de alertas
- **GET /api/alerts/count/spam**: Retorna contagem de alertas de spam
- **GET /api/alerts/health**: Health check do controlador

### Endpoints de Atualização
- **PUT /api/alerts/{id}**: Atualiza alerta completo
- **PATCH /api/alerts/{id}/spam-status**: Atualiza apenas status de spam
- **PATCH /api/alerts/{id}/confidence**: Atualiza apenas nível de confiança
- **PATCH /api/alerts/{id}/subject**: Atualiza apenas assunto do email
- **PUT /api/alerts/bulk-update-spam**: Atualização em lote do status de spam

### Endpoints de Exclusão
- **DELETE /api/alerts/{id}**: Exclui alerta específico
- **DELETE /api/alerts/bulk-delete**: Exclusão em lote de alertas
- **DELETE /api/alerts/spam**: Exclui todos os alertas de spam
- **DELETE /api/alerts/low-confidence**: Exclui alertas com baixa confiança

### Backend Python
- **GET /**: Analisa email de exemplo e retorna classificação
- **POST /analyze**: Analisa conteúdo de email fornecido
- **GET /api/hello** e **GET /api/goodbye**: Endpoints de teste

## Tecnologias Utilizadas

- **Backend**: Java 21, Spring Boot 3.x
- **IA/ML**: Python 3.12, Flask, Google Gemini AI
- **Banco de Dados**: PostgreSQL
- **Documentação**: Swagger/OpenAPI 3
- **Containerização**: Docker, Docker Compose
- **Dependências Java**: Spring Data JPA, Spring Web, SpringDoc OpenAPI

## Estrutura do Banco de Dados

### Tabela Alert
- `id`: Identificador único (BIGINT, AUTO_INCREMENT)
- `confidence`: Nível de confiança da detecção (DOUBLE, 0.0-1.0)
- `is_spam`: Booleano indicando se é spam (BOOLEAN)
- `subject`: Assunto do email (VARCHAR)
- `email_content`: Conteúdo completo do email (TEXT)
- `time_detected`: Timestamp da detecção (TIMESTAMP)

## Documentação da API

A API possui documentação completa via Swagger/OpenAPI disponível em:
- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`

A documentação inclui:
- Descrição detalhada de todos os endpoints
- Exemplos de requisições e respostas
- Códigos de status HTTP
- Interface interativa para testar endpoints

## Observações

- O backend Python depende de uma chave válida da API Gemini configurada no arquivo `.env`.
- O sistema utiliza comunicação REST entre os serviços Java e Python.
- Todos os endpoints possuem tratamento de erros e retornam códigos HTTP apropriados.
- A API suporta operações CRUD completas e operações em lote para eficiência.
- O banco de dados PostgreSQL garante persistência e integridade dos dados.

---

