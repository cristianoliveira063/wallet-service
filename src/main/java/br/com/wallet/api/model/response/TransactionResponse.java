package br.com.wallet.api.model.response;

import br.com.wallet.domain.model.Transaction;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record TransactionResponse(

        UUID id, UUID walletId, String walletName, UUID fromUserId, UUID toUserId, Transaction.TransactionType type,
        BigDecimal amount, String description, UUID relatedTransactionId, LocalDateTime createdAt
) {

}
