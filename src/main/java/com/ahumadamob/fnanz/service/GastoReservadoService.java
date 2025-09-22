package com.ahumadamob.fnanz.service;

import com.ahumadamob.fnanz.entity.GastoReservado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para operaciones de {@link com.ahumadamob.fnanz.entity.GastoReservado}.
 */
public interface GastoReservadoService {
    GastoReservado create(GastoReservado gastoReservado);
    Page<GastoReservado> list(String q, Pageable pageable);
    GastoReservado get(Long id);
    GastoReservado update(Long id, GastoReservado gastoReservado);
    void delete(Long id);
}
