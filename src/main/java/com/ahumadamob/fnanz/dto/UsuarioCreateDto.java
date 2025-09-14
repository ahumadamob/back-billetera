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

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    private Moneda monedaBase;

    @NotBlank(message = "La zona horaria es obligatoria")
    private String zonaHoraria;
}
