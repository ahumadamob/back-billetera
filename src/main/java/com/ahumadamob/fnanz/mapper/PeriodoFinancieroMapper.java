package com.ahumadamob.fnanz.mapper;

import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroResponseDto;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import org.springframework.stereotype.Component;

/**
 * Conversión entre {@link PeriodoFinanciero} y sus DTOs.
 */
@Component
public class PeriodoFinancieroMapper {

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
