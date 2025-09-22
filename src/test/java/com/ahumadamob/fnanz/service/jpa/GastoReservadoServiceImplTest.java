package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import com.ahumadamob.fnanz.entity.GastoReservado;
import com.ahumadamob.fnanz.enums.EstadoReserva;
import com.ahumadamob.fnanz.enums.TipoFin;
import com.ahumadamob.fnanz.error.ResourceConflictException;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.repository.GastoReservadoRepository;
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
class GastoReservadoServiceImplTest {

    @Mock
    private GastoReservadoRepository gastoReservadoRepository;

    @InjectMocks
    private GastoReservadoServiceImpl service;

    @Test
    void createShouldPersistWhenTipoMatchesCategoria() {
        CategoriaFinanciera categoria = buildCategoria(1L, "Servicios", TipoFin.EGRESO);
        GastoReservado toCreate = buildGasto(null, TipoFin.EGRESO, categoria);
        toCreate.setEstado(EstadoReserva.RESERVADO);

        GastoReservado saved = buildGasto(10L, TipoFin.EGRESO, categoria);
        saved.setEstado(EstadoReserva.RESERVADO);

        when(gastoReservadoRepository.save(toCreate)).thenReturn(saved);

        GastoReservado result = service.create(toCreate);

        assertThat(result).isSameAs(saved);
    }

    @Test
    void createShouldThrowConflictWhenTipoDiffersFromCategoria() {
        CategoriaFinanciera categoria = buildCategoria(1L, "Salario", TipoFin.INGRESO);
        GastoReservado toCreate = buildGasto(null, TipoFin.EGRESO, categoria);

        assertThatThrownBy(() -> service.create(toCreate))
                .isInstanceOf(ResourceConflictException.class);
        verify(gastoReservadoRepository, never()).save(any());
    }

    @Test
    void getShouldReturnGastoWhenExists() {
        CategoriaFinanciera categoria = buildCategoria(1L, "Servicios", TipoFin.EGRESO);
        GastoReservado gasto = buildGasto(5L, TipoFin.EGRESO, categoria);
        when(gastoReservadoRepository.findById(5L)).thenReturn(Optional.of(gasto));

        GastoReservado result = service.get(5L);

        assertThat(result).isSameAs(gasto);
    }

    @Test
    void getShouldThrowWhenGastoDoesNotExist() {
        when(gastoReservadoRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(5L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateShouldApplyProvidedChanges() {
        CategoriaFinanciera categoria = buildCategoria(1L, "Servicios", TipoFin.EGRESO);
        CategoriaFinanciera nuevaCategoria = buildCategoria(2L, "Honorarios", TipoFin.EGRESO);
        GastoReservado existing = buildGasto(8L, TipoFin.EGRESO, categoria);
        existing.setConcepto("Pago consultoría");
        existing.setNota("Mensual");

        GastoReservado cambios = new GastoReservado();
        cambios.setTipo(TipoFin.EGRESO);
        cambios.setCategoria(nuevaCategoria);
        cambios.setConcepto("Pago asesoría");
        cambios.setPeriodoFecha(LocalDate.of(2024, 6, 1));
        cambios.setFechaVencimiento(LocalDate.of(2024, 6, 10));
        cambios.setEstado(EstadoReserva.APLICADO);
        cambios.setMontoReservado(new BigDecimal("2000.00"));
        cambios.setMontoAplicado(new BigDecimal("1800.00"));
        cambios.setNota("Actualizado");

        when(gastoReservadoRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(gastoReservadoRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        GastoReservado result = service.update(8L, cambios);

        assertThat(result.getCategoria()).isSameAs(nuevaCategoria);
        assertThat(result.getConcepto()).isEqualTo("Pago asesoría");
        assertThat(result.getPeriodoFecha()).isEqualTo(LocalDate.of(2024, 6, 1));
        assertThat(result.getFechaVencimiento()).isEqualTo(LocalDate.of(2024, 6, 10));
        assertThat(result.getEstado()).isEqualTo(EstadoReserva.APLICADO);
        assertThat(result.getMontoReservado()).isEqualByComparingTo("2000.00");
        assertThat(result.getMontoAplicado()).isEqualByComparingTo("1800.00");
        assertThat(result.getNota()).isEqualTo("Actualizado");
    }

    @Test
    void updateShouldThrowConflictWhenTipoDoesNotMatchCategoria() {
        CategoriaFinanciera categoria = buildCategoria(1L, "Servicios", TipoFin.EGRESO);
        GastoReservado existing = buildGasto(8L, TipoFin.EGRESO, categoria);

        GastoReservado cambios = new GastoReservado();
        cambios.setTipo(TipoFin.INGRESO);

        when(gastoReservadoRepository.findById(8L)).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.update(8L, cambios))
                .isInstanceOf(ResourceConflictException.class);
        verify(gastoReservadoRepository, never()).save(any());
    }

    @Test
    void updateShouldThrowWhenGastoDoesNotExist() {
        GastoReservado cambios = new GastoReservado();
        cambios.setTipo(TipoFin.EGRESO);

        when(gastoReservadoRepository.findById(8L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(8L, cambios))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteShouldRemoveGastoWhenExists() {
        when(gastoReservadoRepository.existsById(3L)).thenReturn(true);

        service.delete(3L);

        verify(gastoReservadoRepository).deleteById(3L);
    }

    @Test
    void deleteShouldThrowWhenGastoDoesNotExist() {
        when(gastoReservadoRepository.existsById(3L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(3L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listShouldCallSimpleFindAllWhenQueryIsBlank() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GastoReservado> expected = new PageImpl<>(List.of());
        when(gastoReservadoRepository.findAll(pageable)).thenReturn(expected);

        Page<GastoReservado> result = service.list("   ", pageable);

        assertThat(result).isSameAs(expected);
        verify(gastoReservadoRepository).findAll(pageable);
        verifyNoMoreInteractions(gastoReservadoRepository);
    }

    @Test
    void listShouldBuildSpecificationWhenQueryIsPresent() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<GastoReservado> expected = new PageImpl<>(List.of());
        when(gastoReservadoRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(expected);

        Page<GastoReservado> result = service.list("consult", pageable);

        assertThat(result).isSameAs(expected);
        ArgumentCaptor<Specification<GastoReservado>> captor = ArgumentCaptor.forClass(Specification.class);
        verify(gastoReservadoRepository).findAll(captor.capture(), eq(pageable));
        assertThat(captor.getValue()).isNotNull();
    }

    private CategoriaFinanciera buildCategoria(Long id, String nombre, TipoFin tipo) {
        CategoriaFinanciera categoria = new CategoriaFinanciera();
        categoria.setId(id);
        categoria.setNombre(nombre);
        categoria.setTipo(tipo);
        categoria.setActivo(true);
        return categoria;
    }

    private GastoReservado buildGasto(Long id, TipoFin tipo, CategoriaFinanciera categoria) {
        GastoReservado gasto = new GastoReservado();
        gasto.setId(id);
        gasto.setTipo(tipo);
        gasto.setCategoria(categoria);
        gasto.setPeriodoFecha(LocalDate.of(2024, 5, 1));
        gasto.setMontoReservado(new BigDecimal("1000.00"));
        return gasto;
    }
}
