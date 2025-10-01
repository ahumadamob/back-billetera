package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.repository.PeriodoFinancieroRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
