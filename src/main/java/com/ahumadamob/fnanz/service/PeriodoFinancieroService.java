package com.ahumadamob.fnanz.service;

import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para operaciones de {@link PeriodoFinanciero}.
 */
public interface PeriodoFinancieroService {

    PeriodoFinanciero create(PeriodoFinanciero periodoFinanciero);

    Page<PeriodoFinanciero> list(Pageable pageable);

    PeriodoFinanciero get(Long id);

    PeriodoFinanciero replace(Long id, PeriodoFinanciero periodoFinanciero);

    PeriodoFinanciero update(Long id, PeriodoFinanciero periodoFinanciero);

    void delete(Long id);
}
