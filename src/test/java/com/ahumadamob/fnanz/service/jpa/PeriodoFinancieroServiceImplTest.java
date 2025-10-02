package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.repository.PeriodoFinancieroRepository;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PeriodoFinancieroServiceImplTest {

    @Mock
    private PeriodoFinancieroRepository periodoFinancieroRepository;

    @InjectMocks
    private PeriodoFinancieroServiceImpl service;

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
    void getShouldThrowWhenPeriodoDoesNotExist() {
        when(periodoFinancieroRepository.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(9L))
                .isInstanceOf(ResourceNotFoundException.class);
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
}
