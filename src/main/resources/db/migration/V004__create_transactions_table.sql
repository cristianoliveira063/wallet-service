CREATE TABLE transactions
(
    id                     UUID PRIMARY KEY,
    wallet_id              UUID           NOT NULL,
    from_user_id           UUID,
    to_user_id             UUID,
    type                   VARCHAR(20)    NOT NULL CHECK (type IN ('DEPOSIT', 'WITHDRAW', 'TRANSFER')),
    amount                 NUMERIC(18, 2) NOT NULL CHECK (amount > 0),
    description            VARCHAR(255),
    related_transaction_id UUID,
    created_at             TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_transaction_wallet FOREIGN KEY (wallet_id) REFERENCES wallets (id),
    CONSTRAINT fk_related_transaction FOREIGN KEY (related_transaction_id) REFERENCES transactions (id)
);