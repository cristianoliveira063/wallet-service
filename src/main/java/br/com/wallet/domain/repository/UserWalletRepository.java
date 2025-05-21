package br.com.wallet.domain.repository;

import br.com.wallet.domain.model.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserWalletRepository extends JpaRepository<UserWallet, UUID> {

    List<UserWallet> findByUserId(UUID userId);

    List<UserWallet> findByWalletId(UUID walletId);

    Optional<UserWallet> findByUserIdAndWalletId(UUID userId, UUID walletId);
}
