package br.com.wallet.domain.repository;

import br.com.wallet.domain.model.UserWallet;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserWalletRepository extends JpaRepository<UserWallet, UUID> {

    List<UserWallet> findByUserId(UUID userId);

    List<UserWallet> findByWalletId(UUID walletId);

    Optional<UserWallet> findByUserIdAndWalletId(UUID userId, UUID walletId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000")})
    @Query("SELECT uw FROM UserWallet uw WHERE uw.userId = :userId AND uw.wallet.id = :walletId")
    Optional<UserWallet> findByUserIdAndWalletIdWithPessimisticLock(
            @Param("userId") UUID userId,
            @Param("walletId") UUID walletId);

}
