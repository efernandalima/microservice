-- Criação da tabela de pedidos
CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    nome_produto VARCHAR(200) NOT NULL,
    quantidade INTEGER NOT NULL CHECK (quantidade > 0),
    preco_total DECIMAL(10, 2) NOT NULL CHECK (preco_total > 0),
    status VARCHAR(20) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL
);

-- Índices para melhor performance
CREATE INDEX idx_pedidos_usuario_id ON pedidos(usuario_id);
CREATE INDEX idx_pedidos_status ON pedidos(status);

-- Comentários
COMMENT ON TABLE pedidos IS 'Tabela de pedidos do sistema';
COMMENT ON COLUMN pedidos.usuario_id IS 'ID do usuário que fez o pedido';
COMMENT ON COLUMN pedidos.status IS 'Status do pedido: PENDENTE, CONFIRMADO, CANCELADO';
