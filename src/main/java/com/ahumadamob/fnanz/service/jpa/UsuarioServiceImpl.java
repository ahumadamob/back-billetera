package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.entity.Usuario;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.repository.UsuarioRepository;
import com.ahumadamob.fnanz.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Implementación JPA del servicio de usuarios.
 */
@Service
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Usuario create(Usuario usuario) {
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "email in use");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Usuario> list(String q, Boolean activo, Pageable pageable) {
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
        return usuarioRepository.findAll(spec, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario get(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public Usuario update(Long id, Usuario cambios) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        if (cambios.getNombre() != null) {
            usuario.setNombre(cambios.getNombre());
        }
        if (cambios.getMonedaBase() != null) {
            usuario.setMonedaBase(cambios.getMonedaBase());
        }
        if (cambios.getZonaHoraria() != null) {
            usuario.setZonaHoraria(cambios.getZonaHoraria());
        }
        if (cambios.getActivo() != null) {
            usuario.setActivo(cambios.getActivo());
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public void delete(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    public void restore(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }

    @Override
    public void changePassword(Long id, String actual, String nueva) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        if (!passwordEncoder.matches(actual, usuario.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid password");
        }
        usuario.setPasswordHash(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario me() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED);
    }
}
