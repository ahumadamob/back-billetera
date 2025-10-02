package com.ahumadamob.fnanz.service;

import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para operaciones de {@link PeriodoFinanciero}.
 */
public interface PeriodoFinancieroService {

    Page<PeriodoFinanciero> list(Pageable pageable);

    PeriodoFinanciero get(Long id);
}
