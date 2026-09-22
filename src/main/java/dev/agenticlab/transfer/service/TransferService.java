package dev.agenticlab.transfer.service;

import dev.agenticlab.account.service.AccountService;
import dev.agenticlab.transfer.model.Transfer;
import dev.agenticlab.transfer.repository.TransferRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * AD-2: usa apenas o serviço público de {@code account}, nunca seu
 * repository nem seu model. AD-3: débito, crédito e registro em uma única
 * transação atômica.
 */
@Service
public class TransferService {

    private final AccountService accountService;
    private final TransferRepository transferRepository;

    public TransferService(AccountService accountService, TransferRepository transferRepository) {
        this.accountService = accountService;
        this.transferRepository = transferRepository;
    }

    @Transactional
    public Transfer create(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount) {
        accountService.transferBalance(sourceAccountId, destinationAccountId, amount);

        Transfer transfer =
                new Transfer(
                        UUID.randomUUID(),
                        sourceAccountId,
                        destinationAccountId,
                        amount,
                        Transfer.STATUS_COMPLETED,
                        Instant.now());
        return transferRepository.save(transfer);
    }
}
