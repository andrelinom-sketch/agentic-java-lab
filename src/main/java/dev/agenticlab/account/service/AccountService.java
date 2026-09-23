package dev.agenticlab.account.service;

import dev.agenticlab.account.model.Account;
import dev.agenticlab.account.repository.AccountRepository;
import java.math.BigDecimal;
import java.util.Optional;
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
     * crescente de id, para evitar deadlock em transferências cruzadas, e o saldo é verificado
     * depois do bloqueio.
     *
     * <p>A existência das contas é verificada depois dos dois bloqueios, origem antes de destino,
     * para que a resposta não dependa da ordem dos ids.
     *
     * @throws SourceAccountNotFoundException se a origem não existe
     * @throws DestinationAccountNotFoundException se o destino não existe
     * @throws InsufficientFundsException se o saldo da origem é menor que {@code amount}
     */
    @Transactional
    public void transferBalance(UUID sourceAccountId, UUID destinationAccountId, BigDecimal amount) {
        boolean sourceFirst = sourceAccountId.compareTo(destinationAccountId) <= 0;
        UUID firstLockId = sourceFirst ? sourceAccountId : destinationAccountId;
        UUID secondLockId = sourceFirst ? destinationAccountId : sourceAccountId;

        Optional<Account> first = accountRepository.findByIdForUpdate(firstLockId);
        Optional<Account> second = accountRepository.findByIdForUpdate(secondLockId);

        Account source =
                (sourceFirst ? first : second)
                        .orElseThrow(() -> new SourceAccountNotFoundException(sourceAccountId));
        Account destination =
                (sourceFirst ? second : first)
                        .orElseThrow(() -> new DestinationAccountNotFoundException(destinationAccountId));

        if (source.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(sourceAccountId);
        }

        source.debit(amount);
        destination.credit(amount);
    }
}
