package com.ahumadamob.fnanz.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para representar totales de montos reservados y aplicados.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GastoReservadoTotalesDto {

    private BigDecimal montoReservado;

    private BigDecimal montoAplicado;
}

