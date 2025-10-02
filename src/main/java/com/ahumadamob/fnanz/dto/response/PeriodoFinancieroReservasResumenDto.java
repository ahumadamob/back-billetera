package com.ahumadamob.fnanz.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que contiene el resumen de reservas por categoría para un periodo financiero.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoFinancieroReservasResumenDto {

    private List<GastoReservadoCategoriaResumenDto> ingresos;

    private GastoReservadoTotalesDto totalIngresos;

    private List<GastoReservadoCategoriaResumenDto> egresos;

    private GastoReservadoTotalesDto totalEgresos;

    private GastoReservadoTotalesDto totalGeneral;
}

