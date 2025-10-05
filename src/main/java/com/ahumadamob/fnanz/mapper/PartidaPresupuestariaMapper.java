package com.ahumadamob.fnanz.mapper;

import com.ahumadamob.fnanz.dto.PartidaPresupuestariaCreateDto;
import com.ahumadamob.fnanz.dto.PartidaPresupuestariaPatchDto;
import com.ahumadamob.fnanz.dto.response.PartidaPresupuestariaResponseDto;
import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import com.ahumadamob.fnanz.entity.PartidaPresupuestaria;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.enums.EstadoReserva;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Especialista en transformar DTOs de PartidaPresupuestaria a entidades y viceversa.
 */
@Component
public class PartidaPresupuestariaMapper {

    public PartidaPresupuestaria toEntity(PartidaPresupuestariaCreateDto dto, CategoriaFinanciera categoria,
            PeriodoFinanciero periodo) {
        PartidaPresupuestaria partida = new PartidaPresupuestaria();
        partida.setTipo(dto.getTipo());
        partida.setCategoria(categoria);
        partida.setConcepto(dto.getConcepto());
        partida.setPeriodo(periodo);
        partida.setEstado(Optional.ofNullable(dto.getEstado()).orElse(EstadoReserva.RESERVADO));
        partida.setMontoReservado(dto.getMontoReservado());
        partida.setMontoAplicado(dto.getMontoAplicado());
        partida.setNota(dto.getNota());
        return partida;
    }

    public PartidaPresupuestaria toPartialEntity(PartidaPresupuestariaPatchDto dto, CategoriaFinanciera categoria,
            PeriodoFinanciero periodo) {
        PartidaPresupuestaria partida = new PartidaPresupuestaria();
        updateEntity(dto, partida, categoria, periodo);
        return partida;
    }

    public void updateEntity(PartidaPresupuestariaPatchDto dto, PartidaPresupuestaria partida,
            CategoriaFinanciera categoria, PeriodoFinanciero periodo) {
        Optional.ofNullable(dto.getTipo()).ifPresent(partida::setTipo);
        if (categoria != null) {
            partida.setCategoria(categoria);
        }
        if (periodo != null) {
            partida.setPeriodo(periodo);
        }
        Optional.ofNullable(dto.getConcepto()).ifPresent(partida::setConcepto);
        Optional.ofNullable(dto.getEstado()).ifPresent(partida::setEstado);
        Optional.ofNullable(dto.getMontoReservado()).ifPresent(partida::setMontoReservado);
        Optional.ofNullable(dto.getMontoAplicado()).ifPresent(partida::setMontoAplicado);
        Optional.ofNullable(dto.getNota()).ifPresent(partida::setNota);
    }

    public PartidaPresupuestariaResponseDto toDto(PartidaPresupuestaria partida) {
        CategoriaFinanciera categoria = partida.getCategoria();
        PeriodoFinanciero periodo = partida.getPeriodo();
        return new PartidaPresupuestariaResponseDto(
                partida.getId(),
                partida.getTipo(),
                categoria != null ? categoria.getId() : null,
                categoria != null ? categoria.getNombre() : null,
                partida.getConcepto(),
                periodo != null ? periodo.getId() : null,
                periodo != null ? periodo.getNombre() : null,
                periodo != null ? periodo.getFechaInicio() : null,
                periodo != null ? periodo.getFechaFin() : null,
                partida.getEstado(),
                partida.getMontoReservado(),
                partida.getMontoAplicado(),
                partida.getNota(),
                partida.getCreatedAt(),
                partida.getUpdatedAt()
        );
    }
}
