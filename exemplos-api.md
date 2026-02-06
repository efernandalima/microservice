# Exemplos de API - Microserviços Lab

## Serviço de Usuário (porta 8081)

### 1. Criar Usuário
```bash
curl -X POST http://localhost:8081/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "cpf": "98765432100",
    "email": "maria@example.com",
    "telefone": "11912345678"
  }'
```

**Resposta:**
```json
{
  "id": 1,
  "nome": "Maria Santos",
  "cpf": "98765432100",
  "email": "maria@example.com",
  "telefone": "11912345678",
  "criadoEm": "2024-02-04T00:00:00",
  "atualizadoEm": "2024-02-04T00:00:00"
}
```

### 2. Buscar Usuário por ID
```bash
curl http://localhost:8081/api/usuarios/1
```

### 3. Listar Todos os Usuários
```bash
curl http://localhost:8081/api/usuarios
```

### 4. Atualizar Usuário
```bash
curl -X PUT http://localhost:8081/api/usuarios/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos Silva",
    "cpf": "98765432100",
    "email": "maria.silva@example.com",
    "telefone": "11912345678"
  }'
```

### 5. Deletar Usuário
```bash
curl -X DELETE http://localhost:8081/api/usuarios/1
```

### 6. Health Check
```bash
curl http://localhost:8081/health
```

---

## Serviço de Pedidos (porta 8082)

### 1. Criar Pedido
```bash
curl -X POST http://localhost:8082/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "nomeProduto": "Notebook Dell Inspiron",
    "quantidade": 1,
    "precoTotal": 3500.00,
    "status": "PENDENTE"
  }'
```

**Resposta:**
```json
{
  "id": 1,
  "usuarioId": 1,
  "nomeProduto": "Notebook Dell Inspiron",
  "quantidade": 1,
  "precoTotal": 3500.00,
  "status": "PENDENTE",
  "criadoEm": "2024-02-04T00:00:00",
  "atualizadoEm": "2024-02-04T00:00:00"
}
```

### 2. Buscar Pedido por ID
```bash
curl http://localhost:8082/api/pedidos/1
```

### 3. Listar Todos os Pedidos
```bash
curl http://localhost:8082/api/pedidos
```

### 4. Atualizar Pedido (Confirmar)
```bash
curl -X PUT http://localhost:8082/api/pedidos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "nomeProduto": "Notebook Dell Inspiron",
    "quantidade": 1,
    "precoTotal": 3500.00,
    "status": "CONFIRMADO"
  }'
```

### 5. Cancelar Pedido
```bash
curl -X PUT http://localhost:8082/api/pedidos/1 \
  -H "Content-Type: application/json" \
  -d '{
    "usuarioId": 1,
    "nomeProduto": "Notebook Dell Inspiron",
    "quantidade": 1,
    "precoTotal": 3500.00,
    "status": "CANCELADO"
  }'
```

### 6. Deletar Pedido
```bash
curl -X DELETE http://localhost:8082/api/pedidos/1
```

### 7. Health Check
```bash
curl http://localhost:8082/health
```

---

## Exemplos PowerShell (Windows)

### Criar Usuário
```powershell
$body = @{
    nome = "João Silva"
    cpf = "12345678901"
    email = "joao@example.com"
    telefone = "11987654321"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8081/api/usuarios" `
  -Method Post `
  -ContentType "application/json" `
  -Body $body
```

### Criar Pedido
```powershell
$body = @{
    usuarioId = 1
    nomeProduto = "Mouse Gamer"
    quantidade = 2
    precoTotal = 150.00
    status = "PENDENTE"
} | ConvertTo-Json

Invoke-RestMethod -Uri "http://localhost:8082/api/pedidos" `
  -Method Post `
  -ContentType "application/json" `
  -Body $body
```

---

## Fluxo Completo de Teste

```bash
# 1. Criar usuário
curl -X POST http://localhost:8081/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{"nome":"Ana Costa","cpf":"11122233344","email":"ana@example.com","telefone":"11999887766"}'

# 2. Listar usuários (pegar o ID retornado)
curl http://localhost:8081/api/usuarios

# 3. Criar pedido para o usuário
curl -X POST http://localhost:8082/api/pedidos \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":1,"nomeProduto":"Teclado Mecânico","quantidade":1,"precoTotal":450.00,"status":"PENDENTE"}'

# 4. Listar pedidos
curl http://localhost:8082/api/pedidos

# 5. Confirmar pedido
curl -X PUT http://localhost:8082/api/pedidos/1 \
  -H "Content-Type: application/json" \
  -d '{"usuarioId":1,"nomeProduto":"Teclado Mecânico","quantidade":1,"precoTotal":450.00,"status":"CONFIRMADO"}'

# 6. Verificar eventos no MongoDB
docker exec -it mongodb mongosh
use events
db.usuarioEvents.find().pretty()
db.pedidoEvents.find().pretty()
exit

# 7. Verificar cache no Redis
docker exec -it redis redis-cli
KEYS *
exit

# 8. Verificar dados criptografados no PostgreSQL
docker exec -it postgres psql -U postgres -d usuariodb
SELECT id, nome, cpf, email FROM usuarios;
\q
```

---

## Status Codes

- **200 OK**: Requisição bem-sucedida (GET, PUT)
- **201 Created**: Recurso criado com sucesso (POST)
- **204 No Content**: Recurso deletado com sucesso (DELETE)
- **400 Bad Request**: Dados inválidos
- **404 Not Found**: Recurso não encontrado
- **500 Internal Server Error**: Erro no servidor
