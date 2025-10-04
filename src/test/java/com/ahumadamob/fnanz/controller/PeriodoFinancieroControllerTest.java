package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.PeriodoFinancieroCreateDto;
import com.ahumadamob.fnanz.dto.PeriodoFinancieroPatchDto;
import com.ahumadamob.fnanz.dto.response.GastoReservadoCategoriaResumenDto;
import com.ahumadamob.fnanz.dto.response.GastoReservadoTotalesDto;
import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroDropdownDto;
import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroReservasResumenDto;
import com.ahumadamob.fnanz.dto.response.PeriodoFinancieroResponseDto;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.enums.TipoFin;
import com.ahumadamob.fnanz.mapper.PeriodoFinancieroMapper;
import com.ahumadamob.fnanz.service.PeriodoFinancieroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PeriodoFinancieroController.class)
class PeriodoFinancieroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PeriodoFinancieroService periodoFinancieroService;

    @MockBean
    private PeriodoFinancieroMapper periodoFinancieroMapper;

    @Test
    void createShouldReturnCreatedResponse() throws Exception {
        PeriodoFinancieroCreateDto dto = new PeriodoFinancieroCreateDto();
        dto.setNombre("Mayo 2024");
        dto.setFechaInicio(LocalDate.of(2024, 5, 1));
        dto.setFechaFin(LocalDate.of(2024, 5, 31));
        dto.setTipo("Mensual");
        dto.setDescripcion("Periodo mensual");
        dto.setCerrado(false);

        PeriodoFinanciero entity = buildPeriodo(null, "Mayo 2024");
        entity.setFechaInicio(LocalDate.of(2024, 5, 1));
        entity.setFechaFin(LocalDate.of(2024, 5, 31));
        entity.setTipo("Mensual");
        entity.setDescripcion("Periodo mensual");
        entity.setCerrado(false);

        PeriodoFinanciero saved = buildPeriodo(7L, "Mayo 2024");
        saved.setFechaInicio(LocalDate.of(2024, 5, 1));
        saved.setFechaFin(LocalDate.of(2024, 5, 31));
        saved.setTipo("Mensual");
        saved.setDescripcion("Periodo mensual");
        saved.setCerrado(false);

        PeriodoFinancieroResponseDto responseDto = buildDto(saved);

        when(periodoFinancieroMapper.toEntity(ArgumentMatchers.any(PeriodoFinancieroCreateDto.class)))
                .thenReturn(entity);
        when(periodoFinancieroService.create(entity)).thenReturn(saved);
        when(periodoFinancieroMapper.toDto(saved)).thenReturn(responseDto);

        mockMvc.perform(post("/api/periodos-financieros")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/api/periodos-financieros/7"))
                .andExpect(jsonPath("$.message").value("Periodo financiero creado correctamente"))
                .andExpect(jsonPath("$.data.id").value(7L))
                .andExpect(jsonPath("$.data.nombre").value("Mayo 2024"))
                .andExpect(jsonPath("$.data.fechaInicio").value("2024-05-01"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(periodoFinancieroMapper).toEntity(ArgumentMatchers.any(PeriodoFinancieroCreateDto.class));
        verify(periodoFinancieroService).create(entity);
        verify(periodoFinancieroMapper).toDto(saved);
        verifyNoMoreInteractions(periodoFinancieroService, periodoFinancieroMapper);
    }

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
    void dropdownShouldReturnOrderedListWithDefaultFilter() throws Exception {
        PeriodoFinanciero periodo1 = buildPeriodo(1L, "Abril 2024");
        PeriodoFinanciero periodo2 = buildPeriodo(2L, "Mayo 2024");
        when(periodoFinancieroService.listarParaDropdown(false)).thenReturn(List.of(periodo1, periodo2));
        when(periodoFinancieroMapper.toDropdownDto(periodo1)).thenReturn(
                new PeriodoFinancieroDropdownDto(1L, "Abril 2024"));
        when(periodoFinancieroMapper.toDropdownDto(periodo2)).thenReturn(
                new PeriodoFinancieroDropdownDto(2L, "Mayo 2024"));

        mockMvc.perform(get("/api/periodos-financieros/dropdown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].nombre").value("Abril 2024"))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].nombre").value("Mayo 2024"));

        verify(periodoFinancieroService).listarParaDropdown(false);
        verify(periodoFinancieroMapper).toDropdownDto(periodo1);
        verify(periodoFinancieroMapper).toDropdownDto(periodo2);
        verifyNoMoreInteractions(periodoFinancieroService, periodoFinancieroMapper);
    }

    @Test
    void dropdownShouldAllowFilteringByAbiertos() throws Exception {
        PeriodoFinanciero periodo = buildPeriodo(3L, "Junio 2024");
        when(periodoFinancieroService.listarParaDropdown(true)).thenReturn(List.of(periodo));
        when(periodoFinancieroMapper.toDropdownDto(periodo)).thenReturn(
                new PeriodoFinancieroDropdownDto(3L, "Junio 2024"));

        mockMvc.perform(get("/api/periodos-financieros/dropdown")
                        .param("soloAbiertos", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id").value(3L))
                .andExpect(jsonPath("$.data[0].nombre").value("Junio 2024"));

        verify(periodoFinancieroService).listarParaDropdown(true);
        verify(periodoFinancieroMapper).toDropdownDto(periodo);
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

    @Test
    void getResumenReservasShouldReturnAggregatedData() throws Exception {
        PeriodoFinancieroReservasResumenDto resumen = new PeriodoFinancieroReservasResumenDto(
                List.of(new GastoReservadoCategoriaResumenDto(
                        1L,
                        "Salario",
                        TipoFin.INGRESO,
                        1,
                        new BigDecimal("1200.00"),
                        new BigDecimal("1100.00")
                )),
                new GastoReservadoTotalesDto(new BigDecimal("1200.00"), new BigDecimal("1100.00")),
                List.of(new GastoReservadoCategoriaResumenDto(
                        2L,
                        "Renta",
                        TipoFin.EGRESO,
                        1,
                        new BigDecimal("700.00"),
                        new BigDecimal("650.00")
                )),
                new GastoReservadoTotalesDto(new BigDecimal("700.00"), new BigDecimal("650.00")),
                new GastoReservadoTotalesDto(new BigDecimal("500.00"), new BigDecimal("450.00"))
        );

        when(periodoFinancieroService.obtenerResumenReservas(9L)).thenReturn(resumen);

        mockMvc.perform(get("/api/periodos-financieros/{id}/reservas-resumen", 9L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.ingresos", hasSize(1)))
                .andExpect(jsonPath("$.data.ingresos[0].categoriaNombre").value("Salario"))
                .andExpect(jsonPath("$.data.totalIngresos.montoReservado").value(1200.00))
                .andExpect(jsonPath("$.data.egresos", hasSize(1)))
                .andExpect(jsonPath("$.data.egresos[0].montoAplicado").value(650.00))
                .andExpect(jsonPath("$.data.totalGeneral.montoReservado").value(500.00))
                .andExpect(jsonPath("$.data.totalGeneral.montoAplicado").value(450.00));

        verify(periodoFinancieroService).obtenerResumenReservas(9L);
        verifyNoMoreInteractions(periodoFinancieroService, periodoFinancieroMapper);
    }

    @Test
    void replaceShouldReturnUpdatedPeriodo() throws Exception {
        PeriodoFinancieroCreateDto dto = new PeriodoFinancieroCreateDto();
        dto.setNombre("Junio 2024");
        dto.setFechaInicio(LocalDate.of(2024, 6, 1));
        dto.setFechaFin(LocalDate.of(2024, 6, 30));
        dto.setTipo("Mensual");
        dto.setDescripcion("Periodo junio");
        dto.setCerrado(true);

        PeriodoFinanciero cambios = buildPeriodo(null, "Junio 2024");
        cambios.setFechaInicio(LocalDate.of(2024, 6, 1));
        cambios.setFechaFin(LocalDate.of(2024, 6, 30));
        cambios.setTipo("Mensual");
        cambios.setDescripcion("Periodo junio");
        cambios.setCerrado(true);

        PeriodoFinanciero actualizado = buildPeriodo(15L, "Junio 2024");
        actualizado.setFechaInicio(LocalDate.of(2024, 6, 1));
        actualizado.setFechaFin(LocalDate.of(2024, 6, 30));
        actualizado.setTipo("Mensual");
        actualizado.setDescripcion("Periodo junio");
        actualizado.setCerrado(true);

        PeriodoFinancieroResponseDto dtoResponse = buildDto(actualizado);

        when(periodoFinancieroMapper.toEntity(ArgumentMatchers.any(PeriodoFinancieroCreateDto.class)))
                .thenReturn(cambios);
        when(periodoFinancieroService.replace(15L, cambios)).thenReturn(actualizado);
        when(periodoFinancieroMapper.toDto(actualizado)).thenReturn(dtoResponse);

        mockMvc.perform(put("/api/periodos-financieros/{id}", 15L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(15L))
                .andExpect(jsonPath("$.data.nombre").value("Junio 2024"))
                .andExpect(jsonPath("$.data.cerrado").value(true));

        verify(periodoFinancieroMapper).toEntity(ArgumentMatchers.any(PeriodoFinancieroCreateDto.class));
        verify(periodoFinancieroService).replace(15L, cambios);
        verify(periodoFinancieroMapper).toDto(actualizado);
        verifyNoMoreInteractions(periodoFinancieroService, periodoFinancieroMapper);
    }

    @Test
    void patchShouldReturnUpdatedPeriodo() throws Exception {
        PeriodoFinancieroPatchDto dto = new PeriodoFinancieroPatchDto();
        dto.setNombre("Abril actualizado");
        dto.setCerrado(true);

        PeriodoFinanciero cambios = new PeriodoFinanciero();
        cambios.setNombre("Abril actualizado");
        cambios.setCerrado(true);

        PeriodoFinanciero actualizado = buildPeriodo(9L, "Abril actualizado");
        actualizado.setCerrado(true);

        PeriodoFinancieroResponseDto responseDto = buildDto(actualizado);

        when(periodoFinancieroMapper.toPartialEntity(ArgumentMatchers.any(PeriodoFinancieroPatchDto.class)))
                .thenReturn(cambios);
        when(periodoFinancieroService.update(9L, cambios)).thenReturn(actualizado);
        when(periodoFinancieroMapper.toDto(actualizado)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/periodos-financieros/{id}", 9L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(9L))
                .andExpect(jsonPath("$.data.nombre").value("Abril actualizado"))
                .andExpect(jsonPath("$.data.cerrado").value(true));

        verify(periodoFinancieroMapper).toPartialEntity(ArgumentMatchers.any(PeriodoFinancieroPatchDto.class));
        verify(periodoFinancieroService).update(9L, cambios);
        verify(periodoFinancieroMapper).toDto(actualizado);
        verifyNoMoreInteractions(periodoFinancieroService, periodoFinancieroMapper);
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        doNothing().when(periodoFinancieroService).delete(22L);

        mockMvc.perform(delete("/api/periodos-financieros/{id}", 22L))
                .andExpect(status().isNoContent());

        verify(periodoFinancieroService).delete(22L);
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
