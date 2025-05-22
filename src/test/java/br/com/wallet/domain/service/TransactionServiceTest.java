package br.com.wallet.domain.service;

import br.com.wallet.domain.model.BalanceHistory;
import br.com.wallet.domain.model.Transaction;
import br.com.wallet.domain.model.UserWallet;
import br.com.wallet.domain.model.Wallet;
import br.com.wallet.domain.repository.BalanceHistoryRepository;
import br.com.wallet.domain.repository.TransactionRepository;
import br.com.wallet.domain.repository.UserWalletRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.UnaryOperator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserWalletRepository userWalletRepository;

    @Mock
    private BalanceHistoryRepository balanceHistoryRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private TransactionService transactionService;

    @Captor
    private ArgumentCaptor<UserWallet> userWalletCaptor;

    @Captor
    private ArgumentCaptor<BalanceHistory> balanceHistoryCaptor;

    private UUID walletId;
    private UUID fromUserId;
    private UUID toUserId;
    private Wallet wallet;
    private UserWallet fromUserWallet;
    private UserWallet toUserWallet;
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

        fromUserWallet = UserWallet.builder()
                .id(UUID.randomUUID())
                .userId(fromUserId)
                .wallet(wallet)
                .balance(new BigDecimal("100.00"))
                .build();

        toUserWallet = UserWallet.builder()
                .id(UUID.randomUUID())
                .userId(toUserId)
                .wallet(wallet)
                .balance(new BigDecimal("50.00"))
                .build();

        transaction = Transaction.builder()
                .id(UUID.randomUUID())
                .amount(new BigDecimal("25.00"))
                .description("Test transaction")
                .build();
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

        when(userWalletRepository.findByUserIdAndWalletId(toUserId, walletId))
                .thenReturn(Optional.of(toUserWallet));
        when(userWalletRepository.save(any(UserWallet.class))).thenReturn(toUserWallet);
        when(balanceHistoryRepository.save(any(BalanceHistory.class))).thenReturn(new BalanceHistory());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When
        Transaction result = transactionService.deposit(transaction);

        // Then
        assertEquals(transaction, result);
        verify(userWalletRepository).save(userWalletCaptor.capture());
        verify(balanceHistoryRepository).save(any(BalanceHistory.class));
        verify(transactionRepository).save(transaction);

        UserWallet savedUserWallet = userWalletCaptor.getValue();
        assertEquals(new BigDecimal("75.00"), savedUserWallet.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenDepositingWithWrongTransactionType() {
        // Given
        transaction.setType(Transaction.TransactionType.WITHDRAW);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transactionService.deposit(transaction));
    }

    @Test
    void shouldThrowExceptionWhenDepositingWithZeroAmount() {
        // Given
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setAmount(BigDecimal.ZERO);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transactionService.deposit(transaction));
    }

    @Test
    void shouldThrowExceptionWhenDepositingWithNonExistentUserWallet() {
        // Given
        transaction.setType(Transaction.TransactionType.DEPOSIT);
        transaction.setToUserId(toUserId);
        transaction.setWallet(wallet);
        transaction.setAmount(new BigDecimal("25.00"));

        when(userWalletRepository.findByUserIdAndWalletId(toUserId, walletId))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> transactionService.deposit(transaction));
    }

    @Test
    void shouldDebitUserWalletAndSaveTransactionWhenWithdrawing() {
        // Given
        transaction.setType(Transaction.TransactionType.WITHDRAW);
        transaction.setFromUserId(fromUserId);
        transaction.setWallet(wallet);

        when(userWalletRepository.findByUserIdAndWalletId(fromUserId, walletId))
                .thenReturn(Optional.of(fromUserWallet));
        when(userWalletRepository.save(any(UserWallet.class))).thenReturn(fromUserWallet);
        when(balanceHistoryRepository.save(any(BalanceHistory.class))).thenReturn(new BalanceHistory());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        // When
        Transaction result = transactionService.withdraw(transaction);

        // Then
        assertEquals(transaction, result);
        verify(userWalletRepository).save(userWalletCaptor.capture());
        verify(balanceHistoryRepository).save(any(BalanceHistory.class));
        verify(transactionRepository).save(transaction);

        UserWallet savedUserWallet = userWalletCaptor.getValue();
        assertEquals(new BigDecimal("75.00"), savedUserWallet.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingWithWrongTransactionType() {
        // Given
        transaction.setType(Transaction.TransactionType.DEPOSIT);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transactionService.withdraw(transaction));
    }

    @Test
    void shouldThrowExceptionWhenWithdrawingWithInsufficientBalance() {
        // Given
        transaction.setType(Transaction.TransactionType.WITHDRAW);
        transaction.setFromUserId(fromUserId);
        transaction.setWallet(wallet);
        transaction.setAmount(new BigDecimal("150.00"));

        when(userWalletRepository.findByUserIdAndWalletId(fromUserId, walletId))
                .thenReturn(Optional.of(fromUserWallet));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transactionService.withdraw(transaction));
    }

    @Test
    void shouldDebitSourceAndCreditTargetUserWalletsWhenTransferring() {
        // Given
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setFromUserId(fromUserId);
        transaction.setToUserId(toUserId);
        transaction.setWallet(wallet);

        Transaction relatedTransaction = Transaction.builder()
                .id(UUID.randomUUID())
                .build();

        when(userWalletRepository.findByUserIdAndWalletId(fromUserId, walletId))
                .thenReturn(Optional.of(fromUserWallet));
        when(userWalletRepository.findByUserIdAndWalletId(toUserId, walletId))
                .thenReturn(Optional.of(toUserWallet));
        when(userWalletRepository.save(any(UserWallet.class)))
                .thenReturn(fromUserWallet)
                .thenReturn(toUserWallet);
        when(balanceHistoryRepository.save(any(BalanceHistory.class))).thenReturn(new BalanceHistory());
        when(transactionRepository.save(any(Transaction.class)))
                .thenReturn(transaction)
                .thenReturn(relatedTransaction)
                .thenReturn(transaction);

        // When
        Transaction result = transactionService.transfer(transaction);

        // Then
        assertEquals(transaction, result);
        verify(userWalletRepository, times(2)).save(userWalletCaptor.capture());
        verify(balanceHistoryRepository, times(2)).save(any(BalanceHistory.class));
        verify(transactionRepository, times(3)).save(any(Transaction.class));

        List<UserWallet> savedUserWallets = userWalletCaptor.getAllValues();
        assertEquals(new BigDecimal("75.00"), savedUserWallets.get(0).getBalance()); // fromUserWallet
        assertEquals(new BigDecimal("75.00"), savedUserWallets.get(1).getBalance()); // toUserWallet
    }

    @Test
    void shouldThrowExceptionWhenTransferringWithWrongTransactionType() {
        // Given
        transaction.setType(Transaction.TransactionType.DEPOSIT);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> transactionService.transfer(transaction));
    }

    @Test
    void shouldThrowExceptionWhenTransferringWithNullFromUserId() {
        // Given
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setFromUserId(null);
        transaction.setToUserId(toUserId);

        // When & Then
        assertThrows(NullPointerException.class, () -> transactionService.transfer(transaction));
    }

    @Test
    void shouldThrowExceptionWhenTransferringWithNullToUserId() {
        // Given
        transaction.setType(Transaction.TransactionType.TRANSFER);
        transaction.setFromUserId(fromUserId);
        transaction.setToUserId(null);

        // When & Then
        assertThrows(NullPointerException.class, () -> transactionService.transfer(transaction));
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
