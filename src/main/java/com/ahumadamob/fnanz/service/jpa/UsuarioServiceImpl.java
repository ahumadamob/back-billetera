package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.dto.UsuarioCreateDto;
import com.ahumadamob.fnanz.dto.UsuarioPatchDto;
import com.ahumadamob.fnanz.dto.UsuarioPasswordDto;
import com.ahumadamob.fnanz.dto.response.UsuarioResponseDto;
import com.ahumadamob.fnanz.entity.Usuario;
import com.ahumadamob.fnanz.repository.UsuarioRepository;
import com.ahumadamob.fnanz.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

/**
 * Implementación JPA del servicio de usuarios.
 */
@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UsuarioResponseDto create(UsuarioCreateDto dto) {
        if (usuarioRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email in use");
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setEmail(dto.getEmail());
        usuario.setMonedaBase(dto.getMonedaBase());
        usuario.setZonaHoraria(dto.getZonaHoraria());
        usuario.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        Usuario saved = usuarioRepository.save(usuario);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponseDto> list(String q, Boolean activo, Pageable pageable) {
        Specification<Usuario> spec = Specification.where(null);
        if (q != null && !q.isBlank()) {
            String like = "%" + q.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("nombre")), like),
                cb.like(cb.lower(root.get("email")), like)
            ));
        }
        if (activo != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("activo"), activo));
        }
        return usuarioRepository.findAll(spec, pageable).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto get(Long id) {
        return toDto(usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Override
    public UsuarioResponseDto update(Long id, UsuarioPatchDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        Optional.ofNullable(dto.getNombre()).ifPresent(usuario::setNombre);
        Optional.ofNullable(dto.getMonedaBase()).ifPresent(usuario::setMonedaBase);
        Optional.ofNullable(dto.getZonaHoraria()).ifPresent(usuario::setZonaHoraria);
        Optional.ofNullable(dto.getActivo()).ifPresent(usuario::setActivo);
        Usuario saved = usuarioRepository.save(usuario);
        return toDto(saved);
    }

    @Override
    public void delete(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    public void restore(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    @Override
    public void changePassword(Long id, UsuarioPasswordDto dto) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!passwordEncoder.matches(dto.getActual(), usuario.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid password");
        }
        usuario.setPasswordHash(passwordEncoder.encode(dto.getNueva()));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDto me() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }

    private UsuarioResponseDto toDto(Usuario usuario) {
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
