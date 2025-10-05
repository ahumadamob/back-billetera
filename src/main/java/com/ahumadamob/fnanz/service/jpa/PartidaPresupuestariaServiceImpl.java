package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.entity.PartidaPresupuestaria;
import com.ahumadamob.fnanz.error.ResourceConflictException;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.repository.PartidaPresupuestariaRepository;
import com.ahumadamob.fnanz.service.PartidaPresupuestariaService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación JPA del servicio de partidas presupuestarias.
 */
@Service
@Transactional
public class PartidaPresupuestariaServiceImpl implements PartidaPresupuestariaService {

    private final PartidaPresupuestariaRepository partidaPresupuestariaRepository;

    public PartidaPresupuestariaServiceImpl(PartidaPresupuestariaRepository partidaPresupuestariaRepository) {
        this.partidaPresupuestariaRepository = partidaPresupuestariaRepository;
    }

    @Override
    public PartidaPresupuestaria create(PartidaPresupuestaria partidaPresupuestaria) {
        validateCategoriaTipoYPeriodo(partidaPresupuestaria);
        return partidaPresupuestariaRepository.save(partidaPresupuestaria);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PartidaPresupuestaria> list(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return partidaPresupuestariaRepository.findAll(pageable);
        }
        String like = "%" + q.toLowerCase() + "%";
        Specification<PartidaPresupuestaria> spec = (root, query, cb) -> {
            var categoriaJoin = root.join("categoria");
            var periodoJoin = root.join("periodo");
            return cb.or(
                    cb.like(cb.lower(cb.coalesce(root.get("concepto"), "")), like),
                    cb.like(cb.lower(cb.coalesce(root.get("nota"), "")), like),
                    cb.like(cb.lower(categoriaJoin.get("nombre")), like),
                    cb.like(cb.lower(cb.coalesce(periodoJoin.get("nombre"), "")), like)
            );
        };
        return partidaPresupuestariaRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PartidaPresupuestaria> listByPeriodo(Long periodoId) {
        return partidaPresupuestariaRepository.findAllByPeriodoIdOrderByIdAsc(periodoId);
    }

    @Override
    @Transactional(readOnly = true)
    public PartidaPresupuestaria get(Long id) {
        return partidaPresupuestariaRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public PartidaPresupuestaria update(Long id, PartidaPresupuestaria cambios) {
        PartidaPresupuestaria partida = partidaPresupuestariaRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        if (cambios.getTipo() != null) {
            partida.setTipo(cambios.getTipo());
        }
        if (cambios.getCategoria() != null) {
            partida.setCategoria(cambios.getCategoria());
        }
        if (cambios.getConcepto() != null) {
            partida.setConcepto(cambios.getConcepto());
        }
        if (cambios.getPeriodo() != null) {
            partida.setPeriodo(cambios.getPeriodo());
        }
        if (cambios.getEstado() != null) {
            partida.setEstado(cambios.getEstado());
        }
        if (cambios.getMontoReservado() != null) {
            partida.setMontoReservado(cambios.getMontoReservado());
        }
        if (cambios.getMontoAplicado() != null) {
            partida.setMontoAplicado(cambios.getMontoAplicado());
        }
        if (cambios.getNota() != null) {
            partida.setNota(cambios.getNota());
        }

        validateCategoriaTipoYPeriodo(partida);
        return partidaPresupuestariaRepository.save(partida);
    }

    @Override
    public void delete(Long id) {
        if (!partidaPresupuestariaRepository.existsById(id)) {
            throw new ResourceNotFoundException();
        }
        partidaPresupuestariaRepository.deleteById(id);
    }

    private void validateCategoriaTipoYPeriodo(PartidaPresupuestaria partidaPresupuestaria) {
        if (partidaPresupuestaria.getCategoria() == null) {
            throw new ResourceConflictException("categoriaId", "La categoría asociada no es válida");
        }
        if (partidaPresupuestaria.getPeriodo() == null) {
            throw new ResourceConflictException("periodoId", "El periodo asociado no es válido");
        }
        if (partidaPresupuestaria.getTipo() != null
                && partidaPresupuestaria.getCategoria().getTipo() != null
                && partidaPresupuestaria.getTipo() != partidaPresupuestaria.getCategoria().getTipo()) {
            throw new ResourceConflictException("tipo", "El tipo no coincide con la categoría asociada");
        }
    }
}
