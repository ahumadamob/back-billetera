package com.ahumadamob.fnanz.dto;

import com.ahumadamob.fnanz.enums.Moneda;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para la creación de usuarios.
 */
@Getter
@Setter
@NoArgsConstructor
public class UsuarioCreateDto {

    private String nombre;

    private String email;

    private String password;

    private Moneda monedaBase;
}
