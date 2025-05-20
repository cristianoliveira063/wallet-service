package br.com.wallet.api.model.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record WalletResponse(
    UUID id,
    String name,
    LocalDateTime createdAt
) {}
