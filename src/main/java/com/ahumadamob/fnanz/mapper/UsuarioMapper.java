package com.ahumadamob.fnanz.mapper;

import com.ahumadamob.fnanz.dto.UsuarioCreateDto;
import com.ahumadamob.fnanz.dto.UsuarioPatchDto;
import com.ahumadamob.fnanz.dto.response.UsuarioResponseDto;
import com.ahumadamob.fnanz.entity.Usuario;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Especialista en transformar DTOs de Usuario a entidades y viceversa.
 */
@Component
public class UsuarioMapper {

    private final PasswordEncoder passwordEncoder;

    public UsuarioMapper(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario toEntity(UsuarioCreateDto dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setMonedaBase(dto.getMonedaBase());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        return usuario;
    }

    public Usuario toPartialEntity(UsuarioPatchDto dto) {
        Usuario usuario = new Usuario();
        updateEntity(dto, usuario);
        return usuario;
    }

    public void updateEntity(UsuarioPatchDto dto, Usuario usuario) {
        Optional.ofNullable(dto.getNombre()).ifPresent(usuario::setNombre);
        Optional.ofNullable(dto.getMonedaBase()).ifPresent(usuario::setMonedaBase);
    }

    public UsuarioResponseDto toDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getMonedaBase(),
                usuario.getCreatedAt(),
                usuario.getUpdatedAt()
        );
    }
}
