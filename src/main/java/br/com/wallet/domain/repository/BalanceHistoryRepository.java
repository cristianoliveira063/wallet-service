package br.com.wallet.domain.repository;

import br.com.wallet.domain.model.BalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BalanceHistoryRepository extends JpaRepository<BalanceHistory, UUID> {
}
