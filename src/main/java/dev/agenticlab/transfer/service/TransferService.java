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
 * transação atômica; no estorno, devolução dos saldos e mudança de status.
 */
@Service
public class TransferService {

    private final AccountService accountService;
    private final TransferRepository transferRepository;

    public TransferService(AccountService accountService, TransferRepository transferRepository) {
        this.accountService = accountService;
        this.transferRepository = transferRepository;
    }

    /** @throws SameAccountException se origem e destino são a mesma Conta */
    @Transactional
    public Transfer create(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount) {
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new SameAccountException(sourceAccountId);
        }
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

    /**
     * Estorna uma Transferência {@code COMPLETED} numa única transação (AD-3). AD-4: a
     * Transferência é bloqueada antes das contas e o status é verificado depois do bloqueio; a
     * devolução usa o mesmo caminho de saldo de uma Transferência, com origem e destino
     * invertidos, que bloqueia as contas em ordem crescente de id.
     *
     * @throws TransferNotFoundException se não existe Transferência com esse id
     * @throws TransferAlreadyReversedException se a Transferência já foi estornada
     * @throws dev.agenticlab.account.service.InsufficientFundsException se o destino não tem
     *     saldo para devolver o valor
     */
    @Transactional
    public Transfer reverse(UUID id) {
        Transfer transfer =
                transferRepository
                        .findByIdForUpdate(id)
                        .orElseThrow(() -> new TransferNotFoundException(id));
        if (transfer.isReversed()) {
            throw new TransferAlreadyReversedException(id);
        }
        accountService.transferBalance(
                transfer.getDestinationAccountId(), transfer.getSourceAccountId(), transfer.getAmount());
        transfer.markReversed();
        return transfer;
    }

    /** @throws TransferNotFoundException se não existe Transferência com esse id */
    @Transactional(readOnly = true)
    public Transfer findById(UUID id) {
        return transferRepository.findById(id).orElseThrow(() -> new TransferNotFoundException(id));
    }
}
