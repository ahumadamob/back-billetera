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
        usuario.setZonaHoraria(dto.getZonaHoraria());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        return usuario;
    }

    public void updateEntity(UsuarioPatchDto dto, Usuario usuario) {
        Optional.ofNullable(dto.getNombre()).ifPresent(usuario::setNombre);
        Optional.ofNullable(dto.getMonedaBase()).ifPresent(usuario::setMonedaBase);
        Optional.ofNullable(dto.getZonaHoraria()).ifPresent(usuario::setZonaHoraria);
        Optional.ofNullable(dto.getActivo()).ifPresent(usuario::setActivo);
    }

    public UsuarioResponseDto toDto(Usuario usuario) {
        return new UsuarioResponseDto(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getMonedaBase(),
                usuario.getZonaHoraria(),
                usuario.getActivo(),
                usuario.getCreatedAt(),
                usuario.getUpdatedAt()
        );
    }
}
