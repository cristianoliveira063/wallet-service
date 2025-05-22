package br.com.wallet.domain.exception;

public class DuplicateWalletNameException extends RuntimeException {

    public DuplicateWalletNameException(String message) {
        super(message);
    }

    public DuplicateWalletNameException(String message, Throwable cause) {
        super(message, cause);
    }
}