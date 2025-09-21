package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import com.ahumadamob.fnanz.error.ResourceConflictException;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.repository.CategoriaFinancieraRepository;
import com.ahumadamob.fnanz.service.CategoriaFinancieraService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación JPA del servicio de categorías financieras.
 */
@Service
@Transactional
public class CategoriaFinancieraServiceImpl implements CategoriaFinancieraService {

    private final CategoriaFinancieraRepository categoriaFinancieraRepository;

    public CategoriaFinancieraServiceImpl(CategoriaFinancieraRepository categoriaFinancieraRepository) {
        this.categoriaFinancieraRepository = categoriaFinancieraRepository;
    }

    @Override
    public CategoriaFinanciera create(CategoriaFinanciera categoria) {
        categoriaFinancieraRepository.findByNombre(categoria.getNombre())
                .ifPresent(existing -> {
                    throw new ResourceConflictException("nombre", "El nombre ya está en uso");
                });
        return categoriaFinancieraRepository.save(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoriaFinanciera> list(String q, Pageable pageable) {
        if (q == null || q.isBlank()) {
            return categoriaFinancieraRepository.findAll(pageable);
        }

        String like = "%" + q.toLowerCase() + "%";
        Specification<CategoriaFinanciera> spec = (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("nombre")), like),
                cb.like(cb.lower(cb.coalesce(root.get("descripcion"), "")), like)
        );
        return categoriaFinancieraRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoriaFinanciera get(Long id) {
        return categoriaFinancieraRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public CategoriaFinanciera update(Long id, CategoriaFinanciera cambios) {
        CategoriaFinanciera categoria = categoriaFinancieraRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        if (cambios.getNombre() != null && !cambios.getNombre().equals(categoria.getNombre())) {
            categoriaFinancieraRepository.findByNombre(cambios.getNombre())
                    .filter(existing -> !existing.getId().equals(id))
                    .ifPresent(existing -> {
                        throw new ResourceConflictException("nombre", "El nombre ya está en uso");
                    });
            categoria.setNombre(cambios.getNombre());
        }
        if (cambios.getTipo() != null) {
            categoria.setTipo(cambios.getTipo());
        }
        if (cambios.getActivo() != null) {
            categoria.setActivo(cambios.getActivo());
        }
        if (cambios.getOrden() != null) {
            categoria.setOrden(cambios.getOrden());
        }
        if (cambios.getDescripcion() != null) {
            categoria.setDescripcion(cambios.getDescripcion());
        }
        return categoriaFinancieraRepository.save(categoria);
    }

    @Override
    public void delete(Long id) {
        if (!categoriaFinancieraRepository.existsById(id)) {
            throw new ResourceNotFoundException();
        }
        categoriaFinancieraRepository.deleteById(id);
    }
}
