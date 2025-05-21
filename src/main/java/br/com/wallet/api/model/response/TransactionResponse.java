package br.com.wallet.api.model.response;

import br.com.wallet.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(

        UUID id, UUID walletId, String walletName, UUID fromUserId, UUID toUserId, Transaction.TransactionType type,
        BigDecimal amount, String description, UUID relatedTransactionId, LocalDateTime createdAt
) {
}
