package br.com.wallet.domain.service;

import br.com.wallet.domain.exception.DuplicateWalletNameException;
import br.com.wallet.domain.model.Wallet;
import br.com.wallet.domain.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    private Wallet wallet;
    private final String walletName = "Test Wallet";

    @BeforeEach
    void setUp() {
        wallet = Wallet.builder()
                .id(UUID.randomUUID())
                .name(walletName)
                .build();
    }

    @Test
    void shouldSaveWalletWhenNameIsUnique() {
        // Given
        when(walletRepository.findByName(walletName)).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenReturn(wallet);

        // When
        Wallet savedWallet = walletService.save(wallet);

        // Then
        assertEquals(wallet.getId(), savedWallet.getId());
        assertEquals(wallet.getName(), savedWallet.getName());
    }

    @Test
    void shouldThrowExceptionWhenSavingWalletWithDuplicateName() {
        // Given
        when(walletRepository.findByName(walletName)).thenReturn(Optional.of(wallet));

        // When & Then
        assertThrows(DuplicateWalletNameException.class, () -> walletService.save(wallet));
    }

    @Test
    void shouldUpdateWalletWhenNameIsUnchanged() {
        // Given
        UUID id = UUID.randomUUID();
        Wallet existingWallet = Wallet.builder()
                .id(id)
                .name(walletName)
                .build();

        Wallet updatedWallet = Wallet.builder()
                .name(walletName)
                .build();

        when(walletRepository.findById(id)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.save(any(Wallet.class))).thenReturn(updatedWallet);

        // When
        Wallet result = walletService.update(id, updatedWallet);

        // Then
        assertEquals(walletName, result.getName());
    }

    @Test
    void shouldUpdateWalletWhenNewNameIsUnique() {
        // Given
        UUID id = UUID.randomUUID();
        String newName = "New Wallet Name";

        Wallet existingWallet = Wallet.builder()
                .id(id)
                .name(walletName)
                .build();

        Wallet updatedWallet = Wallet.builder()
                .name(newName)
                .build();

        when(walletRepository.findById(id)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.findByName(newName)).thenReturn(Optional.empty());
        when(walletRepository.save(any(Wallet.class))).thenReturn(updatedWallet);

        // When
        Wallet result = walletService.update(id, updatedWallet);

        // Then
        assertEquals(newName, result.getName());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingWalletWithDuplicateName() {
        // Given
        UUID id = UUID.randomUUID();
        String newName = "New Wallet Name";

        Wallet existingWallet = Wallet.builder()
                .id(id)
                .name(walletName)
                .build();

        Wallet anotherWallet = Wallet.builder()
                .id(UUID.randomUUID())
                .name(newName)
                .build();

        Wallet updatedWallet = Wallet.builder()
                .name(newName)
                .build();

        when(walletRepository.findById(id)).thenReturn(Optional.of(existingWallet));
        when(walletRepository.findByName(newName)).thenReturn(Optional.of(anotherWallet));

        // When & Then
        assertThrows(DuplicateWalletNameException.class, () -> walletService.update(id, updatedWallet));
    }
}