package br.com.wallet.api.assembler;

import br.com.wallet.api.model.request.TransactionRequest;
import br.com.wallet.api.model.response.TransactionResponse;
import br.com.wallet.core.mapper.TransactionMapper;
import br.com.wallet.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TransactionAssembler {

    private final TransactionMapper transactionMapper;

    public Transaction mapToTransactionEntityFromRequest(TransactionRequest transactionRequest) {
        return transactionMapper.toEntity(transactionRequest);
    }

    public TransactionResponse mapToTransactionResponseFromEntity(Transaction transaction) {
        return transactionMapper.toResponse(transaction);
    }

    public void copyPropertiesToEntity(TransactionRequest transactionRequest, Transaction transaction) {
        transactionMapper.updateEntityFromRequest(transactionRequest, transaction);
    }

    public List<TransactionResponse> mapToTransactionResponseListFromEntities(List<Transaction> transactions) {
        return transactionMapper.toResponseList(transactions);
    }
}
