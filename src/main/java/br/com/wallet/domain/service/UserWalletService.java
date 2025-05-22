package br.com.wallet.domain.service;

import br.com.wallet.domain.exception.DuplicateUserWalletException;
import br.com.wallet.domain.model.UserWallet;
import br.com.wallet.domain.model.Wallet;
import br.com.wallet.domain.repository.UserWalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
        Objects.requireNonNull(id, "UserWallet ID cannot be null");
        return userWalletRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("UserWallet not found with id: " + id));
    }

    public List<UserWallet> findByUserId(UUID userId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        return userWalletRepository.findByUserId(userId);
    }

    public List<UserWallet> findByWalletId(UUID walletId) {
        Objects.requireNonNull(walletId, "Wallet ID cannot be null");
        return userWalletRepository.findByWalletId(walletId);
    }

    public UserWallet findByUserIdAndWalletId(UUID userId, UUID walletId) {
        Objects.requireNonNull(userId, "User ID cannot be null");
        Objects.requireNonNull(walletId, "Wallet ID cannot be null");
        return userWalletRepository.findByUserIdAndWalletId(userId, walletId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "UserWallet not found with userId: " + userId + " and walletId: " + walletId));
    }

    @Transactional
    public UserWallet save(UserWallet userWallet) {
        Objects.requireNonNull(userWallet, "UserWallet cannot be null");
        Objects.requireNonNull(userWallet.getUserId(), "User ID cannot be null");
        Objects.requireNonNull(userWallet.getWallet(), "Wallet cannot be null");

        UUID walletId = getWalletId(userWallet);

        checkForDuplicateUserWallet(userWallet.getUserId(), walletId, null);

        Wallet wallet = walletService.findById(walletId);
        userWallet.setWallet(wallet);

        return userWalletRepository.save(userWallet);
    }

    @Transactional
    public UserWallet update(UUID id, UserWallet userWallet) {
        Objects.requireNonNull(id, "UserWallet ID cannot be null");
        Objects.requireNonNull(userWallet, "UserWallet cannot be null");
        Objects.requireNonNull(userWallet.getUserId(), "User ID cannot be null");
        Objects.requireNonNull(userWallet.getWallet(), "Wallet cannot be null");

        UserWallet existingUserWallet = findById(id);
        UUID walletId = getWalletId(userWallet);

        boolean userIdChanged = !existingUserWallet.getUserId().equals(userWallet.getUserId());
        boolean walletIdChanged = !existingUserWallet.getWallet().getId().equals(walletId);

        if (userIdChanged || walletIdChanged) {
            checkForDuplicateUserWallet(userWallet.getUserId(), walletId, id);
        }

        if (walletIdChanged) {
            Wallet wallet = walletService.findById(walletId);
            userWallet.setWallet(wallet);
        }

        userWallet.setId(existingUserWallet.getId());
        userWallet.setCreatedAt(existingUserWallet.getCreatedAt());
        userWallet.setBalance(existingUserWallet.getBalance());

        return userWalletRepository.save(userWallet);
    }

    @Transactional
    public void delete(UUID id) {
        Objects.requireNonNull(id, "UserWallet ID cannot be null");
        UserWallet userWallet = findById(id);
        userWalletRepository.delete(userWallet);
    }

    private UUID getWalletId(UserWallet userWallet) {
        Objects.requireNonNull(userWallet.getWallet(), "Wallet cannot be null");
        UUID walletId = userWallet.getWallet().getId();
        Objects.requireNonNull(walletId, "Wallet ID cannot be null");
        return walletId;
    }

    private void checkForDuplicateUserWallet(UUID userId, UUID walletId, UUID excludeId) {
        Optional<UserWallet> existingUserWallet = userWalletRepository.findByUserIdAndWalletId(userId, walletId);

        existingUserWallet.ifPresent(userWallet -> {
            if (!userWallet.getId().equals(excludeId)) {
                throw new DuplicateUserWalletException(userId, walletId);
            }
        });
    }
}
