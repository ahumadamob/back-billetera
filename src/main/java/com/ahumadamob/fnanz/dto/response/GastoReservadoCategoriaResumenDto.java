package com.ahumadamob.fnanz.dto.response;

import com.ahumadamob.fnanz.enums.TipoFin;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO con la suma de montos reservados y aplicados por categoría.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GastoReservadoCategoriaResumenDto {

    private Long categoriaId;

    private String categoriaNombre;

    private TipoFin tipo;

    private Integer orden;

    private BigDecimal montoReservado;

    private BigDecimal montoAplicado;
}

