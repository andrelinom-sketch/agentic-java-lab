package dev.agenticlab.account.service;

import dev.agenticlab.account.model.Account;
import dev.agenticlab.account.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço público de {@code account} (AD-2). Nenhum outro pacote acessa
 * {@link AccountRepository} ou {@link Account} diretamente para alterar saldo.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional
    public Account create(BigDecimal initialBalance) {
        Account account = new Account(UUID.randomUUID(), initialBalance);
        return accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Account findById(UUID id) {
        return accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException(id));
    }

    /**
     * Debita {@code sourceAccountId} e credita {@code destinationAccountId} pelo mesmo valor,
     * dentro da transação do chamador (AD-3). AD-4: as duas contas são bloqueadas em ordem
     * crescente de id, para evitar deadlock em transferências cruzadas.
     */
    @Transactional
    public void transferBalance(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount) {
        UUID firstLockId =
                sourceAccountId.compareTo(destinationAccountId) <= 0 ? sourceAccountId : destinationAccountId;
        UUID secondLockId = firstLockId.equals(sourceAccountId) ? destinationAccountId : sourceAccountId;

        Account first = lockById(firstLockId);
        Account second = lockById(secondLockId);

        Account source = firstLockId.equals(sourceAccountId) ? first : second;
        Account destination = firstLockId.equals(sourceAccountId) ? second : first;

        source.debit(amount);
        destination.credit(amount);
    }

    private Account lockById(UUID id) {
        return accountRepository.findByIdForUpdate(id).orElseThrow(() -> new AccountNotFoundException(id));
    }
}
