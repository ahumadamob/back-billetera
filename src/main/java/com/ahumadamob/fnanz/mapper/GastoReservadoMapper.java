package com.ahumadamob.fnanz.mapper;

import com.ahumadamob.fnanz.dto.GastoReservadoCreateDto;
import com.ahumadamob.fnanz.dto.GastoReservadoPatchDto;
import com.ahumadamob.fnanz.dto.response.GastoReservadoResponseDto;
import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import com.ahumadamob.fnanz.entity.GastoReservado;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.enums.EstadoReserva;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Especialista en transformar DTOs de GastoReservado a entidades y viceversa.
 */
@Component
public class GastoReservadoMapper {

    public GastoReservado toEntity(GastoReservadoCreateDto dto, CategoriaFinanciera categoria,
            PeriodoFinanciero periodo) {
        GastoReservado gasto = new GastoReservado();
        gasto.setTipo(dto.getTipo());
        gasto.setCategoria(categoria);
        gasto.setConcepto(dto.getConcepto());
        gasto.setPeriodo(periodo);
        gasto.setEstado(Optional.ofNullable(dto.getEstado()).orElse(EstadoReserva.RESERVADO));
        gasto.setMontoReservado(dto.getMontoReservado());
        gasto.setMontoAplicado(dto.getMontoAplicado());
        gasto.setNota(dto.getNota());
        return gasto;
    }

    public GastoReservado toPartialEntity(GastoReservadoPatchDto dto, CategoriaFinanciera categoria,
            PeriodoFinanciero periodo) {
        GastoReservado gasto = new GastoReservado();
        updateEntity(dto, gasto, categoria, periodo);
        return gasto;
    }

    public void updateEntity(GastoReservadoPatchDto dto, GastoReservado gasto, CategoriaFinanciera categoria,
            PeriodoFinanciero periodo) {
        Optional.ofNullable(dto.getTipo()).ifPresent(gasto::setTipo);
        if (categoria != null) {
            gasto.setCategoria(categoria);
        }
        if (periodo != null) {
            gasto.setPeriodo(periodo);
        }
        Optional.ofNullable(dto.getConcepto()).ifPresent(gasto::setConcepto);
        Optional.ofNullable(dto.getEstado()).ifPresent(gasto::setEstado);
        Optional.ofNullable(dto.getMontoReservado()).ifPresent(gasto::setMontoReservado);
        Optional.ofNullable(dto.getMontoAplicado()).ifPresent(gasto::setMontoAplicado);
        Optional.ofNullable(dto.getNota()).ifPresent(gasto::setNota);
    }

    public GastoReservadoResponseDto toDto(GastoReservado gasto) {
        CategoriaFinanciera categoria = gasto.getCategoria();
        PeriodoFinanciero periodo = gasto.getPeriodo();
        return new GastoReservadoResponseDto(
                gasto.getId(),
                gasto.getTipo(),
                categoria != null ? categoria.getId() : null,
                categoria != null ? categoria.getNombre() : null,
                gasto.getConcepto(),
                periodo != null ? periodo.getId() : null,
                periodo != null ? periodo.getNombre() : null,
                periodo != null ? periodo.getFechaInicio() : null,
                periodo != null ? periodo.getFechaFin() : null,
                gasto.getEstado(),
                gasto.getMontoReservado(),
                gasto.getMontoAplicado(),
                gasto.getNota(),
                gasto.getCreatedAt(),
                gasto.getUpdatedAt()
        );
    }
}
