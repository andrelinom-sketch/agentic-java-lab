package dev.agenticlab.account.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * AD-5: {@code BigDecimal}, mais de 2 casas decimais é rejeitado sem
 * arredondar (via {@link Digits}, que não trunca nem arredonda).
 */
public record CreateAccountRequest(
        @NotNull @DecimalMin(value = "0.00", inclusive = true) @Digits(integer = 17, fraction = 2)
                BigDecimal initialBalance) {}
