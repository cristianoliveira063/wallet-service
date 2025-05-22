package br.com.wallet.domain.service;

import br.com.wallet.domain.model.BalanceHistory;
import br.com.wallet.domain.model.Transaction;
import br.com.wallet.domain.model.UserWallet;
import br.com.wallet.domain.model.Wallet;
import br.com.wallet.domain.repository.BalanceHistoryRepository;
import br.com.wallet.domain.repository.TransactionRepository;
import br.com.wallet.domain.repository.UserWalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserWalletRepository userWalletRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final WalletService walletService;

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
        validateTransactionType(transaction, Transaction.TransactionType.DEPOSIT);
        validateAmount(transaction.getAmount());

        Optional<UserWallet> optionalUserWallet = userWalletRepository.findByUserIdAndWalletId(
                transaction.getToUserId(), transaction.getWallet().getId());

        if (optionalUserWallet.isEmpty()) {
            throw new EntityNotFoundException("UserWallet not found with userId: " +
                    transaction.getToUserId() + " and walletId: " + transaction.getWallet().getId());
        }

        UserWallet userWallet = optionalUserWallet.get();
        updateWalletBalance(userWallet, transaction.getAmount(), true);

        transaction.setRelatedTransaction(null);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction withdraw(Transaction transaction) {
        validateTransactionType(transaction, Transaction.TransactionType.WITHDRAW);
        validateAmount(transaction.getAmount());

        Optional<UserWallet> optionalUserWallet = userWalletRepository.findByUserIdAndWalletId(
                transaction.getFromUserId(), transaction.getWallet().getId());

        if (optionalUserWallet.isEmpty()) {
            throw new EntityNotFoundException("UserWallet not found with userId: " +
                    transaction.getFromUserId() + " and walletId: " + transaction.getWallet().getId());
        }

        UserWallet userWallet = optionalUserWallet.get();
        validateSufficientBalance(userWallet, transaction.getAmount());

        updateWalletBalance(userWallet, transaction.getAmount(), false);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction transfer(Transaction transaction) {
        validateTransactionType(transaction, Transaction.TransactionType.TRANSFER);
        validateAmount(transaction.getAmount());
        validateTransferUsers(transaction);

        Optional<UserWallet> optionalSourceUserWallet = userWalletRepository.findByUserIdAndWalletId(
                transaction.getFromUserId(), transaction.getWallet().getId());

        if (optionalSourceUserWallet.isEmpty()) {
            throw new EntityNotFoundException("Source UserWallet not found with userId: " +
                    transaction.getFromUserId() + " and walletId: " + transaction.getWallet().getId());
        }

        UserWallet sourceUserWallet = optionalSourceUserWallet.get();
        validateSufficientBalance(sourceUserWallet, transaction.getAmount());

        Optional<UserWallet> optionalTargetUserWallet = userWalletRepository.findByUserIdAndWalletId(
                transaction.getToUserId(), transaction.getWallet().getId());

        if (optionalTargetUserWallet.isEmpty()) {
            throw new EntityNotFoundException("Target UserWallet not found with userId: " +
                    transaction.getToUserId() + " and walletId: " + transaction.getWallet().getId());
        }

        UserWallet targetUserWallet = optionalTargetUserWallet.get();

        updateWalletBalance(sourceUserWallet, transaction.getAmount(), false);
        updateWalletBalance(targetUserWallet, transaction.getAmount(), true);

        Transaction savedSourceTransaction = transactionRepository.save(transaction);

        Transaction targetTransaction = createRelatedTransaction(transaction, savedSourceTransaction);
        Transaction savedTargetTransaction = transactionRepository.save(targetTransaction);

        savedSourceTransaction.setRelatedTransaction(savedTargetTransaction);
        transactionRepository.save(savedSourceTransaction);

        return savedSourceTransaction;
    }

    public List<Transaction> findAll() {
        return transactionRepository.findAll();
    }

    public Transaction findById(UUID id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
    }

    private void validateTransactionType(Transaction transaction, Transaction.TransactionType expectedType) {
        Objects.requireNonNull(transaction, "Transaction cannot be null");
        Objects.requireNonNull(expectedType, "Expected transaction type cannot be null");

        if (transaction.getType() != expectedType) {
            throw new IllegalArgumentException("Transaction type must be " + expectedType);
        }
    }

    private void validateAmount(BigDecimal amount) {
        Objects.requireNonNull(amount, "Amount cannot be null");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
    }

    private void validateSufficientBalance(UserWallet userWallet, BigDecimal amount) {
        Objects.requireNonNull(userWallet, "User wallet cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");

        if (userWallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance");
        }
    }

    private void validateTransferUsers(Transaction transaction) {
        Objects.requireNonNull(transaction, "Transaction cannot be null");
        Objects.requireNonNull(transaction.getFromUserId(), "From user ID is required for transfers");
        Objects.requireNonNull(transaction.getToUserId(), "To user ID is required for transfers");
    }

    private void updateWalletBalance(UserWallet userWallet, BigDecimal amount, boolean isCredit) {
        Objects.requireNonNull(userWallet, "User wallet cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");

        if (isCredit) {
            userWallet.setBalance(userWallet.getBalance().add(amount));
        } else {
            userWallet.setBalance(userWallet.getBalance().subtract(amount));
        }
        userWalletRepository.save(userWallet);

        recordBalanceHistory(userWallet);
    }

    private Transaction createRelatedTransaction(Transaction sourceTransaction, Transaction savedSourceTransaction) {
        Objects.requireNonNull(sourceTransaction, "Source transaction cannot be null");
        Objects.requireNonNull(savedSourceTransaction, "Saved source transaction cannot be null");

        return Transaction.builder()
                .wallet(sourceTransaction.getWallet())
                .fromUserId(sourceTransaction.getFromUserId())
                .toUserId(sourceTransaction.getToUserId())
                .type(Transaction.TransactionType.TRANSFER)
                .amount(sourceTransaction.getAmount())
                .description(sourceTransaction.getDescription())
                .relatedTransaction(savedSourceTransaction)
                .build();
    }

    private void recordBalanceHistory(UserWallet userWallet) {
        Objects.requireNonNull(userWallet, "User wallet cannot be null");
        Objects.requireNonNull(userWallet.getWallet(), "Wallet cannot be null");

        BalanceHistory balanceHistory = BalanceHistory.builder()
                .userId(userWallet.getUserId())
                .wallet(userWallet.getWallet().getId())
                .balance(userWallet.getBalance())
                .build();

        balanceHistoryRepository.save(balanceHistory);
    }

    private Wallet findWalletById(UUID walletId) {
        return walletService.findById(walletId);
    }
}
