package br.com.wallet.api.model.request;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserWalletRequest(
    @NotNull(message = "User ID is required")
    UUID userId,
    
    @NotNull(message = "Wallet ID is required")
    UUID walletId
) {}