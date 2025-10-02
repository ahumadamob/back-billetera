package com.ahumadamob.fnanz.mapper;

import com.ahumadamob.fnanz.dto.PeriodoFinancieroCreateDto;
import com.ahumadamob.fnanz.dto.PeriodoFinancieroPatchDto;
import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroResponseDto;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Conversión entre {@link PeriodoFinanciero} y sus DTOs.
 */
@Component
public class PeriodoFinancieroMapper {

    public PeriodoFinanciero toEntity(PeriodoFinancieroCreateDto dto) {
        PeriodoFinanciero periodo = new PeriodoFinanciero();
        if (dto == null) {
            return periodo;
        }
        periodo.setNombre(dto.getNombre());
        periodo.setFechaInicio(dto.getFechaInicio());
        periodo.setFechaFin(dto.getFechaFin());
        periodo.setTipo(dto.getTipo());
        periodo.setDescripcion(dto.getDescripcion());
        if (dto.getCerrado() != null) {
            periodo.setCerrado(dto.getCerrado());
        }
        return periodo;
    }

    public PeriodoFinanciero toPartialEntity(PeriodoFinancieroPatchDto dto) {
        PeriodoFinanciero periodo = new PeriodoFinanciero();
        updateEntity(dto, periodo);
        return periodo;
    }

    public void updateEntity(PeriodoFinancieroPatchDto dto, PeriodoFinanciero periodo) {
        if (dto == null || periodo == null) {
            return;
        }
        Optional.ofNullable(dto.getNombre()).ifPresent(periodo::setNombre);
        Optional.ofNullable(dto.getFechaInicio()).ifPresent(periodo::setFechaInicio);
        Optional.ofNullable(dto.getFechaFin()).ifPresent(periodo::setFechaFin);
        Optional.ofNullable(dto.getTipo()).ifPresent(periodo::setTipo);
        Optional.ofNullable(dto.getDescripcion()).ifPresent(periodo::setDescripcion);
        if (dto.getCerrado() != null) {
            periodo.setCerrado(dto.getCerrado());
        }
    }

    public PeriodoFinancieroResponseDto toDto(PeriodoFinanciero periodo) {
        if (periodo == null) {
            return null;
        }
        return new PeriodoFinancieroResponseDto(
                periodo.getId(),
                periodo.getNombre(),
                periodo.getFechaInicio(),
                periodo.getFechaFin(),
                periodo.getTipo(),
                periodo.getDescripcion(),
                periodo.getCerrado(),
                periodo.getCreatedAt(),
                periodo.getUpdatedAt()
        );
    }
}
