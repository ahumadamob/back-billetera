package com.ahumadamob.fnanz.dto.response;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que contiene el resumen de partidas presupuestarias por categoría para un periodo financiero.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoFinancieroPartidasResumenDto {

    private PartidaPresupuestariaResumenDetalleDto ingresos;

    private PartidaPresupuestariaResumenDetalleDto egresos;

    private BigDecimal netoReservado;

    private BigDecimal netoAplicado;
}
