CREATE TABLE balance_history
(
    id          UUID PRIMARY KEY,
    user_id     UUID           NOT NULL,
    wallet_id   UUID           NOT NULL,
    balance     NUMERIC(18, 2) NOT NULL,
    recorded_at TIMESTAMP      NOT NULL
);

CREATE INDEX idx_balance_history_user_wallet
    ON balance_history (user_id, wallet_id);