package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.entity.GastoReservado;
import com.ahumadamob.fnanz.error.ResourceConflictException;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.repository.GastoReservadoRepository;
import com.ahumadamob.fnanz.service.GastoReservadoService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación JPA del servicio de gastos reservados.
 */
@Service
@Transactional
public class GastoReservadoServiceImpl implements GastoReservadoService {

    private final GastoReservadoRepository gastoReservadoRepository;

    public GastoReservadoServiceImpl(GastoReservadoRepository gastoReservadoRepository) {
        this.gastoReservadoRepository = gastoReservadoRepository;
    }

    @Override
    public GastoReservado create(GastoReservado gastoReservado) {
        validateCategoriaTipoYPeriodo(gastoReservado);
        return gastoReservadoRepository.save(gastoReservado);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<GastoReservado> list(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return gastoReservadoRepository.findAll(pageable);
        }
        String like = "%" + q.toLowerCase() + "%";
        Specification<GastoReservado> spec = (root, query, cb) -> {
            var categoriaJoin = root.join("categoria");
            var periodoJoin = root.join("periodo");
            return cb.or(
                    cb.like(cb.lower(cb.coalesce(root.get("concepto"), "")), like),
                    cb.like(cb.lower(cb.coalesce(root.get("nota"), "")), like),
                    cb.like(cb.lower(categoriaJoin.get("nombre")), like),
                    cb.like(cb.lower(cb.coalesce(periodoJoin.get("nombre"), "")), like)
            );
        };
        return gastoReservadoRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GastoReservado> listByPeriodo(Long periodoId) {
        return gastoReservadoRepository.findAllByPeriodoIdOrderByIdAsc(periodoId);
    }

    @Override
    @Transactional(readOnly = true)
    public GastoReservado get(Long id) {
        return gastoReservadoRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public GastoReservado update(Long id, GastoReservado cambios) {
        GastoReservado gasto = gastoReservadoRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        if (cambios.getTipo() != null) {
            gasto.setTipo(cambios.getTipo());
        }
        if (cambios.getCategoria() != null) {
            gasto.setCategoria(cambios.getCategoria());
        }
        if (cambios.getConcepto() != null) {
            gasto.setConcepto(cambios.getConcepto());
        }
        if (cambios.getPeriodo() != null) {
            gasto.setPeriodo(cambios.getPeriodo());
        }
        if (cambios.getEstado() != null) {
            gasto.setEstado(cambios.getEstado());
        }
        if (cambios.getMontoReservado() != null) {
            gasto.setMontoReservado(cambios.getMontoReservado());
        }
        if (cambios.getMontoAplicado() != null) {
            gasto.setMontoAplicado(cambios.getMontoAplicado());
        }
        if (cambios.getNota() != null) {
            gasto.setNota(cambios.getNota());
        }

        validateCategoriaTipoYPeriodo(gasto);
        return gastoReservadoRepository.save(gasto);
    }

    @Override
    public void delete(Long id) {
        if (!gastoReservadoRepository.existsById(id)) {
            throw new ResourceNotFoundException();
        }
        gastoReservadoRepository.deleteById(id);
    }

    private void validateCategoriaTipoYPeriodo(GastoReservado gastoReservado) {
        if (gastoReservado.getCategoria() == null) {
            throw new ResourceConflictException("categoriaId", "La categoría asociada no es válida");
        }
        if (gastoReservado.getPeriodo() == null) {
            throw new ResourceConflictException("periodoId", "El periodo asociado no es válido");
        }
        if (gastoReservado.getTipo() != null
                && gastoReservado.getCategoria().getTipo() != null
                && gastoReservado.getTipo() != gastoReservado.getCategoria().getTipo()) {
            throw new ResourceConflictException("tipo", "El tipo no coincide con la categoría asociada");
        }
    }
}
