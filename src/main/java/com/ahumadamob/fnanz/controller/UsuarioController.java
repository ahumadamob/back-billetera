package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.UsuarioCreateDto;
import com.ahumadamob.fnanz.dto.UsuarioPasswordDto;
import com.ahumadamob.fnanz.dto.UsuarioPatchDto;
import com.ahumadamob.fnanz.dto.response.ApiResponseSuccessDto;
import com.ahumadamob.fnanz.dto.response.UsuarioResponseDto;
import com.ahumadamob.fnanz.entity.Usuario;
import com.ahumadamob.fnanz.mapper.UsuarioMapper;
import com.ahumadamob.fnanz.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import static com.ahumadamob.fnanz.controller.ResponseFactory.*;

/**
 * Controlador REST para la gestión de usuarios.
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper;

    public UsuarioController(UsuarioService usuarioService, UsuarioMapper usuarioMapper) {
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponseSuccessDto<UsuarioResponseDto>> create(@Validated @RequestBody UsuarioCreateDto dto) {
        Usuario usuario = usuarioMapper.toEntity(dto);
        Usuario created = usuarioService.create(usuario);
        UsuarioResponseDto response = usuarioMapper.toDto(created);
        return created(response.getId(), "Usuario creado correctamente", response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseSuccessDto<List<UsuarioResponseDto>>> list(@RequestParam(required = false) String q,
                                                                                Pageable pageable) {
        Page<Usuario> page = usuarioService.list(q, pageable);
        Page<UsuarioResponseDto> dtoPage = page.map(usuarioMapper::toDto);
        return page(dtoPage);
    }

    @GetMapping("/{id}")
    public ApiResponseSuccessDto<UsuarioResponseDto> get(@PathVariable Long id) {
        Usuario usuario = usuarioService.get(id);
        return ok(usuarioMapper.toDto(usuario));
    }

    @PatchMapping("/{id}")
    public ApiResponseSuccessDto<UsuarioResponseDto> update(@PathVariable Long id, @Validated @RequestBody UsuarioPatchDto dto) {
        Usuario cambios = usuarioMapper.toPartialEntity(dto);
        Usuario updated = usuarioService.update(id, cambios);
        return ok(usuarioMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        usuarioService.delete(id);
    }

    @PostMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable Long id, @Validated @RequestBody UsuarioPasswordDto dto) {
        usuarioService.changePassword(id, dto.getActual(), dto.getNueva());
    }

    @GetMapping("/me")
    public ApiResponseSuccessDto<UsuarioResponseDto> me() {
        Usuario usuario = usuarioService.me();
        return ok(usuarioMapper.toDto(usuario));
    }
}
