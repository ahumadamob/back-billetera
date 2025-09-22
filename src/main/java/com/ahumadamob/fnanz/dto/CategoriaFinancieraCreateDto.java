package com.ahumadamob.fnanz.dto;

import com.ahumadamob.fnanz.enums.TipoFin;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la creación de categorías financieras.
 */
@Getter
@Setter
@NoArgsConstructor
public class CategoriaFinancieraCreateDto {

    private String nombre;

    private TipoFin tipo;

    private Boolean activo;

    private Integer orden;

    private String descripcion;
}
