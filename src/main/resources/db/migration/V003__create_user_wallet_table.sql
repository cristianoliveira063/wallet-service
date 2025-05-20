CREATE TABLE user_wallet
(
    id         UUID PRIMARY KEY,
    user_id    UUID           NOT NULL,
    wallet_id  UUID           NOT NULL,
    balance    NUMERIC(18, 2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP               DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_user_wallet UNIQUE (user_id, wallet_id),
    CONSTRAINT fk_user_wallet_wallet FOREIGN KEY (wallet_id) REFERENCES wallets (id)
);
