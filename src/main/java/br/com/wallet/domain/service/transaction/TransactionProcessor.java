package br.com.wallet.domain.service.transaction;

import br.com.wallet.domain.model.Transaction;

public interface TransactionProcessor {

    boolean canProcess(Transaction.TransactionType transactionType);

    Transaction process(Transaction transaction);
}