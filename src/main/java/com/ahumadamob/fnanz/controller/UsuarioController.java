package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.UsuarioCreateDto;
import com.ahumadamob.fnanz.dto.UsuarioPasswordDto;
import com.ahumadamob.fnanz.dto.UsuarioPatchDto;
import com.ahumadamob.fnanz.dto.response.ApiResponseSuccessDto;
import com.ahumadamob.fnanz.dto.response.UsuarioResponseDto;
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

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseSuccessDto<UsuarioResponseDto>> create(@Validated @RequestBody UsuarioCreateDto dto) {
        UsuarioResponseDto createdUser = usuarioService.create(dto);
        return created(createdUser.getId(), createdUser);
    }

    @GetMapping
    public ResponseEntity<ApiResponseSuccessDto<List<UsuarioResponseDto>>> list(@RequestParam(required = false) String q,
                                                                                @RequestParam(required = false) Boolean activo,
                                                                                Pageable pageable) {
        Page<UsuarioResponseDto> page = usuarioService.list(q, activo, pageable);
        return page(page);
    }

    @GetMapping("/{id}")
    public ApiResponseSuccessDto<UsuarioResponseDto> get(@PathVariable Long id) {
        return ok(usuarioService.get(id));
    }

    @PatchMapping("/{id}")
    public ApiResponseSuccessDto<UsuarioResponseDto> update(@PathVariable Long id, @Validated @RequestBody UsuarioPatchDto dto) {
        return ok(usuarioService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        usuarioService.delete(id);
    }

    @PostMapping("/{id}/restore")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void restore(@PathVariable Long id) {
        usuarioService.restore(id);
    }

    @PostMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changePassword(@PathVariable Long id, @Validated @RequestBody UsuarioPasswordDto dto) {
        usuarioService.changePassword(id, dto);
    }

    @GetMapping("/me")
    public ApiResponseSuccessDto<UsuarioResponseDto> me() {
        return ok(usuarioService.me());
    }
}
