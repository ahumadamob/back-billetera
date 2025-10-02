package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroResponseDto;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.mapper.PeriodoFinancieroMapper;
import com.ahumadamob.fnanz.service.PeriodoFinancieroService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import org.mockito.ArgumentCaptor;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
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
    void listShouldReturnPage() throws Exception {
        PeriodoFinanciero periodo1 = buildPeriodo(3L, "Abril 2024");
        PeriodoFinanciero periodo2 = buildPeriodo(4L, "Mayo 2024");
        Page<PeriodoFinanciero> page = new PageImpl<>(List.of(periodo1, periodo2), PageRequest.of(0, 10), 2);

        PeriodoFinancieroResponseDto dto1 = buildDto(periodo1);
        PeriodoFinancieroResponseDto dto2 = buildDto(periodo2);

        when(periodoFinancieroService.list(PageRequest.of(0, 10))).thenReturn(page);
        when(periodoFinancieroMapper.toDto(periodo1)).thenReturn(dto1);
        when(periodoFinancieroMapper.toDto(periodo2)).thenReturn(dto2);

        mockMvc.perform(get("/api/periodos-financieros")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(3L))
                .andExpect(jsonPath("$.data[0].nombre").value("Abril 2024"))
                .andExpect(jsonPath("$.data[1].id").value(4L))
                .andExpect(jsonPath("$.data[1].nombre").value("Mayo 2024"));

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(periodoFinancieroService).list(captor.capture());
        Assertions.assertThat(captor.getValue()).isEqualTo(PageRequest.of(0, 10));
        verify(periodoFinancieroMapper).toDto(periodo1);
        verify(periodoFinancieroMapper).toDto(periodo2);
        verifyNoMoreInteractions(periodoFinancieroService, periodoFinancieroMapper);
    }

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

    private PeriodoFinanciero buildPeriodo(Long id, String nombre) {
        PeriodoFinanciero periodo = new PeriodoFinanciero();
        periodo.setId(id);
        periodo.setNombre(nombre);
        periodo.setFechaInicio(LocalDate.of(2024, 4, 1));
        periodo.setFechaFin(LocalDate.of(2024, 4, 30));
        periodo.setTipo("Mensual");
        periodo.setDescripcion("Periodo mensual");
        periodo.setCerrado(false);
        periodo.setCreatedAt(LocalDateTime.of(2024, 3, 1, 0, 0));
        periodo.setUpdatedAt(LocalDateTime.of(2024, 3, 2, 0, 0));
        return periodo;
    }

    private PeriodoFinancieroResponseDto buildDto(PeriodoFinanciero periodo) {
        return new PeriodoFinancieroResponseDto(
                periodo.getId(),
                periodo.getNombre(),
                periodo.getFechaInicio(),
                periodo.getFechaFin(),
                periodo.getTipo(),
                periodo.getDescripcion(),
                periodo.getCerrado(),
                periodo.getCreatedAt(),
                periodo.getUpdatedAt()
        );
    }
}
