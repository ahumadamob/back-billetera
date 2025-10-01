package com.ahumadamob.fnanz.dto;

import com.ahumadamob.fnanz.enums.EstadoReserva;
import com.ahumadamob.fnanz.enums.TipoFin;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la creación de gastos reservados.
 */
@Getter
@Setter
@NoArgsConstructor
public class GastoReservadoCreateDto {

    private TipoFin tipo;

    private Long categoriaId;

    private String concepto;

    private Long periodoId;

    private EstadoReserva estado;

    private BigDecimal montoReservado;

    private BigDecimal montoAplicado;

    private String nota;
}
