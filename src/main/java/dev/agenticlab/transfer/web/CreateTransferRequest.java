package dev.agenticlab.transfer.web;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.UUID;

/** AD-5: {@code BigDecimal}, mais de 2 casas decimais é rejeitado sem arredondar. */
public record CreateTransferRequest(
        @NotNull UUID sourceAccountId,
        @NotNull UUID destinationAccountId,
        @NotNull @Digits(integer = 17, fraction = 2) BigDecimal amount) {}
