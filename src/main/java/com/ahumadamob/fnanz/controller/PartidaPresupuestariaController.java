package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.PartidaPresupuestariaAplicarDto;
import com.ahumadamob.fnanz.dto.PartidaPresupuestariaCreateDto;
import com.ahumadamob.fnanz.dto.PartidaPresupuestariaPatchDto;
import com.ahumadamob.fnanz.dto.response.ApiResponseSuccessDto;
import com.ahumadamob.fnanz.dto.response.PartidaPresupuestariaResponseDto;
import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import com.ahumadamob.fnanz.entity.PartidaPresupuestaria;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.mapper.PartidaPresupuestariaMapper;
import com.ahumadamob.fnanz.service.CategoriaFinancieraService;
import com.ahumadamob.fnanz.service.PartidaPresupuestariaService;
import com.ahumadamob.fnanz.service.PeriodoFinancieroService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.ahumadamob.fnanz.controller.ResponseFactory.*;

/**
 * Controlador REST para la gestión de partidas presupuestarias.
 */
@RestController
@RequestMapping("/api/partidas-presupuestarias")
public class PartidaPresupuestariaController {

    private final PartidaPresupuestariaService partidaPresupuestariaService;
    private final CategoriaFinancieraService categoriaFinancieraService;
    private final PartidaPresupuestariaMapper partidaPresupuestariaMapper;
    private final PeriodoFinancieroService periodoFinancieroService;

    public PartidaPresupuestariaController(PartidaPresupuestariaService partidaPresupuestariaService,
                                           CategoriaFinancieraService categoriaFinancieraService,
                                           PartidaPresupuestariaMapper partidaPresupuestariaMapper,
                                           PeriodoFinancieroService periodoFinancieroService) {
        this.partidaPresupuestariaService = partidaPresupuestariaService;
        this.categoriaFinancieraService = categoriaFinancieraService;
        this.partidaPresupuestariaMapper = partidaPresupuestariaMapper;
        this.periodoFinancieroService = periodoFinancieroService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseSuccessDto<PartidaPresupuestariaResponseDto>> create(@Validated @RequestBody PartidaPresupuestariaCreateDto dto) {
        CategoriaFinanciera categoria = categoriaFinancieraService.get(dto.getCategoriaId());
        PeriodoFinanciero periodo = periodoFinancieroService.get(dto.getPeriodoId());
        PartidaPresupuestaria partida = partidaPresupuestariaMapper.toEntity(dto, categoria, periodo);
        PartidaPresupuestaria created = partidaPresupuestariaService.create(partida);
        PartidaPresupuestariaResponseDto response = partidaPresupuestariaMapper.toDto(created);
        return created(response.getId(), "Partida presupuestaria creada correctamente", response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseSuccessDto<List<PartidaPresupuestariaResponseDto>>> list(@RequestParam(required = false) String q,
                                                                                              Pageable pageable) {
        Page<PartidaPresupuestaria> page = partidaPresupuestariaService.list(q, pageable);
        Page<PartidaPresupuestariaResponseDto> dtoPage = page.map(partidaPresupuestariaMapper::toDto);
        return page(dtoPage);
    }

    @GetMapping("/periodo/{periodoId}")
    public ApiResponseSuccessDto<List<PartidaPresupuestariaResponseDto>> listByPeriodo(@PathVariable Long periodoId) {
        List<PartidaPresupuestariaResponseDto> partidas = partidaPresupuestariaService.listByPeriodo(periodoId).stream()
                .map(partidaPresupuestariaMapper::toDto)
                .toList();
        return ok(partidas);
    }

    @GetMapping("/{id}")
    public ApiResponseSuccessDto<PartidaPresupuestariaResponseDto> get(@PathVariable Long id) {
        PartidaPresupuestaria partida = partidaPresupuestariaService.get(id);
        return ok(partidaPresupuestariaMapper.toDto(partida));
    }

    @PatchMapping("/{id}")
    public ApiResponseSuccessDto<PartidaPresupuestariaResponseDto> update(@PathVariable Long id,
                                                                          @Validated @RequestBody PartidaPresupuestariaPatchDto dto) {
        CategoriaFinanciera categoria = null;
        if (dto.getCategoriaId() != null) {
            categoria = categoriaFinancieraService.get(dto.getCategoriaId());
        }
        PeriodoFinanciero periodo = dto.getPeriodoId() != null
                ? periodoFinancieroService.get(dto.getPeriodoId())
                : null;
        PartidaPresupuestaria cambios = partidaPresupuestariaMapper.toPartialEntity(dto, categoria, periodo);
        PartidaPresupuestaria updated = partidaPresupuestariaService.update(id, cambios);
        return ok(partidaPresupuestariaMapper.toDto(updated));
    }

    @PatchMapping("/{id}/aplicar")
    public ApiResponseSuccessDto<PartidaPresupuestariaResponseDto> apply(@PathVariable Long id,
            @Validated @RequestBody PartidaPresupuestariaAplicarDto dto) {
        PartidaPresupuestaria aplicada = partidaPresupuestariaService.applyMonto(id, dto.getMontoAplicado());
        return ok(partidaPresupuestariaMapper.toDto(aplicada));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        partidaPresupuestariaService.delete(id);
    }
}
