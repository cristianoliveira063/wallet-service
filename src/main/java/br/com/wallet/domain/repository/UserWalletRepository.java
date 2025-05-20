package br.com.wallet.domain.repository;

import br.com.wallet.domain.model.UserWallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserWalletRepository extends JpaRepository<UserWallet, UUID> {

}