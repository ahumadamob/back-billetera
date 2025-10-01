package com.ahumadamob.fnanz.repository;

import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para {@link PeriodoFinanciero}.
 */
public interface PeriodoFinancieroRepository extends JpaRepository<PeriodoFinanciero, Long> {
}
