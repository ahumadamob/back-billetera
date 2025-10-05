package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.PeriodoFinancieroCreateDto;
import com.ahumadamob.fnanz.dto.PeriodoFinancieroPatchDto;
import com.ahumadamob.fnanz.dto.response.ApiResponseSuccessDto;
import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroDropdownDto;
import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroPartidasResumenDto;
import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroResponseDto;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.mapper.PeriodoFinancieroMapper;
import com.ahumadamob.fnanz.service.PeriodoFinancieroService;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.ahumadamob.fnanz.controller.ResponseFactory.created;
import static com.ahumadamob.fnanz.controller.ResponseFactory.ok;
import static com.ahumadamob.fnanz.controller.ResponseFactory.page;

/**
 * Controlador REST para periodos financieros reutilizables.
 */
@RestController
@RequestMapping("/api/periodos-financieros")
public class PeriodoFinancieroController {

    private final PeriodoFinancieroService periodoFinancieroService;
    private final PeriodoFinancieroMapper periodoFinancieroMapper;

    public PeriodoFinancieroController(PeriodoFinancieroService periodoFinancieroService,
                                       PeriodoFinancieroMapper periodoFinancieroMapper) {
        this.periodoFinancieroService = periodoFinancieroService;
        this.periodoFinancieroMapper = periodoFinancieroMapper;
    }

    @PostMapping
    public ResponseEntity<ApiResponseSuccessDto<PeriodoFinancieroResponseDto>> create(
            @Validated @RequestBody PeriodoFinancieroCreateDto dto) {
        PeriodoFinanciero periodo = periodoFinancieroMapper.toEntity(dto);
        PeriodoFinanciero createdPeriodo = periodoFinancieroService.create(periodo);
        PeriodoFinancieroResponseDto response = periodoFinancieroMapper.toDto(createdPeriodo);
        return created(response.getId(), "Periodo financiero creado correctamente", response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseSuccessDto<List<PeriodoFinancieroResponseDto>>> list(Pageable pageable) {
        Page<PeriodoFinanciero> pageResult = periodoFinancieroService.list(pageable);
        Page<PeriodoFinancieroResponseDto> dtoPage = pageResult.map(periodoFinancieroMapper::toDto);
        return page(dtoPage);
    }

    @GetMapping("/dropdown")
    public ApiResponseSuccessDto<List<PeriodoFinancieroDropdownDto>> listForDropdown(
            @RequestParam(name = "soloAbiertos", defaultValue = "false") boolean soloAbiertos) {
        List<PeriodoFinanciero> periodos = periodoFinancieroService.listarParaDropdown(soloAbiertos);
        List<PeriodoFinancieroDropdownDto> dtoList = periodos.stream()
                .map(periodoFinancieroMapper::toDropdownDto)
                .toList();
        return ok(dtoList);
    }

    @GetMapping("/{id}")
    public ApiResponseSuccessDto<PeriodoFinancieroResponseDto> get(@PathVariable Long id) {
        PeriodoFinanciero periodo = periodoFinancieroService.get(id);
        return ok(periodoFinancieroMapper.toDto(periodo));
    }

    @GetMapping("/{id}/partidas-resumen")
    public ApiResponseSuccessDto<PeriodoFinancieroPartidasResumenDto> getResumenPartidas(
            @PathVariable Long id) {
        PeriodoFinancieroPartidasResumenDto resumen = periodoFinancieroService.obtenerResumenPartidas(id);
        return ok(resumen);
    }

    @PutMapping("/{id}")
    public ApiResponseSuccessDto<PeriodoFinancieroResponseDto> replace(@PathVariable Long id,
            @Validated @RequestBody PeriodoFinancieroCreateDto dto) {
        PeriodoFinanciero cambios = periodoFinancieroMapper.toEntity(dto);
        PeriodoFinanciero actualizado = periodoFinancieroService.replace(id, cambios);
        return ok(periodoFinancieroMapper.toDto(actualizado));
    }

    @PatchMapping("/{id}")
    public ApiResponseSuccessDto<PeriodoFinancieroResponseDto> update(@PathVariable Long id,
            @Validated @RequestBody PeriodoFinancieroPatchDto dto) {
        PeriodoFinanciero cambios = periodoFinancieroMapper.toPartialEntity(dto);
        PeriodoFinanciero actualizado = periodoFinancieroService.update(id, cambios);
        return ok(periodoFinancieroMapper.toDto(actualizado));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        periodoFinancieroService.delete(id);
    }
}
