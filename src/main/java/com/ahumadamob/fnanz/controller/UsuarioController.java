package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.*;
import com.ahumadamob.fnanz.service.UsuarioService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<UsuarioDto> create(@Validated @RequestBody UsuarioCreateDto dto) {
        UsuarioDto created = usuarioService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDto>> list(@RequestParam(required = false) String q,
                                                 @RequestParam(required = false) Boolean activo,
                                                 Pageable pageable) {
        Page<UsuarioDto> page = usuarioService.list(q, activo, pageable);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(page.getTotalElements()));
        String link = createLinkHeader(page);
        if (!link.isEmpty()) {
            headers.add(HttpHeaders.LINK, link);
        }
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public UsuarioDto get(@PathVariable Long id) {
        return usuarioService.get(id);
    }

    @PatchMapping("/{id}")
    public UsuarioDto update(@PathVariable Long id, @Validated @RequestBody UsuarioPatchDto dto) {
        return usuarioService.update(id, dto);
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
    public UsuarioDto me() {
        return usuarioService.me();
    }

    private String createLinkHeader(Page<?> page) {
        UriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentRequest();
        List<String> links = new ArrayList<>();
        if (page.hasPrevious()) {
            links.add(buildLink(builder, page.getNumber() - 1, page.getSize(), "prev"));
            links.add(buildLink(builder, 0, page.getSize(), "first"));
        }
        if (page.hasNext()) {
            links.add(buildLink(builder, page.getNumber() + 1, page.getSize(), "next"));
            links.add(buildLink(builder, page.getTotalPages() - 1, page.getSize(), "last"));
        }
        return String.join(", ", links);
    }

    private String buildLink(UriComponentsBuilder builder, int page, int size, String rel) {
        String uri = builder.replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .toUriString();
        return "<" + uri + ">; rel=\"" + rel + "\"";
    }
}
