package dev.agenticlab.account.service;

import java.util.UUID;

/** Conta de destino de uma transferência de saldo não existe. */
public class DestinationAccountNotFoundException extends AccountNotFoundException {

    private static final long serialVersionUID = 1L;

    public DestinationAccountNotFoundException(UUID accountId) {
        super(accountId);
    }
}
