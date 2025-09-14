package com.ahumadamob.fnanz.dto;

import com.ahumadamob.fnanz.enums.Moneda;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    private String nombre;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Size(min = 8)
    private String password;

    private Moneda monedaBase;

    @NotBlank
    private String zonaHoraria;
}
