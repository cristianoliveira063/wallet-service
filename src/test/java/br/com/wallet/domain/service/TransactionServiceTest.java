package br.com.wallet.domain.service;

import br.com.wallet.domain.model.Transaction;
import br.com.wallet.domain.model.Wallet;
import br.com.wallet.domain.repository.TransactionRepository;
import br.com.wallet.domain.service.transaction.TransactionProcessor;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private WalletService walletService;

    @Mock
    private TransactionProcessor depositProcessor;

    @Mock
    private TransactionProcessor withdrawProcessor;

    @Mock
    private TransactionProcessor transferProcessor;

    @InjectMocks
    private TransactionService transactionService;

    private UUID walletId;
    private UUID fromUserId;
    private UUID toUserId;
    private Wallet wallet;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        walletId = UUID.randomUUID();
        fromUserId = UUID.randomUUID();
        toUserId = UUID.randomUUID();
        wallet = Wallet.builder()
                .id(walletId)
                .name("Test Wallet")
                .build();
        transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal("25.00"))
                .description("Test transaction")
                .build();

        Mockito.lenient().when(depositProcessor.canProcess(Transaction.TransactionType.DEPOSIT)).thenReturn(true);
        Mockito.lenient().when(depositProcessor.canProcess(Transaction.TransactionType.WITHDRAW)).thenReturn(false);
        Mockito.lenient().when(depositProcessor.canProcess(Transaction.TransactionType.TRANSFER)).thenReturn(false);

        Mockito.lenient().when(withdrawProcessor.canProcess(Transaction.TransactionType.DEPOSIT)).thenReturn(false);
        Mockito.lenient().when(withdrawProcessor.canProcess(Transaction.TransactionType.WITHDRAW)).thenReturn(true);
        Mockito.lenient().when(withdrawProcessor.canProcess(Transaction.TransactionType.TRANSFER)).thenReturn(false);

        Mockito.lenient().when(transferProcessor.canProcess(Transaction.TransactionType.DEPOSIT)).thenReturn(false);
        Mockito.lenient().when(transferProcessor.canProcess(Transaction.TransactionType.WITHDRAW)).thenReturn(false);
        Mockito.lenient().when(transferProcessor.canProcess(Transaction.TransactionType.TRANSFER)).thenReturn(true);

        // Set up the transactionProcessors list in the TransactionService
        List<TransactionProcessor> processors = new ArrayList<>();
        processors.add(depositProcessor);
        processors.add(withdrawProcessor);
        processors.add(transferProcessor);
        ReflectionTestUtils.setField(transactionService, "transactionProcessors", processors);
    }

    @Test
    void shouldApplyProcessorAndReturnResultWhenProcessingTransactionWithWallet() {
        // Given
        when(walletService.findById(walletId)).thenReturn(wallet);
        UnaryOperator<Transaction> processor = t -> {
            t.setDescription("Processed");
            return t;
        };

        // When
        Transaction result = transactionService.processTransactionWithWallet(transaction, walletId, processor);

        // Then
        assertEquals("Processed", result.getDescription());
        assertEquals(wallet, result.getWallet());
        verify(walletService).findById(walletId);
    }

    @Test
    void shouldThrowExceptionWhenProcessingTransactionWithWalletWithNullTransaction() {
        // When & Then
        assertThrows(NullPointerException.class, () ->
                transactionService.processTransactionWithWallet(null, walletId, t -> t)
        );
    }

    @Test
    void shouldThrowExceptionWhenProcessingTransactionWithWalletWithNullWalletId() {
        // When & Then
        assertThrows(NullPointerException.class, () ->
                transactionService.processTransactionWithWallet(transaction, null, t -> t)
        );
    }

    @Test
    void shouldThrowExceptionWhenProcessingTransactionWithWalletWithNullProcessor() {
        // When & Then
        assertThrows(NullPointerException.class, () ->
                transactionService.processTransactionWithWallet(transaction, walletId, null)
        );
    }

    @Test
    void shouldCreditUserWalletAndSaveTransactionWhenDepositing() {
        // Given
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setToUserId(toUserId);
        transaction.setWallet(wallet);

        when(depositProcessor.process(transaction)).thenReturn(transaction);

        // When
        Transaction result = transactionService.deposit(transaction);

        // Then
        assertNotNull(result);
        assertEquals(transaction, result);

        verify(depositProcessor).process(transaction);
    }

    @Test
    void shouldThrowExceptionWhenDepositingWithWrongTransactionType() {
        // Given
        transaction.setType(Transaction.TransactionType.WITHDRAW);

        when(depositProcessor.process(transaction)).thenThrow(new IllegalArgumentException("Transaction type must be DEPOSIT"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transactionService.deposit(transaction));

        verify(depositProcessor).process(transaction);
    }

    @Test
    void shouldThrowExceptionWhenDepositingWithZeroAmount() {
        // Given
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.ZERO);

        when(depositProcessor.process(transaction)).thenThrow(new IllegalArgumentException("Amount must be greater than zero"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transactionService.deposit(transaction));

        verify(depositProcessor).process(transaction);
    }

    @Test
    void shouldThrowExceptionWhenDepositingWithNonExistentUserWallet() {
        // Given
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setToUserId(toUserId);
        transaction.setWallet(wallet);

        when(depositProcessor.process(transaction)).thenThrow(new EntityNotFoundException("UserWallet not found with userId: " + toUserId + " and walletId: " + walletId));

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> transactionService.deposit(transaction));

        verify(depositProcessor).process(transaction);
    }

    @Test
    void shouldDebitUserWalletAndSaveTransactionWhenWithdrawing() {
        // Given
        transaction.setType(Transaction.TransactionType.WITHDRAW);
        transaction.setFromUserId(fromUserId);
        transaction.setWallet(wallet);

        when(withdrawProcessor.process(transaction)).thenReturn(transaction);

        // When
        Transaction result = transactionService.withdraw(transaction);

        // Then
        assertNotNull(result);
        assertEquals(transaction, result);

        verify(withdrawProcessor).process(transaction);
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingWithWrongTransactionType() {
        // Given
        transaction.setType(Transaction.TransactionType.DEPOSIT);

        when(withdrawProcessor.process(transaction)).thenThrow(new IllegalArgumentException("Transaction type must be WITHDRAW"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transactionService.withdraw(transaction));

        verify(withdrawProcessor).process(transaction);
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingWithInsufficientBalance() {
        // Given
        transaction.setType(Transaction.TransactionType.WITHDRAW);
        transaction.setFromUserId(fromUserId);
        transaction.setWallet(wallet);
        transaction.setAmount(new BigDecimal("150.00"));

        when(withdrawProcessor.process(transaction)).thenThrow(new IllegalArgumentException("Insufficient balance"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transactionService.withdraw(transaction));

        verify(withdrawProcessor).process(transaction);
    }

    @Test
    void shouldDebitSourceAndCreditTargetUserWalletsWhenTransferring() {
        // Given
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setFromUserId(fromUserId);
        transaction.setToUserId(toUserId);
        transaction.setWallet(wallet);

        when(transferProcessor.process(transaction)).thenReturn(transaction);

        // When
        Transaction result = transactionService.transfer(transaction);

        // Then
        assertNotNull(result);
        assertEquals(transaction, result);

        verify(transferProcessor).process(transaction);
    }

    @Test
    void shouldThrowExceptionWhenTransferringWithWrongTransactionType() {
        // Given
        transaction.setType(Transaction.TransactionType.DEPOSIT);

        when(transferProcessor.process(transaction)).thenThrow(new IllegalArgumentException("Transaction type must be TRANSFER"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(transaction));

        verify(transferProcessor).process(transaction);
    }

    @Test
    void shouldThrowExceptionWhenTransferringWithNullFromUserId() {
        // Given
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setFromUserId(null);
        transaction.setToUserId(toUserId);

        when(transferProcessor.process(transaction)).thenThrow(new NullPointerException("From user ID is required for transfers"));

        // When & Then
        assertThrows(NullPointerException.class, () -> transactionService.transfer(transaction));

        verify(transferProcessor).process(transaction);
    }

    @Test
    void shouldThrowExceptionWhenTransferringWithNullToUserId() {
        // Given
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setFromUserId(fromUserId);
        transaction.setToUserId(null);

        when(transferProcessor.process(transaction)).thenThrow(new NullPointerException("To user ID is required for transfers"));

        // When & Then
        assertThrows(NullPointerException.class, () -> transactionService.transfer(transaction));

        verify(transferProcessor).process(transaction);
    }

    @Test
    void shouldReturnAllTransactionsWhenFindingAll() {
        // Given
        List<Transaction> transactions = List.of(transaction);
        when(transactionRepository.findAll()).thenReturn(transactions);

        // When
        List<Transaction> result = transactionService.findAll();

        // Then
        assertEquals(transactions, result);
        verify(transactionRepository).findAll();
    }

    @Test
    void shouldReturnTransactionWhenFindingByIdAndTransactionExists() {
        // Given
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Optional.of(transaction));

        // When
        Transaction result = transactionService.findById(id);

        // Then
        assertEquals(transaction, result);
        verify(transactionRepository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenFindingByIdAndTransactionDoesNotExist() {
        // Given
        UUID id = UUID.randomUUID();
        when(transactionRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> transactionService.findById(id));
        verify(transactionRepository).findById(id);
    }
}