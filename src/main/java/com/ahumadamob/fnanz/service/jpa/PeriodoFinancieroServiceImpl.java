package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.repository.PeriodoFinancieroRepository;
import com.ahumadamob.fnanz.service.PeriodoFinancieroService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación JPA del servicio de periodos financieros.
 */
@Service
@Transactional(readOnly = true)
public class PeriodoFinancieroServiceImpl implements PeriodoFinancieroService {

    private final PeriodoFinancieroRepository periodoFinancieroRepository;

    public PeriodoFinancieroServiceImpl(PeriodoFinancieroRepository periodoFinancieroRepository) {
        this.periodoFinancieroRepository = periodoFinancieroRepository;
    }

    @Override
    public PeriodoFinanciero get(Long id) {
        return periodoFinancieroRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }
}
