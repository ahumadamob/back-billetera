package com.ahumadamob.fnanz.service;

import com.ahumadamob.fnanz.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para operaciones de {@link com.ahumadamob.fnanz.entity.Usuario}.
 */
public interface UsuarioService {
    Usuario create(Usuario usuario);
    Page<Usuario> list(String q, Boolean activo, Pageable pageable);
    Usuario get(Long id);
    Usuario update(Long id, Usuario usuario);
    void delete(Long id);
    void restore(Long id);
    void changePassword(Long id, String actual, String nueva);
    Usuario me();
}
