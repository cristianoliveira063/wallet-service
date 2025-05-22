package br.com.wallet.domain.service;

import br.com.wallet.domain.exception.DuplicateUserWalletException;
import br.com.wallet.domain.model.UserWallet;
import br.com.wallet.domain.model.Wallet;
import br.com.wallet.domain.repository.UserWalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserWalletServiceTest {

    @Mock
    private UserWalletRepository userWalletRepository;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private UserWalletService userWalletService;

    private UserWallet userWallet;
    private Wallet wallet;
    private UUID userId;
    private UUID walletId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        walletId = UUID.randomUUID();

        wallet = Wallet.builder()
                .id(walletId)
                .name("Test Wallet")
                .build();

        userWallet = UserWallet.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .wallet(wallet)
                .balance(BigDecimal.ZERO)
                .build();
    }

    @Test
    void shouldSaveUserWalletWhenUserWalletAssociationIsUnique() {
        // Given
        when(userWalletRepository.findByUserIdAndWalletId(userId, walletId)).thenReturn(Optional.empty());
        when(walletService.findById(walletId)).thenReturn(wallet);
        when(userWalletRepository.save(any(UserWallet.class))).thenReturn(userWallet);

        // When
        UserWallet savedUserWallet = userWalletService.save(userWallet);

        // Then
        assertEquals(userWallet.getId(), savedUserWallet.getId());
        assertEquals(userWallet.getUserId(), savedUserWallet.getUserId());
        assertEquals(userWallet.getWallet().getId(), savedUserWallet.getWallet().getId());
    }

    @Test
    void shouldThrowExceptionWhenSavingUserWalletWithDuplicateAssociation() {
        // Given
        when(userWalletRepository.findByUserIdAndWalletId(userId, walletId)).thenReturn(Optional.of(userWallet));

        // When & Then
        assertThrows(DuplicateUserWalletException.class, () -> userWalletService.save(userWallet));
    }

    @Test
    void shouldUpdateUserWalletWhenAssociationIsUnchanged() {
        // Given
        UUID id = UUID.randomUUID();
        UserWallet existingUserWallet = UserWallet.builder()
                .id(id)
                .userId(userId)
                .wallet(wallet)
                .balance(BigDecimal.TEN)
                .build();

        UserWallet updatedUserWallet = UserWallet.builder()
                .userId(userId)
                .wallet(wallet)
                .build();

        when(userWalletRepository.findById(id)).thenReturn(Optional.of(existingUserWallet));
        when(userWalletRepository.save(any(UserWallet.class))).thenReturn(updatedUserWallet);

        // When
        UserWallet result = userWalletService.update(id, updatedUserWallet);

        // Then
        assertEquals(userId, result.getUserId());
        assertEquals(walletId, result.getWallet().getId());
    }

    @Test
    void shouldUpdateUserWalletWhenNewAssociationIsUnique() {
        // Given
        UUID id = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();

        UserWallet existingUserWallet = UserWallet.builder()
                .id(id)
                .userId(userId)
                .wallet(wallet)
                .balance(BigDecimal.TEN)
                .build();

        UserWallet updatedUserWallet = UserWallet.builder()
                .userId(newUserId)
                .wallet(wallet)
                .build();

        when(userWalletRepository.findById(id)).thenReturn(Optional.of(existingUserWallet));
        when(userWalletRepository.findByUserIdAndWalletId(newUserId, walletId)).thenReturn(Optional.empty());
        when(userWalletRepository.save(any(UserWallet.class))).thenReturn(updatedUserWallet);

        // When
        UserWallet result = userWalletService.update(id, updatedUserWallet);

        // Then
        assertEquals(newUserId, result.getUserId());
        assertEquals(walletId, result.getWallet().getId());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUserWalletWithDuplicateAssociation() {
        // Given
        UUID id = UUID.randomUUID();
        UUID newUserId = UUID.randomUUID();

        UserWallet existingUserWallet = UserWallet.builder()
                .id(id)
                .userId(userId)
                .wallet(wallet)
                .balance(BigDecimal.TEN)
                .build();

        UserWallet anotherUserWallet = UserWallet.builder()
                .id(UUID.randomUUID())
                .userId(newUserId)
                .wallet(wallet)
                .build();

        UserWallet updatedUserWallet = UserWallet.builder()
                .userId(newUserId)
                .wallet(wallet)
                .build();

        when(userWalletRepository.findById(id)).thenReturn(Optional.of(existingUserWallet));
        when(userWalletRepository.findByUserIdAndWalletId(newUserId, walletId)).thenReturn(Optional.of(anotherUserWallet));

        // When & Then
        assertThrows(DuplicateUserWalletException.class, () -> userWalletService.update(id, updatedUserWallet));
    }
}