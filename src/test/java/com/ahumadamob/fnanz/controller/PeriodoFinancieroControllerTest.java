package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroResponseDto;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.mapper.PeriodoFinancieroMapper;
import com.ahumadamob.fnanz.service.PeriodoFinancieroService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PeriodoFinancieroController.class)
class PeriodoFinancieroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PeriodoFinancieroService periodoFinancieroService;

    @MockBean
    private PeriodoFinancieroMapper periodoFinancieroMapper;

    @Test
    void getShouldReturnPeriodo() throws Exception {
        PeriodoFinanciero periodo = new PeriodoFinanciero();
        periodo.setId(4L);
        periodo.setNombre("Mayo 2024");
        periodo.setFechaInicio(LocalDate.of(2024, 5, 1));
        periodo.setFechaFin(LocalDate.of(2024, 5, 31));
        periodo.setTipo("Mensual");
        periodo.setDescripcion("Periodo mensual de mayo");
        periodo.setCerrado(false);
        LocalDateTime created = LocalDateTime.of(2024, 4, 15, 10, 30);
        LocalDateTime updated = LocalDateTime.of(2024, 4, 20, 8, 0);
        periodo.setCreatedAt(created);
        periodo.setUpdatedAt(updated);

        PeriodoFinancieroResponseDto dto = new PeriodoFinancieroResponseDto(
                4L,
                "Mayo 2024",
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 31),
                "Mensual",
                "Periodo mensual de mayo",
                false,
                created,
                updated
        );

        when(periodoFinancieroService.get(4L)).thenReturn(periodo);
        when(periodoFinancieroMapper.toDto(periodo)).thenReturn(dto);

        mockMvc.perform(get("/api/periodos-financieros/{id}", 4L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(4L))
                .andExpect(jsonPath("$.data.nombre").value("Mayo 2024"))
                .andExpect(jsonPath("$.data.fechaInicio").value("2024-05-01"))
                .andExpect(jsonPath("$.data.fechaFin").value("2024-05-31"))
                .andExpect(jsonPath("$.data.tipo").value("Mensual"))
                .andExpect(jsonPath("$.data.descripcion").value("Periodo mensual de mayo"))
                .andExpect(jsonPath("$.data.cerrado").value(false))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(periodoFinancieroService).get(4L);
        verify(periodoFinancieroMapper).toDto(periodo);
        verifyNoMoreInteractions(periodoFinancieroService, periodoFinancieroMapper);
    }
}
