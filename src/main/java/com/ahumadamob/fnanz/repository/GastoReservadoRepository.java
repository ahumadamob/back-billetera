package com.ahumadamob.fnanz.repository;

import com.ahumadamob.fnanz.entity.GastoReservado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repositorio JPA para la entidad {@link GastoReservado}.
 */
public interface GastoReservadoRepository extends JpaRepository<GastoReservado, Long>,
        JpaSpecificationExecutor<GastoReservado> {
}
