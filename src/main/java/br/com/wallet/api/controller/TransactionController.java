package br.com.wallet.api.controller;

import br.com.wallet.api.assembler.TransactionAssembler;
import br.com.wallet.api.model.request.TransactionRequest;
import br.com.wallet.api.model.response.TransactionResponse;
import br.com.wallet.domain.model.Transaction;
import br.com.wallet.domain.model.Wallet;
import br.com.wallet.domain.service.TransactionService;
import br.com.wallet.domain.service.WalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.function.UnaryOperator;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final TransactionAssembler transactionAssembler;
    private final WalletService walletService;

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> findAll() {
        List<Transaction> transactions = transactionService.findAll();
        return ResponseEntity.ok(transactionAssembler.mapToTransactionResponseListFromEntities(transactions));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> findById(@PathVariable UUID id) {
        Transaction transaction = transactionService.findById(id);
        return ResponseEntity.ok(transactionAssembler.mapToTransactionResponseFromEntity(transaction));
    }

    @PostMapping("/deposit")
    public ResponseEntity<TransactionResponse> deposit(@RequestBody @Valid TransactionRequest transactionRequest) {
        return processTransaction(
                transactionRequest,
                Transaction.TransactionType.DEPOSIT,
                transactionService::deposit
        );
    }

    @PostMapping("/withdraw")
    public ResponseEntity<TransactionResponse> withdraw(@RequestBody @Valid TransactionRequest transactionRequest) {
        return processTransaction(
                transactionRequest,
                Transaction.TransactionType.WITHDRAW,
                transactionService::withdraw
        );
    }

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(@RequestBody @Valid TransactionRequest transactionRequest) {
        return processTransaction(
                transactionRequest,
                Transaction.TransactionType.TRANSFER,
                transactionService::transfer
        );
    }

    private ResponseEntity<TransactionResponse> processTransaction(
            TransactionRequest transactionRequest,
            Transaction.TransactionType transactionType,
            UnaryOperator<Transaction> transactionProcessor) {

        transactionRequest.validate();

        Transaction transaction = transactionAssembler.mapToTransactionEntityFromRequest(transactionRequest);
        transaction.setType(transactionType);

        Wallet wallet = walletService.findById(transactionRequest.walletId());
        transaction.setWallet(wallet);

        Transaction savedTransaction = transactionProcessor.apply(transaction);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionAssembler.mapToTransactionResponseFromEntity(savedTransaction));
    }
}
