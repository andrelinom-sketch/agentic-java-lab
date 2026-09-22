package dev.agenticlab.account.service;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AccountNotFoundException(UUID accountId) {
        super("Account not found: " + accountId);
    }
}
