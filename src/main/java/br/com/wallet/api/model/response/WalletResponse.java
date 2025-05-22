package br.com.wallet.api.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WalletResponse(
        UUID id,
        String name,
        LocalDateTime createdAt
) {
}
