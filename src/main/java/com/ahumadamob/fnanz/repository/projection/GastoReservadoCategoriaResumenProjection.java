package com.ahumadamob.fnanz.repository.projection;

import com.ahumadamob.fnanz.enums.TipoFin;
import java.math.BigDecimal;

/**
 * Proyección para obtener los totales de reservas agrupados por categoría.
 */
public interface GastoReservadoCategoriaResumenProjection {

    Long getCategoriaId();

    String getCategoriaNombre();

    TipoFin getTipo();

    Integer getCategoriaOrden();

    BigDecimal getTotalMontoReservado();

    BigDecimal getTotalMontoAplicado();
}

