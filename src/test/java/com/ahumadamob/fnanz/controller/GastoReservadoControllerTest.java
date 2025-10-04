package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.GastoReservadoCreateDto;
import com.ahumadamob.fnanz.dto.GastoReservadoPatchDto;
import com.ahumadamob.fnanz.dto.response.GastoReservadoResponseDto;
import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import com.ahumadamob.fnanz.entity.GastoReservado;
import com.ahumadamob.fnanz.enums.EstadoReserva;
import com.ahumadamob.fnanz.enums.TipoFin;
import com.ahumadamob.fnanz.mapper.GastoReservadoMapper;
import com.ahumadamob.fnanz.entity.PeriodoFinanciero;
import com.ahumadamob.fnanz.service.CategoriaFinancieraService;
import com.ahumadamob.fnanz.service.GastoReservadoService;
import com.ahumadamob.fnanz.service.PeriodoFinancieroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
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

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = GastoReservadoController.class)
class GastoReservadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GastoReservadoService gastoReservadoService;

    @MockBean
    private CategoriaFinancieraService categoriaFinancieraService;

    @MockBean
    private GastoReservadoMapper gastoReservadoMapper;

    @MockBean
    private PeriodoFinancieroService periodoFinancieroService;

    @Test
    void createShouldReturnCreatedResponseWithLocationHeader() throws Exception {
        GastoReservadoCreateDto requestDto = new GastoReservadoCreateDto();
        requestDto.setTipo(TipoFin.EGRESO);
        requestDto.setCategoriaId(5L);
        requestDto.setConcepto("Pago consultoría");
        requestDto.setPeriodoId(7L);
        requestDto.setEstado(EstadoReserva.RESERVADO);
        requestDto.setMontoReservado(new BigDecimal("1500.00"));
        requestDto.setMontoAplicado(new BigDecimal("0.00"));
        requestDto.setNota("Mensual");

        CategoriaFinanciera categoria = new CategoriaFinanciera();
        categoria.setId(5L);
        categoria.setNombre("Servicios");
        categoria.setTipo(TipoFin.EGRESO);

        PeriodoFinanciero periodo = new PeriodoFinanciero();
        periodo.setId(7L);
        periodo.setNombre("Mayo 2024");
        periodo.setFechaInicio(LocalDate.of(2024, 5, 1));
        periodo.setFechaFin(LocalDate.of(2024, 5, 31));

        GastoReservado entityToCreate = new GastoReservado();
        entityToCreate.setTipo(TipoFin.EGRESO);
        entityToCreate.setCategoria(categoria);
        entityToCreate.setPeriodo(periodo);
        entityToCreate.setConcepto("Pago consultoría");

        LocalDateTime now = LocalDateTime.of(2024, 5, 2, 12, 0);
        GastoReservado saved = new GastoReservado();
        saved.setId(11L);
        saved.setTipo(TipoFin.EGRESO);
        saved.setCategoria(categoria);
        saved.setPeriodo(periodo);
        saved.setConcepto("Pago consultoría");
        saved.setEstado(EstadoReserva.RESERVADO);
        saved.setMontoReservado(new BigDecimal("1500.00"));
        saved.setMontoAplicado(BigDecimal.ZERO);
        saved.setNota("Mensual");
        saved.setCreatedAt(now);
        saved.setUpdatedAt(now);

        GastoReservadoResponseDto responseDto = new GastoReservadoResponseDto(
                11L,
                TipoFin.EGRESO,
                5L,
                "Servicios",
                "Pago consultoría",
                7L,
                "Mayo 2024",
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 31),
                EstadoReserva.RESERVADO,
                new BigDecimal("1500.00"),
                BigDecimal.ZERO,
                "Mensual",
                now,
                now
        );

        when(categoriaFinancieraService.get(5L)).thenReturn(categoria);
        when(periodoFinancieroService.get(7L)).thenReturn(periodo);
        when(gastoReservadoMapper.toEntity(ArgumentMatchers.any(GastoReservadoCreateDto.class), eq(categoria), eq(periodo)))
                .thenReturn(entityToCreate);
        when(gastoReservadoService.create(entityToCreate)).thenReturn(saved);
        when(gastoReservadoMapper.toDto(saved)).thenReturn(responseDto);

        mockMvc.perform(post("/api/gastos-reservados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/api/gastos-reservados/11"))
                .andExpect(jsonPath("$.message").value("Gasto reservado creado correctamente"))
                .andExpect(jsonPath("$.data.id").value(11L))
                .andExpect(jsonPath("$.data.tipo").value(TipoFin.EGRESO.name()))
                .andExpect(jsonPath("$.data.categoriaId").value(5L))
                .andExpect(jsonPath("$.data.concepto").value("Pago consultoría"))
                .andExpect(jsonPath("$.data.periodoId").value(7L))
                .andExpect(jsonPath("$.data.periodoNombre").value("Mayo 2024"))
                .andExpect(jsonPath("$.data.periodoFechaInicio").value("2024-05-01"))
                .andExpect(jsonPath("$.data.periodoFechaFin").value("2024-05-31"))
                .andExpect(jsonPath("$.data.estado").value(EstadoReserva.RESERVADO.name()))
                .andExpect(jsonPath("$.data.montoReservado").value(1500.00))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(categoriaFinancieraService).get(5L);
        verify(periodoFinancieroService).get(7L);
        verify(gastoReservadoMapper).toEntity(ArgumentMatchers.any(GastoReservadoCreateDto.class), eq(categoria), eq(periodo));
        verify(gastoReservadoService).create(entityToCreate);
        verify(gastoReservadoMapper).toDto(saved);
        verifyNoMoreInteractions(gastoReservadoService, categoriaFinancieraService, gastoReservadoMapper, periodoFinancieroService);
    }

    @Test
    void listShouldReturnPagedResponseWithHeaders() throws Exception {
        CategoriaFinanciera servicios = new CategoriaFinanciera();
        servicios.setId(5L);
        servicios.setNombre("Servicios");
        servicios.setTipo(TipoFin.EGRESO);

        CategoriaFinanciera salarios = new CategoriaFinanciera();
        salarios.setId(6L);
        salarios.setNombre("Salarios");
        salarios.setTipo(TipoFin.INGRESO);

        GastoReservado egreso = new GastoReservado();
        egreso.setId(20L);
        egreso.setCategoria(servicios);
        egreso.setTipo(TipoFin.EGRESO);

        GastoReservado ingreso = new GastoReservado();
        ingreso.setId(21L);
        ingreso.setCategoria(salarios);
        ingreso.setTipo(TipoFin.INGRESO);

        GastoReservadoResponseDto egresoDto = new GastoReservadoResponseDto(
                20L,
                TipoFin.EGRESO,
                5L,
                "Servicios",
                "Pago consultoría",
                3L,
                "Mayo 2024",
                LocalDate.of(2024, 5, 1),
                LocalDate.of(2024, 5, 31),
                EstadoReserva.RESERVADO,
                new BigDecimal("1500.00"),
                BigDecimal.ZERO,
                "Mensual",
                null,
                null
        );

        GastoReservadoResponseDto ingresoDto = new GastoReservadoResponseDto(
                21L,
                TipoFin.INGRESO,
                6L,
                "Salarios",
                "Ingreso mensual",
                4L,
                "2024",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31),
                EstadoReserva.APLICADO,
                new BigDecimal("5000.00"),
                new BigDecimal("5000.00"),
                null,
                null,
                null
        );

        Page<GastoReservado> page = new PageImpl<>(List.of(egreso, ingreso), PageRequest.of(0, 2), 4);

        when(gastoReservadoService.list(ArgumentMatchers.isNull(), any(Pageable.class))).thenReturn(page);
        when(gastoReservadoMapper.toDto(egreso)).thenReturn(egresoDto);
        when(gastoReservadoMapper.toDto(ingreso)).thenReturn(ingresoDto);

        mockMvc.perform(get("/api/gastos-reservados")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "4"))
                .andExpect(header().string(HttpHeaders.LINK, containsString("rel=\"next\"")))
                .andExpect(header().string(HttpHeaders.LINK, containsString("rel=\"last\"")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(20L))
                .andExpect(jsonPath("$.data[0].categoriaNombre").value("Servicios"))
                .andExpect(jsonPath("$.data[1].id").value(21L))
                .andExpect(jsonPath("$.data[1].tipo").value(TipoFin.INGRESO.name()))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(gastoReservadoService).list(ArgumentMatchers.isNull(), any(Pageable.class));
        verify(gastoReservadoMapper).toDto(egreso);
        verify(gastoReservadoMapper).toDto(ingreso);
        verifyNoMoreInteractions(gastoReservadoService, gastoReservadoMapper, categoriaFinancieraService, periodoFinancieroService);
    }

    @Test
    void listByPeriodoShouldReturnFilteredResults() throws Exception {
        CategoriaFinanciera categoria = new CategoriaFinanciera();
        categoria.setId(8L);
        categoria.setNombre("Marketing");
        categoria.setTipo(TipoFin.EGRESO);

        PeriodoFinanciero periodo = new PeriodoFinanciero();
        periodo.setId(7L);
        periodo.setNombre("Julio 2024");
        periodo.setFechaInicio(LocalDate.of(2024, 7, 1));
        periodo.setFechaFin(LocalDate.of(2024, 7, 31));

        GastoReservado primero = new GastoReservado();
        primero.setId(30L);
        primero.setCategoria(categoria);
        primero.setPeriodo(periodo);

        GastoReservado segundo = new GastoReservado();
        segundo.setId(31L);
        segundo.setCategoria(categoria);
        segundo.setPeriodo(periodo);

        GastoReservadoResponseDto primeroDto = new GastoReservadoResponseDto(
                30L,
                TipoFin.EGRESO,
                8L,
                "Marketing",
                "Campaña",
                7L,
                "Julio 2024",
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 7, 31),
                EstadoReserva.RESERVADO,
                new BigDecimal("1200.00"),
                BigDecimal.ZERO,
                null,
                null,
                null
        );

        GastoReservadoResponseDto segundoDto = new GastoReservadoResponseDto(
                31L,
                TipoFin.EGRESO,
                8L,
                "Marketing",
                "Eventos",
                7L,
                "Julio 2024",
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 7, 31),
                EstadoReserva.APLICADO,
                new BigDecimal("900.00"),
                new BigDecimal("450.00"),
                null,
                null,
                null
        );

        when(gastoReservadoService.listByPeriodo(7L)).thenReturn(List.of(primero, segundo));
        when(gastoReservadoMapper.toDto(primero)).thenReturn(primeroDto);
        when(gastoReservadoMapper.toDto(segundo)).thenReturn(segundoDto);

        mockMvc.perform(get("/api/gastos-reservados/periodo/{periodoId}", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(30L))
                .andExpect(jsonPath("$.data[0].periodoId").value(7L))
                .andExpect(jsonPath("$.data[1].id").value(31L))
                .andExpect(jsonPath("$.data[1].concepto").value("Eventos"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(gastoReservadoService).listByPeriodo(7L);
        verify(gastoReservadoMapper).toDto(primero);
        verify(gastoReservadoMapper).toDto(segundo);
        verifyNoMoreInteractions(gastoReservadoService, gastoReservadoMapper, categoriaFinancieraService, periodoFinancieroService);
    }

    @Test
    void getShouldReturnExistingGasto() throws Exception {
        CategoriaFinanciera categoria = new CategoriaFinanciera();
        categoria.setId(7L);
        categoria.setNombre("Honorarios");
        categoria.setTipo(TipoFin.EGRESO);

        GastoReservado gasto = new GastoReservado();
        gasto.setId(30L);
        gasto.setCategoria(categoria);
        gasto.setTipo(TipoFin.EGRESO);

        PeriodoFinanciero periodo = new PeriodoFinanciero();
        periodo.setId(8L);
        periodo.setNombre("Junio 2024");
        periodo.setFechaInicio(LocalDate.of(2024, 6, 1));
        periodo.setFechaFin(LocalDate.of(2024, 6, 30));

        GastoReservadoResponseDto dto = new GastoReservadoResponseDto(
                30L,
                TipoFin.EGRESO,
                7L,
                "Honorarios",
                "Pago asesoría",
                8L,
                "Junio 2024",
                LocalDate.of(2024, 6, 1),
                LocalDate.of(2024, 6, 30),
                EstadoReserva.RESERVADO,
                new BigDecimal("1200.00"),
                null,
                null,
                null,
                null
        );

        when(gastoReservadoService.get(30L)).thenReturn(gasto);
        when(gastoReservadoMapper.toDto(gasto)).thenReturn(dto);

        mockMvc.perform(get("/api/gastos-reservados/{id}", 30L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(30L))
                .andExpect(jsonPath("$.data.categoriaNombre").value("Honorarios"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(gastoReservadoService).get(30L);
        verify(gastoReservadoMapper).toDto(gasto);
        verifyNoMoreInteractions(gastoReservadoService, gastoReservadoMapper, categoriaFinancieraService, periodoFinancieroService);
    }

    @Test
    void updateShouldReturnUpdatedGasto() throws Exception {
        GastoReservadoPatchDto patchDto = new GastoReservadoPatchDto();
        patchDto.setCategoriaId(9L);
        patchDto.setConcepto("Actualizado");
        patchDto.setPeriodoId(12L);
        patchDto.setMontoReservado(new BigDecimal("1800.00"));

        CategoriaFinanciera categoria = new CategoriaFinanciera();
        categoria.setId(9L);
        categoria.setNombre("Servicios");
        categoria.setTipo(TipoFin.EGRESO);

        PeriodoFinanciero periodo = new PeriodoFinanciero();
        periodo.setId(12L);
        periodo.setNombre("Julio 2024");
        periodo.setFechaInicio(LocalDate.of(2024, 7, 1));
        periodo.setFechaFin(LocalDate.of(2024, 7, 31));

        GastoReservado cambios = new GastoReservado();
        cambios.setCategoria(categoria);
        cambios.setConcepto("Actualizado");
        cambios.setPeriodo(periodo);

        GastoReservado updated = new GastoReservado();
        updated.setId(40L);
        updated.setCategoria(categoria);
        updated.setTipo(TipoFin.EGRESO);
        updated.setConcepto("Actualizado");
        updated.setPeriodo(periodo);
        updated.setMontoReservado(new BigDecimal("1800.00"));

        GastoReservadoResponseDto responseDto = new GastoReservadoResponseDto(
                40L,
                TipoFin.EGRESO,
                9L,
                "Servicios",
                "Actualizado",
                12L,
                "Julio 2024",
                LocalDate.of(2024, 7, 1),
                LocalDate.of(2024, 7, 31),
                EstadoReserva.RESERVADO,
                new BigDecimal("1800.00"),
                null,
                null,
                null,
                null
        );

        when(categoriaFinancieraService.get(9L)).thenReturn(categoria);
        when(periodoFinancieroService.get(12L)).thenReturn(periodo);
        when(gastoReservadoMapper.toPartialEntity(ArgumentMatchers.any(GastoReservadoPatchDto.class), eq(categoria), eq(periodo)))
                .thenReturn(cambios);
        when(gastoReservadoService.update(40L, cambios)).thenReturn(updated);
        when(gastoReservadoMapper.toDto(updated)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/gastos-reservados/{id}", 40L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(40L))
                .andExpect(jsonPath("$.data.categoriaId").value(9L))
                .andExpect(jsonPath("$.data.periodoId").value(12L))
                .andExpect(jsonPath("$.data.montoReservado").value(1800.00))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(categoriaFinancieraService).get(9L);
        verify(periodoFinancieroService).get(12L);
        verify(gastoReservadoMapper).toPartialEntity(ArgumentMatchers.any(GastoReservadoPatchDto.class), eq(categoria), eq(periodo));
        verify(gastoReservadoService).update(40L, cambios);
        verify(gastoReservadoMapper).toDto(updated);
        verifyNoMoreInteractions(gastoReservadoService, categoriaFinancieraService, gastoReservadoMapper, periodoFinancieroService);
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        doNothing().when(gastoReservadoService).delete(55L);

        mockMvc.perform(delete("/api/gastos-reservados/{id}", 55L))
                .andExpect(status().isNoContent());

        verify(gastoReservadoService).delete(55L);
        verifyNoMoreInteractions(gastoReservadoService);
        verifyNoMoreInteractions(categoriaFinancieraService, gastoReservadoMapper, periodoFinancieroService);
}
}
