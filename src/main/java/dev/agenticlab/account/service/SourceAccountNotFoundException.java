package dev.agenticlab.account.service;

import java.util.UUID;

/** Conta de origem de uma transferência de saldo não existe. */
public class SourceAccountNotFoundException extends AccountNotFoundException {

    private static final long serialVersionUID = 1L;

    public SourceAccountNotFoundException(UUID accountId) {
        super(accountId);
    }
}
