package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.repository.PeriodoFinancieroRepository;
import com.ahumadamob.fnanz.service.PeriodoFinancieroService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación JPA del servicio de periodos financieros.
 */
@Service
@Transactional
public class PeriodoFinancieroServiceImpl implements PeriodoFinancieroService {

    private final PeriodoFinancieroRepository periodoFinancieroRepository;

    public PeriodoFinancieroServiceImpl(PeriodoFinancieroRepository periodoFinancieroRepository) {
        this.periodoFinancieroRepository = periodoFinancieroRepository;
    }

    @Override
    public PeriodoFinanciero create(PeriodoFinanciero periodoFinanciero) {
        ensureCerradoFlag(periodoFinanciero);
        return periodoFinancieroRepository.save(periodoFinanciero);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PeriodoFinanciero> list(Pageable pageable) {
        return periodoFinancieroRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodoFinanciero get(Long id) {
        return periodoFinancieroRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public PeriodoFinanciero replace(Long id, PeriodoFinanciero periodoFinanciero) {
        PeriodoFinanciero existente = periodoFinancieroRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        existente.setNombre(periodoFinanciero.getNombre());
        existente.setFechaInicio(periodoFinanciero.getFechaInicio());
        existente.setFechaFin(periodoFinanciero.getFechaFin());
        existente.setTipo(periodoFinanciero.getTipo());
        existente.setDescripcion(periodoFinanciero.getDescripcion());
        existente.setCerrado(periodoFinanciero.getCerrado());

        ensureCerradoFlag(existente);
        return periodoFinancieroRepository.save(existente);
    }

    @Override
    public PeriodoFinanciero update(Long id, PeriodoFinanciero cambios) {
        PeriodoFinanciero existente = periodoFinancieroRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        if (cambios.getNombre() != null) {
            existente.setNombre(cambios.getNombre());
        }
        if (cambios.getFechaInicio() != null) {
            existente.setFechaInicio(cambios.getFechaInicio());
        }
        if (cambios.getFechaFin() != null) {
            existente.setFechaFin(cambios.getFechaFin());
        }
        if (cambios.getTipo() != null) {
            existente.setTipo(cambios.getTipo());
        }
        if (cambios.getDescripcion() != null) {
            existente.setDescripcion(cambios.getDescripcion());
        }
        if (cambios.getCerrado() != null) {
            existente.setCerrado(cambios.getCerrado());
        }

        ensureCerradoFlag(existente);
        return periodoFinancieroRepository.save(existente);
    }

    @Override
    public void delete(Long id) {
        if (!periodoFinancieroRepository.existsById(id)) {
            throw new ResourceNotFoundException();
        }
        periodoFinancieroRepository.deleteById(id);
    }

    private void ensureCerradoFlag(PeriodoFinanciero periodoFinanciero) {
        if (periodoFinanciero.getCerrado() == null) {
            periodoFinanciero.setCerrado(Boolean.FALSE);
        }
    }
}
