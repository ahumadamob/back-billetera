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
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El email ya está en uso");
        }
        if (usuarioRepository.findByNombre(usuario.getNombre()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre ya está en uso");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Usuario> list(String q, Pageable pageable) {
        Specification<Usuario> spec = Specification.where(null);
        if (q != null && !q.isBlank()) {
            String like = "%" + q.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("nombre")), like),
                cb.like(cb.lower(root.get("email")), like)
            ));
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
        if (cambios.getNombre() != null && !cambios.getNombre().equals(usuario.getNombre())) {
            if (usuarioRepository.findByNombre(cambios.getNombre()).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre ya está en uso");
            }
            usuario.setNombre(cambios.getNombre());
        }
        if (cambios.getMonedaBase() != null) {
            usuario.setMonedaBase(cambios.getMonedaBase());
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public void delete(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException();
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public void changePassword(Long id, String actual, String nueva) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
        if (!passwordEncoder.matches(actual, usuario.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "La contraseña actual no es válida");
        }
        usuario.setPasswordHash(passwordEncoder.encode(nueva));
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario me() {
        throw new ResponseStatusException(HttpStatus.NOT_IMPLEMENTED, "No implementado");
    }
}
