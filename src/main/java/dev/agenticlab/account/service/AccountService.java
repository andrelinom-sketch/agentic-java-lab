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
}
