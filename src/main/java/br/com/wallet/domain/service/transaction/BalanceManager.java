package br.com.wallet.domain.service.transaction;

import br.com.wallet.domain.model.BalanceHistory;
import br.com.wallet.domain.model.UserWallet;
import br.com.wallet.domain.repository.BalanceHistoryRepository;
import br.com.wallet.domain.repository.UserWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BalanceManager {

    private final UserWalletRepository userWalletRepository;
    private final BalanceHistoryRepository balanceHistoryRepository;

    public void creditUserWallet(UserWallet userWallet, BigDecimal amount) {
        updateUserWalletBalance(userWallet, amount, true);
    }

    public void debitUserWallet(UserWallet userWallet, BigDecimal amount) {
        updateUserWalletBalance(userWallet, amount, false);
    }

    private void updateUserWalletBalance(UserWallet userWallet, BigDecimal amount, boolean isCredit) {
        Objects.requireNonNull(userWallet, "User wallet cannot be null");
        Objects.requireNonNull(amount, "Amount cannot be null");
        userWallet.setBalance(isCredit ? userWallet.getBalance().add(amount) : userWallet.getBalance().subtract(amount));
        userWalletRepository.save(userWallet);
        recordBalanceHistory(userWallet);
    }

    private void recordBalanceHistory(UserWallet userWallet) {
        Objects.requireNonNull(userWallet, "User wallet cannot be null");
        Objects.requireNonNull(userWallet.getWallet(), "Wallet cannot be null");
        BalanceHistory balanceHistory = BalanceHistory.builder()
                .userId(userWallet.getUserId())
                .wallet(userWallet.getWallet().getId())
                .balance(userWallet.getBalance())
                .build();
        balanceHistoryRepository.save(balanceHistory);
    }
}