package com.ahumadamob.fnanz.service;

import com.ahumadamob.fnanz.entity.PartidaPresupuestaria;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para operaciones de {@link com.ahumadamob.fnanz.entity.PartidaPresupuestaria}.
 */
public interface PartidaPresupuestariaService {
    PartidaPresupuestaria create(PartidaPresupuestaria partidaPresupuestaria);
    Page<PartidaPresupuestaria> list(String q, Pageable pageable);
    PartidaPresupuestaria get(Long id);
    PartidaPresupuestaria update(Long id, PartidaPresupuestaria partidaPresupuestaria);
    void delete(Long id);
    List<PartidaPresupuestaria> listByPeriodo(Long periodoId);
}
