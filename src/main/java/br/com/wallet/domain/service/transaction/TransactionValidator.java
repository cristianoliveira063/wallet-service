package br.com.wallet.domain.service.transaction;

import br.com.wallet.domain.model.Transaction;
import br.com.wallet.domain.model.UserWallet;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
public class TransactionValidator {

    public void validateTransactionType(Transaction transaction, Transaction.TransactionType expectedType) {
        Objects.requireNonNull(transaction, "Transaction cannot be null");
        Objects.requireNonNull(expectedType, "Expected transaction type cannot be null");
        if (transaction.getType() != expectedType) {
            throw new IllegalArgumentException("Transaction type must be " + expectedType);
        }
    }

    public void validateAmount(BigDecimal amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    public void validateSufficientBalance(UserWallet userWallet, BigDecimal amount) {
        Objects.requireNonNull(userWallet, "User wallet cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");
        if (userWallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

    public void validateTransferUsers(Transaction transaction) {
        Objects.requireNonNull(transaction, "Transaction cannot be null");
        Objects.requireNonNull(transaction.getFromUserId(), "From user ID is required for transfers");
        Objects.requireNonNull(transaction.getToUserId(), "To user ID is required for transfers");
    }
}
