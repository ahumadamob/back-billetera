package com.ahumadamob.fnanz.dto.response;

import java.util.List;
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

    private List<PartidaPresupuestariaCategoriaResumenDto> ingresos;

    private PartidaPresupuestariaTotalesDto totalIngresos;

    private List<PartidaPresupuestariaCategoriaResumenDto> egresos;

    private PartidaPresupuestariaTotalesDto totalEgresos;

    private PartidaPresupuestariaTotalesDto totalGeneral;
}

