package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.GastoReservadoCreateDto;
import com.ahumadamob.fnanz.dto.GastoReservadoPatchDto;
import com.ahumadamob.fnanz.dto.response.ApiResponseSuccessDto;
import com.ahumadamob.fnanz.dto.response.GastoReservadoResponseDto;
import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import com.ahumadamob.fnanz.entity.GastoReservado;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.mapper.GastoReservadoMapper;
import com.ahumadamob.fnanz.service.CategoriaFinancieraService;
import com.ahumadamob.fnanz.service.GastoReservadoService;
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
 * Controlador REST para la gestión de gastos reservados.
 */
@RestController
@RequestMapping("/api/gastos-reservados")
public class GastoReservadoController {

    private final GastoReservadoService gastoReservadoService;
    private final CategoriaFinancieraService categoriaFinancieraService;
    private final GastoReservadoMapper gastoReservadoMapper;
    private final PeriodoFinancieroService periodoFinancieroService;

    public GastoReservadoController(GastoReservadoService gastoReservadoService,
                                    CategoriaFinancieraService categoriaFinancieraService,
                                    GastoReservadoMapper gastoReservadoMapper,
                                    PeriodoFinancieroService periodoFinancieroService) {
        this.gastoReservadoService = gastoReservadoService;
        this.categoriaFinancieraService = categoriaFinancieraService;
        this.gastoReservadoMapper = gastoReservadoMapper;
        this.periodoFinancieroService = periodoFinancieroService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseSuccessDto<GastoReservadoResponseDto>> create(@Validated @RequestBody GastoReservadoCreateDto dto) {
        CategoriaFinanciera categoria = categoriaFinancieraService.get(dto.getCategoriaId());
        PeriodoFinanciero periodo = periodoFinancieroService.get(dto.getPeriodoId());
        GastoReservado gasto = gastoReservadoMapper.toEntity(dto, categoria, periodo);
        GastoReservado created = gastoReservadoService.create(gasto);
        GastoReservadoResponseDto response = gastoReservadoMapper.toDto(created);
        return created(response.getId(), "Gasto reservado creado correctamente", response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseSuccessDto<List<GastoReservadoResponseDto>>> list(@RequestParam(required = false) String q,
                                                                                       Pageable pageable) {
        Page<GastoReservado> page = gastoReservadoService.list(q, pageable);
        Page<GastoReservadoResponseDto> dtoPage = page.map(gastoReservadoMapper::toDto);
        return page(dtoPage);
    }

    @GetMapping("/periodo/{periodoId}")
    public ApiResponseSuccessDto<List<GastoReservadoResponseDto>> listByPeriodo(@PathVariable Long periodoId) {
        List<GastoReservadoResponseDto> gastos = gastoReservadoService.listByPeriodo(periodoId).stream()
                .map(gastoReservadoMapper::toDto)
                .toList();
        return ok(gastos);
    }

    @GetMapping("/{id}")
    public ApiResponseSuccessDto<GastoReservadoResponseDto> get(@PathVariable Long id) {
        GastoReservado gasto = gastoReservadoService.get(id);
        return ok(gastoReservadoMapper.toDto(gasto));
    }

    @PatchMapping("/{id}")
    public ApiResponseSuccessDto<GastoReservadoResponseDto> update(@PathVariable Long id,
                                                                    @Validated @RequestBody GastoReservadoPatchDto dto) {
        CategoriaFinanciera categoria = null;
        if (dto.getCategoriaId() != null) {
            categoria = categoriaFinancieraService.get(dto.getCategoriaId());
        }
        PeriodoFinanciero periodo = dto.getPeriodoId() != null
                ? periodoFinancieroService.get(dto.getPeriodoId())
                : null;
        GastoReservado cambios = gastoReservadoMapper.toPartialEntity(dto, categoria, periodo);
        GastoReservado updated = gastoReservadoService.update(id, cambios);
        return ok(gastoReservadoMapper.toDto(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        gastoReservadoService.delete(id);
    }
}
