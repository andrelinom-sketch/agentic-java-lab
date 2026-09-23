package dev.agenticlab.transfer.service;

import java.util.UUID;

public class SameAccountException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SameAccountException(UUID accountId) {
        super("Source and destination must be different accounts: " + accountId);
    }
}
