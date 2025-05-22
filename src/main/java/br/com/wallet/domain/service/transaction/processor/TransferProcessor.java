package br.com.wallet.domain.service.transaction.processor;

import br.com.wallet.domain.model.Transaction;
import br.com.wallet.domain.model.UserWallet;
import br.com.wallet.domain.repository.TransactionRepository;
import br.com.wallet.domain.service.transaction.BalanceManager;
import br.com.wallet.domain.service.transaction.TransactionProcessor;
import br.com.wallet.domain.service.transaction.TransactionValidator;
import br.com.wallet.domain.service.transaction.UserWalletFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransferProcessor implements TransactionProcessor {

    private final TransactionRepository transactionRepository;
    private final TransactionValidator transactionValidator;
    private final BalanceManager balanceManager;
    private final UserWalletFinder userWalletFinder;

    @Override
    public boolean canProcess(Transaction.TransactionType transactionType) {
        return Transaction.TransactionType.TRANSFER.equals(transactionType);
    }

    @Override
    @Transactional
    public Transaction process(Transaction transaction) {
        transactionValidator.validateTransactionType(transaction, Transaction.TransactionType.TRANSFER);
        transactionValidator.validateAmount(transaction.getAmount());
        transactionValidator.validateTransferUsers(transaction);

        UserWallet sourceUserWallet = userWalletFinder.getUserWalletWithLockOrThrow(
                transaction.getFromUserId(),
                transaction.getWallet().getId(),
                "Source UserWallet not found with userId: "
        );

        transactionValidator.validateSufficientBalance(sourceUserWallet, transaction.getAmount());

        UUID destinationWalletId = transaction.getDestinationWalletId();
        if (destinationWalletId == null) {
            throw new IllegalArgumentException("Destination wallet ID is required for transfers");
        }

        // Get target user wallet with pessimistic lock to prevent concurrent modifications
        UserWallet targetUserWallet = userWalletFinder.getUserWalletWithLockOrThrow(
                transaction.getToUserId(),
                destinationWalletId,
                "Target UserWallet not found with userId: "
        );

        balanceManager.debitUserWallet(sourceUserWallet, transaction.getAmount());
        balanceManager.creditUserWallet(targetUserWallet, transaction.getAmount());

        Transaction savedSourceTransaction = transactionRepository.save(transaction);
        Transaction targetTransaction = createRelatedTransaction(transaction, savedSourceTransaction);
        Transaction savedTargetTransaction = transactionRepository.save(targetTransaction);

        savedSourceTransaction.setRelatedTransaction(savedTargetTransaction);
        return transactionRepository.save(savedSourceTransaction);
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
                .destinationWalletId(sourceTransaction.getDestinationWalletId())
                .build();
    }
}
