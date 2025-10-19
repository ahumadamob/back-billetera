package com.ahumadamob.fnanz.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO que agrupa las categorías con sus totales para ingresos o egresos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartidaPresupuestariaResumenDetalleDto {

    private List<PartidaPresupuestariaCategoriaResumenDto> categorias;

    private PartidaPresupuestariaTotalesDto totales;
}
