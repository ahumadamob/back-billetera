package com.ahumadamob.fnanz.controller;

import com.ahumadamob.fnanz.dto.CategoriaFinancieraCreateDto;
import com.ahumadamob.fnanz.dto.CategoriaFinancieraPatchDto;
import com.ahumadamob.fnanz.dto.response.CategoriaFinancieraResponseDto;
import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import com.ahumadamob.fnanz.enums.TipoFin;
import com.ahumadamob.fnanz.mapper.CategoriaFinancieraMapper;
import com.ahumadamob.fnanz.service.CategoriaFinancieraService;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
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

@WebMvcTest(controllers = CategoriaFinancieraController.class)
class CategoriaFinancieraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaFinancieraService categoriaFinancieraService;

    @MockBean
    private CategoriaFinancieraMapper categoriaFinancieraMapper;

    @Test
    void createShouldReturnCreatedResponseWithLocationHeader() throws Exception {
        CategoriaFinancieraCreateDto requestDto = new CategoriaFinancieraCreateDto();
        requestDto.setNombre("Salario");
        requestDto.setTipo(TipoFin.INGRESO);
        requestDto.setActivo(true);
        requestDto.setOrden(1);
        requestDto.setDescripcion("Ingreso mensual");

        CategoriaFinanciera entityToCreate = new CategoriaFinanciera();
        entityToCreate.setNombre("Salario");
        entityToCreate.setTipo(TipoFin.INGRESO);
        entityToCreate.setActivo(true);
        entityToCreate.setOrden(1);
        entityToCreate.setDescripcion("Ingreso mensual");

        LocalDateTime now = LocalDateTime.of(2024, 5, 1, 10, 0);
        CategoriaFinanciera savedEntity = new CategoriaFinanciera();
        savedEntity.setId(42L);
        savedEntity.setNombre("Salario");
        savedEntity.setTipo(TipoFin.INGRESO);
        savedEntity.setActivo(true);
        savedEntity.setOrden(1);
        savedEntity.setDescripcion("Ingreso mensual");
        savedEntity.setCreatedAt(now);
        savedEntity.setUpdatedAt(now);

        CategoriaFinancieraResponseDto responseDto = new CategoriaFinancieraResponseDto(
                42L,
                "Salario",
                TipoFin.INGRESO,
                true,
                1,
                "Ingreso mensual",
                now,
                now
        );

        when(categoriaFinancieraMapper.toEntity(ArgumentMatchers.any(CategoriaFinancieraCreateDto.class)))
                .thenReturn(entityToCreate);
        when(categoriaFinancieraService.create(entityToCreate)).thenReturn(savedEntity);
        when(categoriaFinancieraMapper.toDto(savedEntity)).thenReturn(responseDto);

        mockMvc.perform(post("/api/categorias-financieras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, "http://localhost/api/categorias-financieras/42"))
                .andExpect(jsonPath("$.message").value("Categoría financiera creada correctamente"))
                .andExpect(jsonPath("$.data.id").value(42L))
                .andExpect(jsonPath("$.data.nombre").value("Salario"))
                .andExpect(jsonPath("$.data.tipo").value(TipoFin.INGRESO.name()))
                .andExpect(jsonPath("$.data.activo").value(true))
                .andExpect(jsonPath("$.data.orden").value(1))
                .andExpect(jsonPath("$.data.descripcion").value("Ingreso mensual"))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(categoriaFinancieraMapper).toEntity(ArgumentMatchers.any(CategoriaFinancieraCreateDto.class));
        verify(categoriaFinancieraService).create(entityToCreate);
        verify(categoriaFinancieraMapper).toDto(savedEntity);
        verifyNoMoreInteractions(categoriaFinancieraService, categoriaFinancieraMapper);
    }

    @Test
    void listShouldReturnPagedResponseWithHeaders() throws Exception {
        CategoriaFinanciera ingreso = new CategoriaFinanciera();
        ingreso.setId(1L);
        ingreso.setNombre("Salario");
        ingreso.setTipo(TipoFin.INGRESO);

        CategoriaFinanciera egreso = new CategoriaFinanciera();
        egreso.setId(2L);
        egreso.setNombre("Alquiler");
        egreso.setTipo(TipoFin.EGRESO);

        CategoriaFinancieraResponseDto ingresoDto = new CategoriaFinancieraResponseDto(
                1L,
                "Salario",
                TipoFin.INGRESO,
                true,
                1,
                "Ingreso mensual",
                null,
                null
        );
        CategoriaFinancieraResponseDto egresoDto = new CategoriaFinancieraResponseDto(
                2L,
                "Alquiler",
                TipoFin.EGRESO,
                true,
                2,
                "Gasto fijo",
                null,
                null
        );

        Page<CategoriaFinanciera> page = new PageImpl<>(List.of(ingreso, egreso), PageRequest.of(0, 2), 4);

        when(categoriaFinancieraService.list(ArgumentMatchers.isNull(), ArgumentMatchers.any(Pageable.class)))
                .thenReturn(page);
        when(categoriaFinancieraMapper.toDto(ingreso)).thenReturn(ingresoDto);
        when(categoriaFinancieraMapper.toDto(egreso)).thenReturn(egresoDto);

        mockMvc.perform(get("/api/categorias-financieras")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "4"))
                .andExpect(header().string(HttpHeaders.LINK, containsString("rel=\"next\"")))
                .andExpect(header().string(HttpHeaders.LINK, containsString("rel=\"last\"")))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[0].nombre").value("Salario"))
                .andExpect(jsonPath("$.data[0].tipo").value(TipoFin.INGRESO.name()))
                .andExpect(jsonPath("$.data[1].id").value(2L))
                .andExpect(jsonPath("$.data[1].tipo").value(TipoFin.EGRESO.name()))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(categoriaFinancieraService).list(ArgumentMatchers.isNull(), ArgumentMatchers.any(Pageable.class));
        verify(categoriaFinancieraMapper).toDto(ingreso);
        verify(categoriaFinancieraMapper).toDto(egreso);
        verifyNoMoreInteractions(categoriaFinancieraService, categoriaFinancieraMapper);
    }

    @Test
    void getShouldReturnExistingCategoria() throws Exception {
        CategoriaFinanciera categoria = new CategoriaFinanciera();
        categoria.setId(10L);
        categoria.setNombre("Consultoría");
        categoria.setTipo(TipoFin.INGRESO);

        CategoriaFinancieraResponseDto dto = new CategoriaFinancieraResponseDto(
                10L,
                "Consultoría",
                TipoFin.INGRESO,
                true,
                3,
                "Servicios profesionales",
                null,
                null
        );

        when(categoriaFinancieraService.get(10L)).thenReturn(categoria);
        when(categoriaFinancieraMapper.toDto(categoria)).thenReturn(dto);

        mockMvc.perform(get("/api/categorias-financieras/{id}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(10L))
                .andExpect(jsonPath("$.data.nombre").value("Consultoría"))
                .andExpect(jsonPath("$.data.tipo").value(TipoFin.INGRESO.name()))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(categoriaFinancieraService).get(10L);
        verify(categoriaFinancieraMapper).toDto(categoria);
        verifyNoMoreInteractions(categoriaFinancieraService, categoriaFinancieraMapper);
    }

    @Test
    void updateShouldReturnUpdatedCategoria() throws Exception {
        CategoriaFinancieraPatchDto patchDto = new CategoriaFinancieraPatchDto();
        patchDto.setNombre("Dividendos");
        patchDto.setActivo(false);

        CategoriaFinanciera cambios = new CategoriaFinanciera();
        cambios.setNombre("Dividendos");
        cambios.setActivo(false);

        CategoriaFinanciera updated = new CategoriaFinanciera();
        updated.setId(7L);
        updated.setNombre("Dividendos");
        updated.setTipo(TipoFin.INGRESO);
        updated.setActivo(false);

        CategoriaFinancieraResponseDto responseDto = new CategoriaFinancieraResponseDto(
                7L,
                "Dividendos",
                TipoFin.INGRESO,
                false,
                null,
                null,
                null,
                null
        );

        when(categoriaFinancieraMapper.toPartialEntity(ArgumentMatchers.any(CategoriaFinancieraPatchDto.class)))
                .thenReturn(cambios);
        when(categoriaFinancieraService.update(7L, cambios)).thenReturn(updated);
        when(categoriaFinancieraMapper.toDto(updated)).thenReturn(responseDto);

        mockMvc.perform(patch("/api/categorias-financieras/{id}", 7L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(7L))
                .andExpect(jsonPath("$.data.nombre").value("Dividendos"))
                .andExpect(jsonPath("$.data.activo").value(false))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(categoriaFinancieraMapper).toPartialEntity(ArgumentMatchers.any(CategoriaFinancieraPatchDto.class));
        verify(categoriaFinancieraService).update(7L, cambios);
        verify(categoriaFinancieraMapper).toDto(updated);
        verifyNoMoreInteractions(categoriaFinancieraService, categoriaFinancieraMapper);
    }

    @Test
    void deleteShouldReturnNoContent() throws Exception {
        doNothing().when(categoriaFinancieraService).delete(99L);

        mockMvc.perform(delete("/api/categorias-financieras/{id}", 99L))
                .andExpect(status().isNoContent());

        verify(categoriaFinancieraService).delete(99L);
        verifyNoMoreInteractions(categoriaFinancieraService);
    }
}
