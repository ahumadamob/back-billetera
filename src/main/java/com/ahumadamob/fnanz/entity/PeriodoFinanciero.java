package com.ahumadamob.fnanz.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa un periodo de planificación financiera reutilizable.
 */
@Entity
@Table(name = "periodo_financiero", uniqueConstraints = {
        @UniqueConstraint(name = "uk_periodo_financiero_nombre", columnNames = "nombre")
})
@Getter
@Setter
@NoArgsConstructor
public class PeriodoFinanciero extends BaseEntity {

    @Size(max = 80)
    @Column(name = "nombre", length = 80, unique = true)
    private String nombre;

    @NotNull
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @Size(max = 40)
    @Column(name = "tipo", length = 40)
    private String tipo;

    @Column(name = "descripcion", columnDefinition = "text")
    private String descripcion;

    @NotNull
    @Column(name = "cerrado", nullable = false)
    private Boolean cerrado = Boolean.FALSE;

    @AssertTrue(message = "La fecha de inicio debe ser anterior o igual a la fecha de fin")
    public boolean isRangoValido() {
        if (fechaInicio == null || fechaFin == null) {
            return true;
        }
        return !fechaFin.isBefore(fechaInicio);
    }
}
