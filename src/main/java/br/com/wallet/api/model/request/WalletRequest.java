package br.com.wallet.api.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record WalletRequest(
    @NotBlank(message = "Wallet name is required")
    @Size(min = 3, max = 100, message = "Wallet name must be between 3 and 100 characters")
    String name
) {}
