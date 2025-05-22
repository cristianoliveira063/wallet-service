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

@Component
@RequiredArgsConstructor
public class DepositProcessor implements TransactionProcessor {

    private final TransactionRepository transactionRepository;
    private final TransactionValidator transactionValidator;
    private final BalanceManager balanceManager;
    private final UserWalletFinder userWalletFinder;

    @Override
    public boolean canProcess(Transaction.TransactionType transactionType) {
        return Transaction.TransactionType.DEPOSIT.equals(transactionType);
    }

    @Override
    @Transactional
    public Transaction process(Transaction transaction) {
        transactionValidator.validateTransactionType(transaction, Transaction.TransactionType.DEPOSIT);
        transactionValidator.validateAmount(transaction.getAmount());

        UserWallet userWallet = userWalletFinder.getUserWalletWithLockOrThrow(
                transaction.getToUserId(),
                transaction.getWallet().getId(),
                "UserWallet not found with userId: "
        );

        balanceManager.creditUserWallet(userWallet, transaction.getAmount());
        transaction.setRelatedTransaction(null);

        return transactionRepository.save(transaction);
    }
}
