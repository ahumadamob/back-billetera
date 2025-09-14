package com.ahumadamob.fnanz.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para cambio de contraseña.
 */
@Getter
@Setter
@NoArgsConstructor
public class UsuarioPasswordDto {
    @NotBlank(message = "La contraseña actual es obligatoria")
    private String actual;

    @NotBlank(message = "La nueva contraseña es obligatoria")
    @Size(min = 8, message = "La nueva contraseña debe tener al menos 8 caracteres")
    private String nueva;
}
