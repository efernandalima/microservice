-- Criação da tabela de usuários
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(200) NOT NULL,
    cpf VARCHAR(500) NOT NULL UNIQUE,
    email VARCHAR(500) NOT NULL UNIQUE,
    telefone VARCHAR(500) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL
);

-- Índices para melhor performance
CREATE INDEX idx_usuarios_cpf ON usuarios(cpf);
CREATE INDEX idx_usuarios_email ON usuarios(email);

-- Comentários
COMMENT ON TABLE usuarios IS 'Tabela de usuários do sistema';
COMMENT ON COLUMN usuarios.cpf IS 'CPF criptografado com AES-256-GCM';
COMMENT ON COLUMN usuarios.email IS 'Email criptografado com AES-256-GCM';
COMMENT ON COLUMN usuarios.telefone IS 'Telefone criptografado com AES-256-GCM';
