package com.ahumadamob.fnanz.mapper;

import com.ahumadamob.fnanz.dto.GastoReservadoCreateDto;
import com.ahumadamob.fnanz.dto.GastoReservadoPatchDto;
import com.ahumadamob.fnanz.dto.response.GastoReservadoResponseDto;
import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import com.ahumadamob.fnanz.entity.GastoReservado;
import com.ahumadamob.fnanz.enums.EstadoReserva;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Especialista en transformar DTOs de GastoReservado a entidades y viceversa.
 */
@Component
public class GastoReservadoMapper {

    public GastoReservado toEntity(GastoReservadoCreateDto dto, CategoriaFinanciera categoria) {
        GastoReservado gasto = new GastoReservado();
        gasto.setTipo(dto.getTipo());
        gasto.setCategoria(categoria);
        gasto.setConcepto(dto.getConcepto());
        gasto.setPeriodoFecha(dto.getPeriodoFecha());
        gasto.setFechaVencimiento(dto.getFechaVencimiento());
        gasto.setEstado(Optional.ofNullable(dto.getEstado()).orElse(EstadoReserva.RESERVADO));
        gasto.setMontoReservado(dto.getMontoReservado());
        gasto.setMontoAplicado(dto.getMontoAplicado());
        gasto.setNota(dto.getNota());
        return gasto;
    }

    public GastoReservado toPartialEntity(GastoReservadoPatchDto dto, CategoriaFinanciera categoria) {
        GastoReservado gasto = new GastoReservado();
        updateEntity(dto, gasto, categoria);
        return gasto;
    }

    public void updateEntity(GastoReservadoPatchDto dto, GastoReservado gasto, CategoriaFinanciera categoria) {
        Optional.ofNullable(dto.getTipo()).ifPresent(gasto::setTipo);
        if (categoria != null) {
            gasto.setCategoria(categoria);
        }
        Optional.ofNullable(dto.getConcepto()).ifPresent(gasto::setConcepto);
        Optional.ofNullable(dto.getPeriodoFecha()).ifPresent(gasto::setPeriodoFecha);
        Optional.ofNullable(dto.getFechaVencimiento()).ifPresent(gasto::setFechaVencimiento);
        Optional.ofNullable(dto.getEstado()).ifPresent(gasto::setEstado);
        Optional.ofNullable(dto.getMontoReservado()).ifPresent(gasto::setMontoReservado);
        Optional.ofNullable(dto.getMontoAplicado()).ifPresent(gasto::setMontoAplicado);
        Optional.ofNullable(dto.getNota()).ifPresent(gasto::setNota);
    }

    public GastoReservadoResponseDto toDto(GastoReservado gasto) {
        CategoriaFinanciera categoria = gasto.getCategoria();
        return new GastoReservadoResponseDto(
                gasto.getId(),
                gasto.getTipo(),
                categoria != null ? categoria.getId() : null,
                categoria != null ? categoria.getNombre() : null,
                gasto.getConcepto(),
                gasto.getPeriodoFecha(),
                gasto.getFechaVencimiento(),
                gasto.getEstado(),
                gasto.getMontoReservado(),
                gasto.getMontoAplicado(),
                gasto.getNota(),
                gasto.getCreatedAt(),
                gasto.getUpdatedAt()
        );
    }
}
