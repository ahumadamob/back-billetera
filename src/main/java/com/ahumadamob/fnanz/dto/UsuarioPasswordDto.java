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
    @NotBlank
    private String actual;

    @NotBlank
    @Size(min = 8)
    private String nueva;
}
