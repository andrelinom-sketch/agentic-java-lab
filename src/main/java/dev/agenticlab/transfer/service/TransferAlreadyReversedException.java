package dev.agenticlab.transfer.service;

import java.util.UUID;

public class TransferAlreadyReversedException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TransferAlreadyReversedException(UUID transferId) {
        super("Transfer already reversed: " + transferId);
    }
}
