package com.ahumadamob.fnanz.service;

import com.ahumadamob.fnanz.dto.UsuarioCreateDto;
import com.ahumadamob.fnanz.dto.UsuarioPatchDto;
import com.ahumadamob.fnanz.dto.UsuarioPasswordDto;
import com.ahumadamob.fnanz.dto.response.UsuarioResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Servicio para operaciones de {@link com.ahumadamob.fnanz.entity.Usuario}.
 */
public interface UsuarioService {
    UsuarioResponseDto create(UsuarioCreateDto dto);
    Page<UsuarioResponseDto> list(String q, Boolean activo, Pageable pageable);
    UsuarioResponseDto get(Long id);
    UsuarioResponseDto update(Long id, UsuarioPatchDto dto);
    void delete(Long id);
    void restore(Long id);
    void changePassword(Long id, UsuarioPasswordDto dto);
    UsuarioResponseDto me();
}
