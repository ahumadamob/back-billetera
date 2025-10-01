package com.ahumadamob.fnanz.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO de lectura para {@code PeriodoFinanciero}.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoFinancieroResponseDto {

    private Long id;

    private String nombre;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private String tipo;

    private String descripcion;

    private Boolean cerrado;

    private LocalDateTime creadoEn;

    private LocalDateTime actualizadoEn;
}
