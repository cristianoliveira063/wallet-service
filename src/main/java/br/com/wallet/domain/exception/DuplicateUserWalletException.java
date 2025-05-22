package br.com.wallet.domain.exception;

import java.util.UUID;

public class DuplicateUserWalletException extends RuntimeException {

    public DuplicateUserWalletException(UUID userId, UUID walletId) {
        super(String.format("A UserWallet association already exists for userId: %s and walletId: %s",
                userId, walletId));
    }
}