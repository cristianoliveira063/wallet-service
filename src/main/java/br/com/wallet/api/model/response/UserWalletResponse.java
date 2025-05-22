package br.com.wallet.api.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserWalletResponse(
        UUID id,
        UUID userId,
        UUID walletId,
        String walletName,
        BigDecimal balance,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}