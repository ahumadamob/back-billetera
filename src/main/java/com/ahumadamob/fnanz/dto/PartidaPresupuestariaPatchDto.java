package com.ahumadamob.fnanz.dto;

import com.ahumadamob.fnanz.enums.EstadoReserva;
import com.ahumadamob.fnanz.enums.TipoFin;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para actualizaciones parciales de partidas presupuestarias.
 */
@Getter
@Setter
@NoArgsConstructor
public class PartidaPresupuestariaPatchDto {

    private TipoFin tipo;

    private Long categoriaId;

    private String concepto;

    private Long periodoId;

    private EstadoReserva estado;

    private BigDecimal montoReservado;

    private BigDecimal montoAplicado;

    private String nota;
}
