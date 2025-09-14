package com.ahumadamob.billeteraapi.entity;

import com.ahumadamob.billeteraapi.enums.Moneda;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa a un usuario del sistema.
 */
@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
public class Usuario extends BaseEntity {

    @Column(name = "nombre", nullable = false)
    @NotBlank
    private String nombre;

    @Column(name = "email", nullable = false, unique = true)
    @NotBlank
    @Email
    private String email;

    @Column(name = "password_hash", nullable = false)
    @NotBlank
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "moneda_base", nullable = false)
    private Moneda monedaBase;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;
}
