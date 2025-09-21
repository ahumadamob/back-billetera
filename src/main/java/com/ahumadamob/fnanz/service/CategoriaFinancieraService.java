package com.ahumadamob.fnanz.service;

import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para operaciones de {@link com.ahumadamob.fnanz.entity.CategoriaFinanciera}.
 */
public interface CategoriaFinancieraService {
    CategoriaFinanciera create(CategoriaFinanciera categoria);
    Page<CategoriaFinanciera> list(String q, Pageable pageable);
    CategoriaFinanciera get(Long id);
    CategoriaFinanciera update(Long id, CategoriaFinanciera categoria);
    void delete(Long id);
}
