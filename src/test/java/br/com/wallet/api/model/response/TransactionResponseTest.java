package br.com.wallet.api.model.response;

import br.com.wallet.domain.model.Transaction;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TransactionResponseTest {

    @Test
    void shouldReplaceNullValuesWithDefaults() {
        // Given
        UUID id = UUID.randomUUID();
        UUID walletId = UUID.randomUUID();
        String walletName = "Test Wallet";
        BigDecimal amount = BigDecimal.valueOf(100);
        LocalDateTime createdAt = LocalDateTime.now();
        
        // When
        TransactionResponse response = new TransactionResponse(
                id, walletId, walletName, null, null, Transaction.TransactionType.DEPOSIT,
                amount, null, null, createdAt
        );
        
        // Then
        assertNotNull(response.id());
        assertNotNull(response.walletId());
        assertNotNull(response.walletName());
        assertNotNull(response.fromUserId());
        assertNotNull(response.toUserId());
        assertNotNull(response.type());
        assertNotNull(response.amount());
        assertNotNull(response.description());
        assertNotNull(response.relatedTransactionId());
        assertNotNull(response.createdAt());
        
        assertEquals(id, response.id());
        assertEquals(walletId, response.walletId());
        assertEquals(walletName, response.walletName());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), response.fromUserId());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), response.toUserId());
        assertEquals(Transaction.TransactionType.DEPOSIT, response.type());
        assertEquals(amount, response.amount());
        assertEquals("", response.description());
        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000000"), response.relatedTransactionId());
        assertEquals(createdAt, response.createdAt());
    }
}