package com.ahumadamob.fnanz.entity;

import com.ahumadamob.fnanz.enums.EstadoReserva;
import com.ahumadamob.fnanz.enums.TipoFin;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa una reserva de gasto/ingreso planificado.
 */
@Entity
@Table(name = "gasto_reservado")
@Getter
@Setter
@NoArgsConstructor
public class GastoReservado extends BaseEntity {

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoFin tipo;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_reserva_categoria"))
    private CategoriaFinanciera categoria;

    @Size(max = 120)
    @Column(name = "concepto", length = 120)
    private String concepto;

    /** Usar el 1er día del mes como convención de periodo */
    @NotNull
    @Column(name = "periodo_fecha", nullable = false)
    private LocalDate periodoFecha;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 12)
    private EstadoReserva estado = EstadoReserva.RESERVADO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Digits(integer = 14, fraction = 2)
    @Column(name = "monto_reservado", nullable = false, precision = 14, scale = 2)
    private BigDecimal montoReservado;

    @DecimalMin(value = "0.00")
    @Digits(integer = 14, fraction = 2)
    @Column(name = "monto_aplicado", precision = 14, scale = 2)
    private BigDecimal montoAplicado;

    @Column(name = "nota", columnDefinition = "text")
    private String nota;
}
