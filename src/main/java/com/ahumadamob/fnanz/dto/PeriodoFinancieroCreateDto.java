package com.ahumadamob.fnanz.dto;

import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la creación o reemplazo completo de periodos financieros.
 */
@Getter
@Setter
@NoArgsConstructor
public class PeriodoFinancieroCreateDto {

    private String nombre;

    private LocalDate fechaInicio;

    private LocalDate fechaFin;

    private String tipo;

    private String descripcion;

    private Boolean cerrado;
}
