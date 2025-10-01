package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.response.ApiResponseSuccessDto;
import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroResponseDto;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.mapper.PeriodoFinancieroMapper;
import com.ahumadamob.fnanz.service.PeriodoFinancieroService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.ahumadamob.fnanz.controller.ResponseFactory.ok;

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

    @GetMapping("/{id}")
    public ApiResponseSuccessDto<PeriodoFinancieroResponseDto> get(@PathVariable Long id) {
        PeriodoFinanciero periodo = periodoFinancieroService.get(id);
        return ok(periodoFinancieroMapper.toDto(periodo));
    }
}
