package com.ahumadamob.fnanz.dto;

import com.ahumadamob.fnanz.enums.Moneda;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para actualizaciones parciales de usuarios.
 */
@Getter
@Setter
@NoArgsConstructor
public class UsuarioPatchDto {
    private String nombre;
    private Moneda monedaBase;
}
