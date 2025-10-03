package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.dto.response.GastoReservadoCategoriaResumenDto;
import com.ahumadamob.fnanz.dto.response.GastoReservadoTotalesDto;
import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroReservasResumenDto;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.enums.TipoFin;
import com.ahumadamob.fnanz.repository.GastoReservadoRepository;
import com.ahumadamob.fnanz.repository.PeriodoFinancieroRepository;
import com.ahumadamob.fnanz.repository.projection.GastoReservadoCategoriaResumenProjection;
import com.ahumadamob.fnanz.service.PeriodoFinancieroService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación JPA del servicio de periodos financieros.
 */
@Service
@Transactional
public class PeriodoFinancieroServiceImpl implements PeriodoFinancieroService {

    private final PeriodoFinancieroRepository periodoFinancieroRepository;
    private final GastoReservadoRepository gastoReservadoRepository;

    public PeriodoFinancieroServiceImpl(PeriodoFinancieroRepository periodoFinancieroRepository,
                                        GastoReservadoRepository gastoReservadoRepository) {
        this.periodoFinancieroRepository = periodoFinancieroRepository;
        this.gastoReservadoRepository = gastoReservadoRepository;
    }

    @Override
    public PeriodoFinanciero create(PeriodoFinanciero periodoFinanciero) {
        ensureCerradoFlag(periodoFinanciero);
        return periodoFinancieroRepository.save(periodoFinanciero);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PeriodoFinanciero> list(Pageable pageable) {
        return periodoFinancieroRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodoFinanciero get(Long id) {
        return periodoFinancieroRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @Override
    public PeriodoFinanciero replace(Long id, PeriodoFinanciero periodoFinanciero) {
        PeriodoFinanciero existente = periodoFinancieroRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        existente.setNombre(periodoFinanciero.getNombre());
        existente.setFechaInicio(periodoFinanciero.getFechaInicio());
        existente.setFechaFin(periodoFinanciero.getFechaFin());
        existente.setTipo(periodoFinanciero.getTipo());
        existente.setDescripcion(periodoFinanciero.getDescripcion());
        existente.setCerrado(periodoFinanciero.getCerrado());

        ensureCerradoFlag(existente);
        return periodoFinancieroRepository.save(existente);
    }

    @Override
    public PeriodoFinanciero update(Long id, PeriodoFinanciero cambios) {
        PeriodoFinanciero existente = periodoFinancieroRepository.findById(id)
                .orElseThrow(ResourceNotFoundException::new);

        if (cambios.getNombre() != null) {
            existente.setNombre(cambios.getNombre());
        }
        if (cambios.getFechaInicio() != null) {
            existente.setFechaInicio(cambios.getFechaInicio());
        }
        if (cambios.getFechaFin() != null) {
            existente.setFechaFin(cambios.getFechaFin());
        }
        if (cambios.getTipo() != null) {
            existente.setTipo(cambios.getTipo());
        }
        if (cambios.getDescripcion() != null) {
            existente.setDescripcion(cambios.getDescripcion());
        }
        if (cambios.getCerrado() != null) {
            existente.setCerrado(cambios.getCerrado());
        }

        ensureCerradoFlag(existente);
        return periodoFinancieroRepository.save(existente);
    }

    @Override
    public void delete(Long id) {
        if (!periodoFinancieroRepository.existsById(id)) {
            throw new ResourceNotFoundException();
        }
        periodoFinancieroRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public PeriodoFinancieroReservasResumenDto obtenerResumenReservas(Long id) {
        if (!periodoFinancieroRepository.existsById(id)) {
            throw new ResourceNotFoundException();
        }

        List<GastoReservadoCategoriaResumenProjection> resumenes =
                gastoReservadoRepository.sumByPeriodoId(id);

        List<GastoReservadoCategoriaResumenDto> ingresos = new ArrayList<>();
        List<GastoReservadoCategoriaResumenDto> egresos = new ArrayList<>();

        BigDecimal totalIngresosReservado = BigDecimal.ZERO;
        BigDecimal totalIngresosAplicado = BigDecimal.ZERO;
        BigDecimal totalEgresosReservado = BigDecimal.ZERO;
        BigDecimal totalEgresosAplicado = BigDecimal.ZERO;

        for (GastoReservadoCategoriaResumenProjection resumen : resumenes) {
            BigDecimal montoReservado = valueOrZero(resumen.getTotalMontoReservado());
            BigDecimal montoAplicado = valueOrZero(resumen.getTotalMontoAplicado());

            GastoReservadoCategoriaResumenDto dto = new GastoReservadoCategoriaResumenDto(
                    resumen.getCategoriaId(),
                    resumen.getCategoriaNombre(),
                    resumen.getTipo(),
                    resumen.getCategoriaOrden(),
                    montoReservado,
                    montoAplicado
            );

            if (resumen.getTipo() == TipoFin.INGRESO) {
                ingresos.add(dto);
                totalIngresosReservado = totalIngresosReservado.add(montoReservado);
                totalIngresosAplicado = totalIngresosAplicado.add(montoAplicado);
            } else {
                egresos.add(dto);
                totalEgresosReservado = totalEgresosReservado.add(montoReservado);
                totalEgresosAplicado = totalEgresosAplicado.add(montoAplicado);
            }
        }

        Comparator<GastoReservadoCategoriaResumenDto> comparator = Comparator
                .comparing(GastoReservadoCategoriaResumenDto::getOrden,
                        Comparator.nullsLast(Integer::compareTo))
                .thenComparing(GastoReservadoCategoriaResumenDto::getCategoriaNombre,
                        String.CASE_INSENSITIVE_ORDER);
        ingresos.sort(comparator);
        egresos.sort(comparator);

        GastoReservadoTotalesDto totalIngresos = new GastoReservadoTotalesDto(
                totalIngresosReservado, totalIngresosAplicado);
        GastoReservadoTotalesDto totalEgresos = new GastoReservadoTotalesDto(
                totalEgresosReservado, totalEgresosAplicado);
        GastoReservadoTotalesDto totalGeneral = new GastoReservadoTotalesDto(
                totalIngresosReservado.subtract(totalEgresosReservado),
                totalIngresosAplicado.subtract(totalEgresosAplicado));

        return new PeriodoFinancieroReservasResumenDto(
                ingresos,
                totalIngresos,
                egresos,
                totalEgresos,
                totalGeneral
        );
    }

    private void ensureCerradoFlag(PeriodoFinanciero periodoFinanciero) {
        if (periodoFinanciero.getCerrado() == null) {
            periodoFinanciero.setCerrado(Boolean.FALSE);
        }
    }

    private BigDecimal valueOrZero(BigDecimal value) {
        return value != null ? value : BigDecimal.ZERO;
    }
}
