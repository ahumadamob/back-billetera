package com.ahumadamob.fnanz.repository;

import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link CategoriaFinanciera}.
 */
public interface CategoriaFinancieraRepository extends JpaRepository<CategoriaFinanciera, Long>,
        JpaSpecificationExecutor<CategoriaFinanciera> {
    Optional<CategoriaFinanciera> findByNombre(String nombre);
}
