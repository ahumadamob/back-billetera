package com.ahumadamob.fnanz.repository;

import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para {@link PeriodoFinanciero}.
 */
public interface PeriodoFinancieroRepository extends JpaRepository<PeriodoFinanciero, Long> {

    List<PeriodoFinanciero> findAllByOrderByFechaInicioAsc();

    List<PeriodoFinanciero> findAllByCerradoFalseOrderByFechaInicioAsc();
}
