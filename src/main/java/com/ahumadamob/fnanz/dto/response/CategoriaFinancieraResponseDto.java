package com.ahumadamob.fnanz.dto.response;

import com.ahumadamob.fnanz.enums.TipoFin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de lectura de categorías financieras.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaFinancieraResponseDto {

    private Long id;

    private String nombre;

    private TipoFin tipo;

    private boolean activo;

    private Integer orden;

    private String descripcion;

    private LocalDateTime creadoEn;

    private LocalDateTime actualizadoEn;
}
