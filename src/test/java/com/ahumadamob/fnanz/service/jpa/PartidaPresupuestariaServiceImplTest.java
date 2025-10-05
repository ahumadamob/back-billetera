package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import com.ahumadamob.fnanz.entity.PartidaPresupuestaria;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.enums.EstadoReserva;
import com.ahumadamob.fnanz.enums.TipoFin;
import com.ahumadamob.fnanz.error.ResourceConflictException;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.repository.PartidaPresupuestariaRepository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PartidaPresupuestariaServiceImplTest {

    @Mock
    private PartidaPresupuestariaRepository partidaPresupuestariaRepository;

    @InjectMocks
    private PartidaPresupuestariaServiceImpl service;

    @Test
    void createShouldPersistWhenTipoMatchesCategoria() {
        CategoriaFinanciera categoria = buildCategoria(1L, "Servicios", TipoFin.EGRESO);
        PeriodoFinanciero periodo = buildPeriodo(1L, LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31));
        PartidaPresupuestaria toCreate = buildPartida(null, TipoFin.EGRESO, categoria, periodo);
        toCreate.setEstado(EstadoReserva.RESERVADO);

        PartidaPresupuestaria saved = buildPartida(10L, TipoFin.EGRESO, categoria, periodo);
        saved.setEstado(EstadoReserva.RESERVADO);

        when(partidaPresupuestariaRepository.save(toCreate)).thenReturn(saved);

        PartidaPresupuestaria result = service.create(toCreate);

        assertThat(result).isSameAs(saved);
    }

    @Test
    void createShouldThrowConflictWhenTipoDiffersFromCategoria() {
        CategoriaFinanciera categoria = buildCategoria(1L, "Salario", TipoFin.INGRESO);
        PeriodoFinanciero periodo = buildPeriodo(1L, LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31));
        PartidaPresupuestaria toCreate = buildPartida(null, TipoFin.EGRESO, categoria, periodo);

        assertThatThrownBy(() -> service.create(toCreate))
                .isInstanceOf(ResourceConflictException.class);
        verify(partidaPresupuestariaRepository, never()).save(any());
    }

    @Test
    void getShouldReturnPartidaWhenExists() {
        CategoriaFinanciera categoria = buildCategoria(1L, "Servicios", TipoFin.EGRESO);
        PeriodoFinanciero periodo = buildPeriodo(1L, LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31));
        PartidaPresupuestaria partida = buildPartida(5L, TipoFin.EGRESO, categoria, periodo);
        when(partidaPresupuestariaRepository.findById(5L)).thenReturn(Optional.of(partida));

        PartidaPresupuestaria result = service.get(5L);

        assertThat(result).isSameAs(partida);
    }

    @Test
    void getShouldThrowWhenPartidaDoesNotExist() {
        when(partidaPresupuestariaRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateShouldApplyProvidedChanges() {
        CategoriaFinanciera categoria = buildCategoria(1L, "Servicios", TipoFin.EGRESO);
        CategoriaFinanciera nuevaCategoria = buildCategoria(2L, "Honorarios", TipoFin.EGRESO);
        PeriodoFinanciero periodo = buildPeriodo(1L, LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31));
        PeriodoFinanciero nuevoPeriodo = buildPeriodo(2L, LocalDate.of(2024, 6, 1), LocalDate.of(2024, 6, 30));
        PartidaPresupuestaria existing = buildPartida(8L, TipoFin.EGRESO, categoria, periodo);
        existing.setConcepto("Pago consultoría");
        existing.setNota("Mensual");

        PartidaPresupuestaria cambios = new PartidaPresupuestaria();
        cambios.setTipo(TipoFin.EGRESO);
        cambios.setCategoria(nuevaCategoria);
        cambios.setConcepto("Pago asesoría");
        cambios.setPeriodo(nuevoPeriodo);
        cambios.setEstado(EstadoReserva.APLICADO);
        cambios.setMontoReservado(new BigDecimal("2000.00"));
        cambios.setMontoAplicado(new BigDecimal("1800.00"));
        cambios.setNota("Actualizado");

        when(partidaPresupuestariaRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(partidaPresupuestariaRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        PartidaPresupuestaria result = service.update(8L, cambios);

        assertThat(result.getCategoria()).isSameAs(nuevaCategoria);
        assertThat(result.getConcepto()).isEqualTo("Pago asesoría");
        assertThat(result.getPeriodo()).isSameAs(nuevoPeriodo);
        assertThat(result.getEstado()).isEqualTo(EstadoReserva.APLICADO);
        assertThat(result.getMontoReservado()).isEqualByComparingTo("2000.00");
        assertThat(result.getMontoAplicado()).isEqualByComparingTo("1800.00");
        assertThat(result.getNota()).isEqualTo("Actualizado");
    }

    @Test
    void updateShouldThrowConflictWhenTipoDoesNotMatchCategoria() {
        CategoriaFinanciera categoria = buildCategoria(1L, "Servicios", TipoFin.EGRESO);
        PeriodoFinanciero periodo = buildPeriodo(1L, LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31));
        PartidaPresupuestaria existing = buildPartida(8L, TipoFin.EGRESO, categoria, periodo);

        PartidaPresupuestaria cambios = new PartidaPresupuestaria();
        cambios.setTipo(TipoFin.INGRESO);

        when(partidaPresupuestariaRepository.findById(8L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.update(8L, cambios))
                .isInstanceOf(ResourceConflictException.class);
        verify(partidaPresupuestariaRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowWhenPartidaDoesNotExist() {
        PartidaPresupuestaria cambios = new PartidaPresupuestaria();
        cambios.setTipo(TipoFin.EGRESO);
        cambios.setPeriodo(buildPeriodo(1L, LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31)));

        when(partidaPresupuestariaRepository.findById(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(8L, cambios))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteShouldRemovePartidaWhenExists() {
        when(partidaPresupuestariaRepository.existsById(3L)).thenReturn(true);

        service.delete(3L);

        verify(partidaPresupuestariaRepository).deleteById(3L);
    }

    @Test
    void deleteShouldThrowWhenPartidaDoesNotExist() {
        when(partidaPresupuestariaRepository.existsById(3L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(3L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listShouldCallSimpleFindAllWhenQueryIsBlank() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PartidaPresupuestaria> expected = new PageImpl<>(List.of());
        when(partidaPresupuestariaRepository.findAll(pageable)).thenReturn(expected);

        Page<PartidaPresupuestaria> result = service.list("   ", pageable);

        assertThat(result).isSameAs(expected);
        verify(partidaPresupuestariaRepository).findAll(pageable);
        verifyNoMoreInteractions(partidaPresupuestariaRepository);
    }

    @Test
    void listShouldBuildSpecificationWhenQueryIsPresent() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PartidaPresupuestaria> expected = new PageImpl<>(List.of());
        when(partidaPresupuestariaRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expected);

        Page<PartidaPresupuestaria> result = service.list("consult", pageable);

        assertThat(result).isSameAs(expected);
        ArgumentCaptor<Specification<PartidaPresupuestaria>> captor = ArgumentCaptor.forClass(Specification.class);
        verify(partidaPresupuestariaRepository).findAll(captor.capture(), eq(pageable));
        assertThat(captor.getValue()).isNotNull();
    }

    @Test
    void listByPeriodoShouldDelegateToRepository() {
        CategoriaFinanciera categoria = buildCategoria(1L, "Servicios", TipoFin.EGRESO);
        PeriodoFinanciero periodo = buildPeriodo(3L, LocalDate.of(2024, 7, 1), LocalDate.of(2024, 7, 31));
        PartidaPresupuestaria primero = buildPartida(10L, TipoFin.EGRESO, categoria, periodo);
        PartidaPresupuestaria segundo = buildPartida(12L, TipoFin.EGRESO, categoria, periodo);
        List<PartidaPresupuestaria> expected = List.of(primero, segundo);

        when(partidaPresupuestariaRepository.findAllByPeriodoIdOrderByIdAsc(3L)).thenReturn(expected);

        List<PartidaPresupuestaria> result = service.listByPeriodo(3L);

        assertThat(result).isSameAs(expected);
        verify(partidaPresupuestariaRepository).findAllByPeriodoIdOrderByIdAsc(3L);
    }

    private CategoriaFinanciera buildCategoria(Long id, String nombre, TipoFin tipo) {
        CategoriaFinanciera categoria = new CategoriaFinanciera();
        categoria.setId(id);
        categoria.setNombre(nombre);
        categoria.setTipo(tipo);
        categoria.setActivo(true);
        return categoria;
    }

    private PeriodoFinanciero buildPeriodo(Long id, LocalDate inicio, LocalDate fin) {
        PeriodoFinanciero periodo = new PeriodoFinanciero();
        periodo.setId(id);
        periodo.setNombre("Periodo " + id);
        periodo.setFechaInicio(inicio);
        periodo.setFechaFin(fin);
        periodo.setCerrado(false);
        return periodo;
    }

    private PartidaPresupuestaria buildPartida(Long id, TipoFin tipo, CategoriaFinanciera categoria, PeriodoFinanciero periodo) {
        PartidaPresupuestaria partida = new PartidaPresupuestaria();
        partida.setId(id);
        partida.setTipo(tipo);
        partida.setCategoria(categoria);
        partida.setPeriodo(periodo);
        partida.setMontoReservado(new BigDecimal("1000.00"));
        return partida;
    }
}
