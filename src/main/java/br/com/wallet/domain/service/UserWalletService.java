package br.com.wallet.domain.service;

import br.com.wallet.domain.model.UserWallet;
import br.com.wallet.domain.model.Wallet;
import br.com.wallet.domain.repository.UserWalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserWalletService {

    private final UserWalletRepository userWalletRepository;
    private final WalletService walletService;

    public List<UserWallet> findAll() {
        return userWalletRepository.findAll();
    }

    public UserWallet findById(UUID id) {
        return userWalletRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserWallet not found with id: " + id));
    }

    public List<UserWallet> findByUserId(UUID userId) {
        return userWalletRepository.findByUserId(userId);
    }

    public List<UserWallet> findByWalletId(UUID walletId) {
        return userWalletRepository.findByWalletId(walletId);
    }

    public UserWallet findByUserIdAndWalletId(UUID userId, UUID walletId) {
        return userWalletRepository.findByUserIdAndWalletId(userId, walletId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "UserWallet not found with userId: " + userId + " and walletId: " + walletId));
    }

    @Transactional
    public UserWallet save(UserWallet userWallet) {

        Wallet wallet = walletService.findById(userWallet.getWallet().getId());
        userWallet.setWallet(wallet);

        return userWalletRepository.save(userWallet);
    }

    @Transactional
    public UserWallet update(UUID id, UserWallet userWallet) {
        UserWallet existingUserWallet = findById(id);

        if (!existingUserWallet.getWallet().getId().equals(userWallet.getWallet().getId())) {
            Wallet wallet = walletService.findById(userWallet.getWallet().getId());
            userWallet.setWallet(wallet);
        }

        userWallet.setId(existingUserWallet.getId());
        userWallet.setCreatedAt(existingUserWallet.getCreatedAt());
        userWallet.setBalance(existingUserWallet.getBalance()); // Preserve the balance

        return userWalletRepository.save(userWallet);
    }

    @Transactional
    public void delete(UUID id) {
        UserWallet userWallet = findById(id);
        userWalletRepository.delete(userWallet);
    }
}