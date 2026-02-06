# Microserviços Lab - Java/Spring Boot

Projeto completo de microserviços com Java 21, Spring Boot 3.x, observabilidade e segurança.

## 📋 Visão Geral

Este projeto demonstra uma arquitetura de microserviços moderna com:
- **2 Microserviços**: Serviço de Usuário (8081) e Serviço de Pedidos (8082)
- **Autenticação JWT**: Spring Security + JWT para proteção das APIs
- **Criptografia AES-256-GCM**: Dados sensíveis criptografados
- **Cache Redis**: Performance otimizada com serialização LocalDateTime
- **Eventos MongoDB**: Auditoria completa
- **GlobalExceptionHandler**: Tratamento de erros estruturado (JSON)
- **Logs estruturados**: Logback + Logstash (ELK Stack)
- **Documentação API**: Swagger/OpenAPI
- **Testes Unitários**: 14 testes com JUnit 5 e Mockito

## 🏗️ Arquitetura

```
┌─────────────────┐     ┌─────────────────┐
│ Serviço Usuário │     │ Serviço Pedidos │
│    (8081)       │     │    (8082)       │
└────────┬────────┘     └────────┬────────┘
         │                       │
         ├───────────┬───────────┤
         │           │           │
    ┌────▼────┐ ┌───▼───┐ ┌────▼─────┐
    │PostgreSQL│ │ Redis │ │ MongoDB  │
    └─────────┘ └───────┘ └──────────┘
         │           │           │
         └───────────┴───────────┘
                     │
         ┌───────────┴───────────┐
         │                       │
    ┌────▼────┐            ┌────▼────┐
    │   ELK   │            │ Jaeger  │
    │  Stack  │            │ + OTel  │
    └─────────┘            └─────────┘
```

## 🚀 Tecnologias

- **Java 21**
- **Spring Boot 3.2.2**
- **PostgreSQL 16** - Dados relacionais
- **Redis 7** - Cache
- **MongoDB 7** - Eventos/Auditoria
- **Elasticsearch 8.11** - Armazenamento de logs
- **Kibana 8.11** - Visualização de logs
- **Logstash 8.11** - Pipeline de logs
- **Jaeger 1.52** - Distributed tracing
- **OpenTelemetry** - Observabilidade
- **Flyway** - Migrations de banco
- **Swagger/OpenAPI** - Documentação de API
- **Docker & Docker Compose** - Containerização

## 📦 Pré-requisitos

- **Docker** e **Docker Compose**
- **Java 21** (para desenvolvimento local)
- **Maven 3.9+** (para build local)

## 🔧 Instalação e Execução

### 1. Clone o repositório
```bash
cd c:\Users\Fer\Desktop\microservice
```

### 2. Inicie toda a infraestrutura
```bash
cd infra
docker-compose up -d
```

Isso irá iniciar:
- PostgreSQL (porta 5432)
- Redis (porta 6379)
- MongoDB (porta 27017)
- Elasticsearch (porta 9200)
- Kibana (porta 5601)
- Logstash (porta 5000)
- Jaeger UI (porta 16686)
- Serviço de Usuário (porta 8081)
- Serviço de Pedidos (porta 8082)

### 3. Verifique o status
```bash
docker-compose ps
```

### 4. Verifique health dos serviços
```bash
curl http://localhost:8081/health
curl http://localhost:8082/health
```

### Modo Desenvolvimento (sem Docker para os serviços)

Se preferir rodar os serviços localmente com Maven:

```bash
# Terminal 1 - Subir apenas infraestrutura
cd infra
docker-compose up -d postgres redis mongodb

# Terminal 2 - Rodar servico-usuario
mvn spring-boot:run -pl servico-usuario

# Terminal 3 - Rodar servico-pedido
mvn spring-boot:run -pl servico-pedido
```

## 📚 Documentação da API

### Swagger UI
- **Serviço de Usuário**: http://localhost:8081/swagger-ui.html
- **Serviço de Pedidos**: http://localhost:8082/swagger-ui.html

### OpenAPI JSON
- **Serviço de Usuário**: http://localhost:8081/api-docs
- **Serviço de Pedidos**: http://localhost:8082/api-docs

## 🔐 Segurança - Criptografia

Os campos sensíveis (CPF, email, telefone) são criptografados usando **AES-256-GCM** antes de serem salvos no banco de dados.

### Configurar chave de criptografia

A chave padrão é apenas para desenvolvimento. Para produção, gere uma chave segura:

```bash
# Gerar chave de 256 bits em Base64
openssl rand -base64 32
```

Configure a variável de ambiente:
```bash
export ENCRYPTION_KEY=sua_chave_aqui
```

Ou edite o `docker-compose.yml`:
```yaml
environment:
  - ENCRYPTION_KEY=sua_chave_aqui
```

## 🧪 Testando a API

### Criar Usuário
```bash
curl -X POST http://localhost:8081/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João da Silva",
    "cpf": "12345678901",
    "email": "joao@example.com",
    "telefone": "11987654321"
  }'
```

### Listar Usuários
```bash
curl http://localhost:8081/api/usuarios
```

### Criar Pedido
```bash
curl -X POST http://localhost:8082/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "nomeProduto": "Notebook Dell",
    "quantidade": 2,
    "precoTotal": 2500.00,
    "status": "PENDENTE"
  }'
```

### Listar Pedidos
```bash
curl http://localhost:8082/api/pedidos
```

### Testar tratamento de erro (GlobalExceptionHandler)
```bash
# Requisição inválida - retorna JSON estruturado
curl -X POST http://localhost:8081/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{}'

# Resposta esperada:
# {
#   "timestamp": "2026-02-04T23:30:00",
#   "status": 400,
#   "error": "Validation Error",
#   "message": "Erro de validação nos campos",
#   "fieldErrors": [...]
# }
```

## 📊 Observabilidade

### Logs no Kibana
1. Acesse: http://localhost:5601
2. Vá em **Management** → **Stack Management** → **Index Patterns**
3. Crie um index pattern: `microservices-logs-*`
4. Vá em **Discover** para visualizar os logs

### Traces no Jaeger
1. Acesse: http://localhost:16686
2. Selecione o serviço: `servico-usuario` ou `servico-pedido`
3. Clique em **Find Traces**

### Verificar Cache Redis
```bash
docker exec -it redis redis-cli
KEYS *
GET usuarios::1
```

### Verificar Eventos MongoDB
```bash
docker exec -it mongodb mongosh
use events
db.usuarioEvents.find().pretty()
db.pedidoEvents.find().pretty()
```

### Verificar Dados Criptografados
```bash
docker exec -it postgres psql -U postgres -d usuariodb
SELECT id, nome, cpf, email FROM usuarios;
```

Os campos `cpf`, `email` e `telefone` aparecerão como strings Base64 criptografadas.

## 🛠️ Comandos Úteis

### Parar todos os serviços
```bash
cd infra
docker-compose down
```

### Ver logs de um serviço específico
```bash
docker-compose logs -f servico-usuario
docker-compose logs -f servico-pedido
```

### Rebuild de um serviço
```bash
docker-compose up -d --build servico-usuario
```

### Limpar volumes (CUIDADO: apaga dados)
```bash
docker-compose down -v
```

## 📁 Estrutura do Projeto

```
microservice/
├── pom.xml                          # POM raiz
├── servico-usuario/                 # Microserviço de Usuário
│   ├── src/main/java/
│   │   └── lab/microservices/usuario/
│   │       ├── domain/              # Entidades JPA
│   │       ├── repo/                # Repositories
│   │       ├── service/             # Lógica de negócio
│   │       ├── api/                 # Controllers REST
│   │       │   └── dto/             # DTOs
│   │       ├── events/              # Eventos MongoDB
│   │       └── crypto/              # Criptografia AES
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── logback-spring.xml
│   │   └── db/migration/            # Flyway migrations
│   ├── Dockerfile
│   └── pom.xml
├── servico-pedido/                  # Microserviço de Pedidos
│   └── (mesma estrutura)
└── infra/                           # Infraestrutura
    ├── docker-compose.yml
    ├── postgres/
    │   └── init-multiple-dbs.sh
    ├── logstash/pipeline/
    │   └── logstash.conf
    └── otel/
        └── otel-collector.yaml
```

## ✨ Componentes de Qualidade

### GlobalExceptionHandler
Cada serviço possui um handler global que captura exceções e retorna respostas JSON estruturadas:
- `MethodArgumentNotValidException` → 400 Bad Request com lista de erros
- `IllegalArgumentException` → 400 Bad Request
- `Exception` → 500 Internal Server Error (sem expor stack trace)

### CacheConfig
Configuração customizada do Redis cache com:
- Serialização JSON para suporte a `LocalDateTime`
- TTL de 10 minutos por padrão
- Não cacheia valores nulos

## 🐛 Troubleshooting

### Serviços não iniciam
```bash
# Verifique logs
docker-compose logs

# Verifique se as portas estão disponíveis
netstat -an | findstr "8081 8082 5432 6379 27017"
```

### Erro de conexão com banco de dados
```bash
# Aguarde o PostgreSQL inicializar completamente
docker-compose logs postgres

# Reinicie os serviços de aplicação
docker-compose restart servico-usuario servico-pedido
```

### Elasticsearch não inicia
```bash
# Aumentar vm.max_map_count (Linux/WSL)
sudo sysctl -w vm.max_map_count=262144
```

## 📝 Licença

Este projeto é um exemplo educacional para demonstração de arquitetura de microserviços.

## 👨‍💻 Autor

Desenvolvido como projeto de demonstração técnica.

---

**Endpoints Principais:**
- Serviço Usuário: http://localhost:8081
- Serviço Pedidos: http://localhost:8082
- Kibana: http://localhost:5601
- Jaeger: http://localhost:16686
