-- Colunas para busca deterministíca de unicidade em campos criptografados com AES-GCM.
-- AES-GCM usa IV aleatório por cifragem, tornando existsByCpf/existsByEmail ineficazes.
-- O HMAC-SHA256 é determinístico (mesmo input → mesmo hash) e permite lookup sem
-- expor o valor em plaintext.
ALTER TABLE usuarios ADD COLUMN cpf_hash VARCHAR(44);
ALTER TABLE usuarios ADD COLUMN email_hash VARCHAR(44);

-- Índice único parcial: ignora o valor padrão nulo/vazio de registros legados.
CREATE UNIQUE INDEX idx_usuarios_cpf_hash   ON usuarios(cpf_hash)   WHERE cpf_hash IS NOT NULL;
CREATE UNIQUE INDEX idx_usuarios_email_hash ON usuarios(email_hash) WHERE email_hash IS NOT NULL;
