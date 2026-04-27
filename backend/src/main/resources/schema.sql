-- ============================================================
-- APUF – Script SQL inicial (opcional – o JPA cria as tabelas)
-- Use este script apenas se precisar criar manualmente.
-- ============================================================

-- Extensão para UUID (opcional)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ── Tabela: usuarios ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS usuarios (
    id          BIGSERIAL PRIMARY KEY,
    nome        VARCHAR(255) NOT NULL,
    email       VARCHAR(255) NOT NULL UNIQUE,
    senha       VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'CLIENTE',
    telefone    VARCHAR(20),
    ativo       BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- ── Tabela: produtos ────────────────────────────────────────
CREATE TABLE IF NOT EXISTS produtos (
    id                BIGSERIAL PRIMARY KEY,
    nome              VARCHAR(255)    NOT NULL,
    descricao         TEXT,
    preco             NUMERIC(10, 2)  NOT NULL,
    categoria         VARCHAR(20)     NOT NULL,
    estoque           INTEGER         NOT NULL DEFAULT 0,
    unidade           VARCHAR(50)     DEFAULT 'unidade',
    imagem_url        VARCHAR(500),
    imagem_public_id  VARCHAR(255),
    ativo             BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- ── Tabela: pedidos ─────────────────────────────────────────
CREATE TABLE IF NOT EXISTS pedidos (
    id                  BIGSERIAL PRIMARY KEY,
    numero_pedido       VARCHAR(20) UNIQUE,
    cliente_nome        VARCHAR(255) NOT NULL,
    cliente_telefone    VARCHAR(20)  NOT NULL,
    cliente_rua         VARCHAR(255),
    cliente_numero      VARCHAR(20),
    cliente_bairro      VARCHAR(100),
    cliente_referencia  TEXT,
    usuario_id          BIGINT REFERENCES usuarios(id) ON DELETE SET NULL,
    total               NUMERIC(10, 2) NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    created_at          TIMESTAMP   NOT NULL DEFAULT NOW()
);

-- ── Tabela: itens_pedido ─────────────────────────────────────
CREATE TABLE IF NOT EXISTS itens_pedido (
    id               BIGSERIAL PRIMARY KEY,
    pedido_id        BIGINT         NOT NULL REFERENCES pedidos(id) ON DELETE CASCADE,
    produto_id       BIGINT         REFERENCES produtos(id) ON DELETE SET NULL,
    nome_produto     VARCHAR(255)   NOT NULL,
    quantidade       INTEGER        NOT NULL,
    preco_unitario   NUMERIC(10, 2) NOT NULL
);

-- ── Índices úteis ────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_produtos_categoria  ON produtos(categoria);
CREATE INDEX IF NOT EXISTS idx_produtos_ativo      ON produtos(ativo);
CREATE INDEX IF NOT EXISTS idx_pedidos_status      ON pedidos(status);
CREATE INDEX IF NOT EXISTS idx_pedidos_created_at  ON pedidos(created_at);
CREATE INDEX IF NOT EXISTS idx_pedidos_usuario     ON pedidos(usuario_id);

-- ── Admin padrão (senha: apuf2025) ───────────────────────────
-- ATENÇÃO: troque a senha em produção!
-- Hash BCrypt de "apuf2025":
INSERT INTO usuarios (nome, email, senha, role, ativo)
VALUES (
    'Administrador APUF',
    'admin@apuf.com.br',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- apuf2025
    'ADMIN',
    TRUE
) ON CONFLICT (email) DO NOTHING;
