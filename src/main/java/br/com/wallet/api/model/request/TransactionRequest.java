package br.com.wallet.api.model.request;

import br.com.wallet.domain.model.Transaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public record TransactionRequest(
        @NotNull(message = "Origin Wallet ID is required")
        UUID walletId,

        UUID destinationWalletId,

        UUID fromUserId,

        UUID toUserId,

        Transaction.TransactionType type,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        String description,

        UUID relatedTransactionId
) {

    public void validate() {
        Objects.requireNonNull(walletId, "Origin Wallet ID is required");
        Objects.requireNonNull(amount, "Amount is required");
        Objects.requireNonNull(type, "Transaction type is required");

        switch (type) {
            case DEPOSIT:
                validateDeposit();
                break;
            case WITHDRAW:
                validateWithdraw();
                break;
            case TRANSFER:
                validateTransfer();
                break;
            default:
                throw new IllegalArgumentException("Unsupported transaction type: " + type);
        }
    }

    private void validateDeposit() {
        Objects.requireNonNull(toUserId, "To user ID is required for deposits");

        if (Objects.nonNull(fromUserId)) {
            throw new IllegalArgumentException("From user ID should not be specified for deposits");
        }
        if (Objects.nonNull(destinationWalletId)) {
            throw new IllegalArgumentException("Destination Wallet ID should not be specified for deposits");
        }
    }

    private void validateWithdraw() {
        Objects.requireNonNull(fromUserId, "From user ID is required for withdrawals");

        if (Objects.nonNull(toUserId)) {
            throw new IllegalArgumentException("To user ID should not be specified for withdrawals");
        }
        if (Objects.nonNull(destinationWalletId)) {
            throw new IllegalArgumentException("Destination Wallet ID should not be specified for withdrawals");
        }
    }

    private void validateTransfer() {
        Objects.requireNonNull(fromUserId, "From user ID is required for transfers");
        Objects.requireNonNull(toUserId, "To user ID is required for transfers");
        Objects.requireNonNull(destinationWalletId, "Destination Wallet ID is required for transfers");

        if (walletId.equals(destinationWalletId)) {
            throw new IllegalArgumentException("Origin and destination wallets must be different for transfers");
        }
    }
}
