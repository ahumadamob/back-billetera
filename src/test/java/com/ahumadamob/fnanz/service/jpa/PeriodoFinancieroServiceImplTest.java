package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.dto.response.PartidaPresupuestariaCategoriaResumenDto;
import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroPartidasResumenDto;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.enums.TipoFin;
import com.ahumadamob.fnanz.repository.PartidaPresupuestariaRepository;
import com.ahumadamob.fnanz.repository.PeriodoFinancieroRepository;
import com.ahumadamob.fnanz.repository.projection.PartidaPresupuestariaCategoriaResumenProjection;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeriodoFinancieroServiceImplTest {

    @Mock
    private PeriodoFinancieroRepository periodoFinancieroRepository;

    @Mock
    private PartidaPresupuestariaRepository partidaPresupuestariaRepository;

    @InjectMocks
    private PeriodoFinancieroServiceImpl service;

    @Test
    void createShouldPersistPeriodoWithDefaultCerradoWhenNull() {
        PeriodoFinanciero periodo = buildPeriodo(null, "Mayo 2024",
                LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31));
        periodo.setCerrado(null);

        when(periodoFinancieroRepository.save(periodo)).thenReturn(periodo);

        PeriodoFinanciero result = service.create(periodo);

        assertThat(result).isSameAs(periodo);
        assertThat(periodo.getCerrado()).isFalse();
        verify(periodoFinancieroRepository).save(periodo);
    }

    @Test
    void getShouldReturnPeriodoWhenExists() {
        PeriodoFinanciero periodo = buildPeriodo(5L, "Mayo 2024",
                LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31));
        when(periodoFinancieroRepository.findById(5L)).thenReturn(Optional.of(periodo));

        PeriodoFinanciero result = service.get(5L);

        assertThat(result).isSameAs(periodo);
        verify(periodoFinancieroRepository).findById(5L);
    }

    @Test
    void listShouldDelegateToRepository() {
        Pageable pageable = PageRequest.of(1, 20);
        Page<PeriodoFinanciero> expected = new PageImpl<>(List.of(
                buildPeriodo(10L, "Junio 2024", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30))
        ), pageable, 1);
        when(periodoFinancieroRepository.findAll(pageable)).thenReturn(expected);

        Page<PeriodoFinanciero> result = service.list(pageable);

        assertThat(result).isSameAs(expected);
        verify(periodoFinancieroRepository).findAll(pageable);
    }

    @Test
    void listarParaDropdownShouldReturnAllOrderedWhenSoloAbiertosIsFalse() {
        List<PeriodoFinanciero> periodos = List.of(
                buildPeriodo(1L, "Enero", LocalDate.of(2024, 1, 1), LocalDate.of(2024, 1, 31)),
                buildPeriodo(2L, "Febrero", LocalDate.of(2024, 2, 1), LocalDate.of(2024, 2, 29))
        );
        when(periodoFinancieroRepository.findAllByOrderByFechaInicioAsc()).thenReturn(periodos);

        List<PeriodoFinanciero> result = service.listarParaDropdown(false);

        assertThat(result).isEqualTo(periodos);
        verify(periodoFinancieroRepository).findAllByOrderByFechaInicioAsc();
    }

    @Test
    void listarParaDropdownShouldFilterClosedWhenSoloAbiertosIsTrue() {
        List<PeriodoFinanciero> abiertos = List.of(
                buildPeriodo(3L, "Marzo", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 31))
        );
        when(periodoFinancieroRepository.findAllByCerradoFalseOrderByFechaInicioAsc()).thenReturn(abiertos);

        List<PeriodoFinanciero> result = service.listarParaDropdown(true);

        assertThat(result).isEqualTo(abiertos);
        verify(periodoFinancieroRepository).findAllByCerradoFalseOrderByFechaInicioAsc();
    }

    @Test
    void getShouldThrowWhenPeriodoDoesNotExist() {
        when(periodoFinancieroRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(9L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void replaceShouldOverwriteAllFields() {
        PeriodoFinanciero existente = buildPeriodo(12L, "Abril", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 30));
        existente.setDescripcion("Periodo original");
        existente.setTipo("Mensual");

        PeriodoFinanciero cambios = buildPeriodo(null, "Mayo", LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31));
        cambios.setDescripcion("Periodo reemplazado");
        cambios.setTipo("Mensual");
        cambios.setCerrado(true);

        when(periodoFinancieroRepository.findById(12L)).thenReturn(Optional.of(existente));
        when(periodoFinancieroRepository.save(existente)).thenReturn(existente);

        PeriodoFinanciero result = service.replace(12L, cambios);

        assertThat(result.getNombre()).isEqualTo("Mayo");
        assertThat(result.getFechaInicio()).isEqualTo(LocalDate.of(2024, 5, 1));
        assertThat(result.getFechaFin()).isEqualTo(LocalDate.of(2024, 5, 31));
        assertThat(result.getDescripcion()).isEqualTo("Periodo reemplazado");
        assertThat(result.getTipo()).isEqualTo("Mensual");
        assertThat(result.getCerrado()).isTrue();
        verify(periodoFinancieroRepository).save(existente);
    }

    @Test
    void updateShouldApplyOnlyProvidedFields() {
        PeriodoFinanciero existente = buildPeriodo(20L, "Junio", LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30));
        existente.setDescripcion("Descripcion original");
        existente.setTipo("Mensual");

        PeriodoFinanciero cambios = new PeriodoFinanciero();
        cambios.setNombre("Junio actualizado");
        cambios.setDescripcion("Descripcion nueva");

        when(periodoFinancieroRepository.findById(20L)).thenReturn(Optional.of(existente));
        when(periodoFinancieroRepository.save(existente)).thenReturn(existente);

        PeriodoFinanciero result = service.update(20L, cambios);

        assertThat(result.getNombre()).isEqualTo("Junio actualizado");
        assertThat(result.getDescripcion()).isEqualTo("Descripcion nueva");
        assertThat(result.getTipo()).isEqualTo("Mensual");
        assertThat(result.getFechaInicio()).isEqualTo(LocalDate.of(2024, 6, 1));
        verify(periodoFinancieroRepository).save(existente);
    }

    @Test
    void deleteShouldRemoveExistingPeriodo() {
        when(periodoFinancieroRepository.existsById(30L)).thenReturn(true);

        service.delete(30L);

        verify(periodoFinancieroRepository).existsById(30L);
        verify(periodoFinancieroRepository).deleteById(30L);
    }

    @Test
    void deleteShouldThrowWhenPeriodoDoesNotExist() {
        when(periodoFinancieroRepository.existsById(40L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(40L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(periodoFinancieroRepository).existsById(40L);
        verify(periodoFinancieroRepository, never()).deleteById(40L);
    }

    @Test
    void obtenerResumenPartidasShouldAggregateByCategoria() {
        when(periodoFinancieroRepository.existsById(5L)).thenReturn(true);
        when(partidaPresupuestariaRepository.sumByPeriodoId(5L)).thenReturn(List.of(
                projection(2L, "Consultoría", TipoFin.INGRESO, 20,
                        new BigDecimal("500.00"), new BigDecimal("450.00")),
                projection(1L, "Salario", TipoFin.INGRESO, 10,
                        new BigDecimal("1000.00"), null),
                projection(3L, "Renta", TipoFin.EGRESO, 5,
                        new BigDecimal("600.00"), new BigDecimal("580.00"))
        ));

        PeriodoFinancieroPartidasResumenDto resumen = service.obtenerResumenPartidas(5L);

        assertThat(resumen.getIngresos()).extracting(PartidaPresupuestariaCategoriaResumenDto::getCategoriaNombre)
                .containsExactly("Salario", "Consultoría");
        assertThat(resumen.getEgresos()).extracting(PartidaPresupuestariaCategoriaResumenDto::getCategoriaNombre)
                .containsExactly("Renta");
        assertThat(resumen.getTotalIngresos().getMontoReservado())
                .isEqualByComparingTo(new BigDecimal("1500.00"));
        assertThat(resumen.getTotalIngresos().getMontoAplicado())
                .isEqualByComparingTo(new BigDecimal("450.00"));
        assertThat(resumen.getTotalEgresos().getMontoReservado())
                .isEqualByComparingTo(new BigDecimal("600.00"));
        assertThat(resumen.getTotalGeneral().getMontoReservado())
                .isEqualByComparingTo(new BigDecimal("900.00"));
        assertThat(resumen.getTotalGeneral().getMontoAplicado())
                .isEqualByComparingTo(new BigDecimal("-130.00"));

        verify(periodoFinancieroRepository).existsById(5L);
        verify(partidaPresupuestariaRepository).sumByPeriodoId(5L);
    }

    @Test
    void obtenerResumenPartidasShouldThrowWhenPeriodoDoesNotExist() {
        when(periodoFinancieroRepository.existsById(77L)).thenReturn(false);

        assertThatThrownBy(() -> service.obtenerResumenPartidas(77L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(periodoFinancieroRepository).existsById(77L);
        verify(partidaPresupuestariaRepository, never()).sumByPeriodoId(77L);
    }

    private PeriodoFinanciero buildPeriodo(Long id, String nombre, LocalDate inicio, LocalDate fin) {
        PeriodoFinanciero periodo = new PeriodoFinanciero();
        periodo.setId(id);
        periodo.setNombre(nombre);
        periodo.setFechaInicio(inicio);
        periodo.setFechaFin(fin);
        periodo.setCerrado(false);
        return periodo;
    }

    private PartidaPresupuestariaCategoriaResumenProjection projection(Long categoriaId, String nombre,
            TipoFin tipo, Integer orden, BigDecimal reservado, BigDecimal aplicado) {
        return new PartidaPresupuestariaCategoriaResumenProjection() {
            @Override
            public Long getCategoriaId() {
                return categoriaId;
            }

            @Override
            public String getCategoriaNombre() {
                return nombre;
            }

            @Override
            public TipoFin getTipo() {
                return tipo;
            }

            @Override
            public Integer getCategoriaOrden() {
                return orden;
            }

            @Override
            public BigDecimal getTotalMontoReservado() {
                return reservado;
            }

            @Override
            public BigDecimal getTotalMontoAplicado() {
                return aplicado;
            }
        };
    }
}
