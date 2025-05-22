package br.com.wallet.domain.service;

import br.com.wallet.domain.exception.DuplicateWalletNameException;
import br.com.wallet.domain.model.Wallet;
import br.com.wallet.domain.repository.WalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public List<Wallet> findAll() {
        return walletRepository.findAll();
    }

    public Wallet findById(UUID id) {
        return walletRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found with id: " + id));
    }

    @Transactional
    public Wallet save(Wallet wallet) {
        walletRepository.findByName(wallet.getName())
                .ifPresent(existingWallet -> {
                    throw new DuplicateWalletNameException("Wallet with name '" + wallet.getName() + "' already exists");
                });
        return walletRepository.save(wallet);
    }

    @Transactional
    public Wallet update(UUID id, Wallet wallet) {
        Wallet existingWallet = findById(id);

        if (!existingWallet.getName().equals(wallet.getName())) {
            walletRepository.findByName(wallet.getName())
                    .ifPresent(duplicateWallet -> {
                        throw new DuplicateWalletNameException("Wallet with name '" + wallet.getName() + "' already exists");
                    });
        }

        wallet.setId(existingWallet.getId());
        wallet.setCreatedAt(existingWallet.getCreatedAt());
        return walletRepository.save(wallet);
    }

    @Transactional
    public void delete(UUID id) {
        Wallet wallet = findById(id);
        walletRepository.delete(wallet);
    }
}
