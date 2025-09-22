package com.ahumadamob.fnanz.entity;

import com.ahumadamob.fnanz.enums.TipoFin;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa una categoría financiera para clasificar ingresos y egresos.
 */
@Entity
@Table(name = "categoria_financiera")
@Getter
@Setter
@NoArgsConstructor
public class CategoriaFinanciera extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "nombre", nullable = false, length = 100, unique = true)
    private String nombre;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoFin tipo;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;
}
