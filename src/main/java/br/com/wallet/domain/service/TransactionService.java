package br.com.wallet.domain.service;

import br.com.wallet.domain.model.Transaction;
import br.com.wallet.domain.model.Wallet;
import br.com.wallet.domain.repository.TransactionRepository;
import br.com.wallet.domain.service.transaction.TransactionProcessor;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final WalletService walletService;
    private final List<TransactionProcessor> transactionProcessors;

    @Transactional
    public Transaction processTransactionWithWallet(Transaction transaction, UUID walletId, UnaryOperator<Transaction> transactionProcessor) {
        Objects.requireNonNull(transaction, "Transaction cannot be null");
        Objects.requireNonNull(walletId, "Wallet ID cannot be null");
        Objects.requireNonNull(transactionProcessor, "Transaction processor cannot be null");
        Wallet wallet = findWalletById(walletId);
        transaction.setWallet(wallet);
        return transactionProcessor.apply(transaction);
    }

    @Transactional
    public Transaction deposit(Transaction transaction) {
        return getProcessorForType(Transaction.TransactionType.DEPOSIT).process(transaction);
    }

    @Transactional
    public Transaction withdraw(Transaction transaction) {
        return getProcessorForType(Transaction.TransactionType.WITHDRAW).process(transaction);
    }

    @Transactional
    public Transaction transfer(Transaction transaction) {
        return getProcessorForType(Transaction.TransactionType.TRANSFER).process(transaction);
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction findById(UUID id) {
        Objects.requireNonNull(id, "Transaction ID cannot be null");
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
    }

    private TransactionProcessor getProcessorForType(Transaction.TransactionType transactionType) {
        Objects.requireNonNull(transactionType, "Transaction type cannot be null");
        return transactionProcessors.stream()
                .filter(processor -> processor.canProcess(transactionType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No processor found for transaction type: " + transactionType));
    }

    private Wallet findWalletById(UUID walletId) {
        return walletService.findById(walletId);
    }
}
