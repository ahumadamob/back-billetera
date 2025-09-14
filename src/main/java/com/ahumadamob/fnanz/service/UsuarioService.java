package com.ahumadamob.fnanz.service;

import com.ahumadamob.fnanz.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para operaciones de {@link com.ahumadamob.fnanz.entity.Usuario}.
 */
public interface UsuarioService {
    UsuarioDto create(UsuarioCreateDto dto);
    Page<UsuarioDto> list(String q, Boolean activo, Pageable pageable);
    UsuarioDto get(Long id);
    UsuarioDto update(Long id, UsuarioPatchDto dto);
    void delete(Long id);
    void restore(Long id);
    void changePassword(Long id, UsuarioPasswordDto dto);
    UsuarioDto me();
}
