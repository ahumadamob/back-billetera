package com.ahumadamob.fnanz.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para aplicar una partida presupuestaria.
 */
@Getter
@Setter
@NoArgsConstructor
public class PartidaPresupuestariaAplicarDto {

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 14, fraction = 2)
    private BigDecimal montoAplicado;
}
