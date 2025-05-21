package br.com.wallet.api.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserWalletResponse(
    UUID id,
    UUID userId,
    UUID walletId,
    String walletName,
    BigDecimal balance,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}