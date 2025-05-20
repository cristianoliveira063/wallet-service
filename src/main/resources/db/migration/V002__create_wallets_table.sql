CREATE TABLE wallets
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_wallet_name UNIQUE (name)
);