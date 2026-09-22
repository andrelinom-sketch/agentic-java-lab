package dev.agenticlab.account.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Conta com saldo (AD-2: account é o único dono do saldo).
 *
 * <p>O {@code id} é um UUID v4 gerado pela aplicação (AD-6), nunca pelo banco.
 */
@Entity
@Table(name = "account")
public class Account {

    @Id private UUID id;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    protected Account() {
        // exigido pelo JPA
    }

    public Account(UUID id, BigDecimal balance) {
        this.id = id;
        this.balance = balance;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    /** Mutação de saldo restrita a {@code account} (AD-2). */
    public void debit(BigDecimal amount) {
        this.balance = this.balance.subtract(amount);
    }

    /** Mutação de saldo restrita a {@code account} (AD-2). */
    public void credit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
