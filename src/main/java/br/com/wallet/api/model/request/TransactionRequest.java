package br.com.wallet.api.model.request;

import br.com.wallet.domain.model.Transaction;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Request DTO for transaction operations.
 * Contains all necessary fields for creating deposits, withdrawals, and transfers.
 */
public record TransactionRequest(
        @NotNull(message = "Wallet ID is required")
        UUID walletId,

        UUID fromUserId,

        UUID toUserId,

        @NotNull(message = "Transaction type is required")
        Transaction.TransactionType type,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        String description,

        UUID relatedTransactionId
) {

    public void validate() {
        Objects.requireNonNull(walletId, "Wallet ID is required");
        Objects.requireNonNull(amount, "Amount is required");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

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
    }

    private void validateWithdraw() {
        Objects.requireNonNull(fromUserId, "From user ID is required for withdrawals");

        if (Objects.nonNull(toUserId)) {
            throw new IllegalArgumentException("To user ID should not be specified for withdrawals");
        }
    }

    private void validateTransfer() {
        Objects.requireNonNull(fromUserId, "From user ID is required for transfers");
        Objects.requireNonNull(toUserId, "To user ID is required for transfers");

        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("From user ID and To user ID cannot be the same for transfers");
        }
    }
}
