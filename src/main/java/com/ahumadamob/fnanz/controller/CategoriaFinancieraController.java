package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.CategoriaFinancieraCreateDto;
import com.ahumadamob.fnanz.dto.CategoriaFinancieraPatchDto;
import com.ahumadamob.fnanz.dto.response.ApiResponseSuccessDto;
import com.ahumadamob.fnanz.dto.response.CategoriaFinancieraResponseDto;
import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import com.ahumadamob.fnanz.mapper.CategoriaFinancieraMapper;
import com.ahumadamob.fnanz.service.CategoriaFinancieraService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.ahumadamob.fnanz.controller.ResponseFactory.*;

/**
 * Controlador REST para la gestión de categorías financieras.
 */
@RestController
@RequestMapping("/api/categorias-financieras")
public class CategoriaFinancieraController {

    private final CategoriaFinancieraService categoriaFinancieraService;
    private final CategoriaFinancieraMapper categoriaFinancieraMapper;

    public CategoriaFinancieraController(CategoriaFinancieraService categoriaFinancieraService,
                                         CategoriaFinancieraMapper categoriaFinancieraMapper) {
        this.categoriaFinancieraService = categoriaFinancieraService;
        this.categoriaFinancieraMapper = categoriaFinancieraMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponseSuccessDto<CategoriaFinancieraResponseDto>> create(@Validated @RequestBody CategoriaFinancieraCreateDto dto) {
        CategoriaFinanciera categoria = categoriaFinancieraMapper.toEntity(dto);
        CategoriaFinanciera saved = categoriaFinancieraService.create(categoria);
        CategoriaFinancieraResponseDto response = categoriaFinancieraMapper.toDto(saved);
        return created(response.getId(), "Categoría financiera creada correctamente", response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseSuccessDto<List<CategoriaFinancieraResponseDto>>> list(@RequestParam(required = false) String q,
                                                                                            Pageable pageable) {
        Page<CategoriaFinanciera> page = categoriaFinancieraService.list(q, pageable);
        Page<CategoriaFinancieraResponseDto> dtoPage = page.map(categoriaFinancieraMapper::toDto);
        return page(dtoPage);
    }

    @GetMapping("/{id}")
    public ApiResponseSuccessDto<CategoriaFinancieraResponseDto> get(@PathVariable Long id) {
        CategoriaFinanciera categoria = categoriaFinancieraService.get(id);
        return ok(categoriaFinancieraMapper.toDto(categoria));
    }

    @PatchMapping("/{id}")
    public ApiResponseSuccessDto<CategoriaFinancieraResponseDto> update(@PathVariable Long id,
                                                                        @Validated @RequestBody CategoriaFinancieraPatchDto dto) {
        CategoriaFinanciera cambios = categoriaFinancieraMapper.toPartialEntity(dto);
        CategoriaFinanciera updated = categoriaFinancieraService.update(id, cambios);
        return ok(categoriaFinancieraMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        categoriaFinancieraService.delete(id);
    }
}
