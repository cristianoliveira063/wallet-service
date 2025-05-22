package br.com.wallet.domain.service.transaction;

import br.com.wallet.domain.model.UserWallet;
import br.com.wallet.domain.repository.UserWalletRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserWalletFinder {

    private final UserWalletRepository userWalletRepository;

    public UserWallet getUserWalletWithLockOrThrow(UUID userId, UUID walletId, String notFoundMessage) {
        return userWalletRepository.findByUserIdAndWalletIdWithPessimisticLock(userId, walletId)
                .orElseThrow(() -> new EntityNotFoundException(
                        notFoundMessage + userId + " and walletId: " + walletId
                ));
    }

}
