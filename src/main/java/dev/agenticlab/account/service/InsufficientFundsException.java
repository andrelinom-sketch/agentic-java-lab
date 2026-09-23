package dev.agenticlab.account.service;

import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InsufficientFundsException(UUID accountId) {
        super("Insufficient funds in account: " + accountId);
    }
}
