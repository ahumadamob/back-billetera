package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.dto.response.PartidaPresupuestariaCategoriaResumenDto;
import com.ahumadamob.fnanz.dto.response.PartidaPresupuestariaTotalesDto;
import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroPartidasResumenDto;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.enums.TipoFin;
import com.ahumadamob.fnanz.repository.PartidaPresupuestariaRepository;
import com.ahumadamob.fnanz.repository.PeriodoFinancieroRepository;
import com.ahumadamob.fnanz.repository.projection.PartidaPresupuestariaCategoriaResumenProjection;
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
    private final PartidaPresupuestariaRepository partidaPresupuestariaRepository;

    public PeriodoFinancieroServiceImpl(PeriodoFinancieroRepository periodoFinancieroRepository,
                                        PartidaPresupuestariaRepository partidaPresupuestariaRepository) {
        this.periodoFinancieroRepository = periodoFinancieroRepository;
        this.partidaPresupuestariaRepository = partidaPresupuestariaRepository;
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
    public List<PeriodoFinanciero> listarParaDropdown(boolean soloAbiertos) {
        if (soloAbiertos) {
            return periodoFinancieroRepository.findAllByCerradoFalseOrderByFechaInicioAsc();
        }
        return periodoFinancieroRepository.findAllByOrderByFechaInicioAsc();
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
    public PeriodoFinancieroPartidasResumenDto obtenerResumenPartidas(Long id) {
        if (!periodoFinancieroRepository.existsById(id)) {
            throw new ResourceNotFoundException();
        }

        List<PartidaPresupuestariaCategoriaResumenProjection> resumenes =
                partidaPresupuestariaRepository.sumByPeriodoId(id);

        List<PartidaPresupuestariaCategoriaResumenDto> ingresos = new ArrayList<>();
        List<PartidaPresupuestariaCategoriaResumenDto> egresos = new ArrayList<>();

        BigDecimal totalIngresosReservado = BigDecimal.ZERO;
        BigDecimal totalIngresosAplicado = BigDecimal.ZERO;
        BigDecimal totalEgresosReservado = BigDecimal.ZERO;
        BigDecimal totalEgresosAplicado = BigDecimal.ZERO;

        for (PartidaPresupuestariaCategoriaResumenProjection resumen : resumenes) {
            BigDecimal montoReservado = valueOrZero(resumen.getTotalMontoReservado());
            BigDecimal montoAplicado = valueOrZero(resumen.getTotalMontoAplicado());

            PartidaPresupuestariaCategoriaResumenDto dto = new PartidaPresupuestariaCategoriaResumenDto(
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

        Comparator<PartidaPresupuestariaCategoriaResumenDto> comparator = Comparator
                .comparing(PartidaPresupuestariaCategoriaResumenDto::getOrden,
                        Comparator.nullsLast(Integer::compareTo))
                .thenComparing(PartidaPresupuestariaCategoriaResumenDto::getCategoriaNombre,
                        String.CASE_INSENSITIVE_ORDER);
        ingresos.sort(comparator);
        egresos.sort(comparator);

        PartidaPresupuestariaTotalesDto totalIngresos = new PartidaPresupuestariaTotalesDto(
                totalIngresosReservado, totalIngresosAplicado);
        PartidaPresupuestariaTotalesDto totalEgresos = new PartidaPresupuestariaTotalesDto(
                totalEgresosReservado, totalEgresosAplicado);
        PartidaPresupuestariaTotalesDto totalGeneral = new PartidaPresupuestariaTotalesDto(
                totalIngresosReservado.subtract(totalEgresosReservado),
                totalIngresosAplicado.subtract(totalEgresosAplicado));

        return new PeriodoFinancieroPartidasResumenDto(
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
