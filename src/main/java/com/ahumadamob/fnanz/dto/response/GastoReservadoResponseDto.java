package com.ahumadamob.fnanz.dto.response;

import com.ahumadamob.fnanz.enums.EstadoReserva;
import com.ahumadamob.fnanz.enums.TipoFin;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de lectura de gastos reservados.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GastoReservadoResponseDto {

    private Long id;

    private TipoFin tipo;

    private Long categoriaId;

    private String categoriaNombre;

    private String concepto;

    private Long periodoId;

    private String periodoNombre;

    private LocalDate periodoFechaInicio;

    private LocalDate periodoFechaFin;

    private EstadoReserva estado;

    private BigDecimal montoReservado;

    private BigDecimal montoAplicado;

    private String nota;

    private LocalDateTime creadoEn;

    private LocalDateTime actualizadoEn;
}
