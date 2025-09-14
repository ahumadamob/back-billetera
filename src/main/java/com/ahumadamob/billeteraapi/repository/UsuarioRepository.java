package com.ahumadamob.billeteraapi.repository;

import com.ahumadamob.billeteraapi.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para la entidad {@link Usuario}.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}

