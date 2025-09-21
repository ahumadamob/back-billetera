package com.ahumadamob.fnanz.mapper;

import com.ahumadamob.fnanz.dto.CategoriaFinancieraCreateDto;
import com.ahumadamob.fnanz.dto.CategoriaFinancieraPatchDto;
import com.ahumadamob.fnanz.dto.response.CategoriaFinancieraResponseDto;
import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Especialista en transformar DTOs de CategoriaFinanciera a entidades y viceversa.
 */
@Component
public class CategoriaFinancieraMapper {

    public CategoriaFinanciera toEntity(CategoriaFinancieraCreateDto dto) {
        CategoriaFinanciera categoria = new CategoriaFinanciera();
        categoria.setNombre(dto.getNombre());
        categoria.setTipo(dto.getTipo());
        Optional.ofNullable(dto.getActivo()).ifPresent(categoria::setActivo);
        categoria.setOrden(dto.getOrden());
        categoria.setDescripcion(dto.getDescripcion());
        return categoria;
    }

    public CategoriaFinanciera toPartialEntity(CategoriaFinancieraPatchDto dto) {
        CategoriaFinanciera categoria = new CategoriaFinanciera();
        updateEntity(dto, categoria);
        return categoria;
    }

    public void updateEntity(CategoriaFinancieraPatchDto dto, CategoriaFinanciera categoria) {
        Optional.ofNullable(dto.getNombre()).ifPresent(categoria::setNombre);
        Optional.ofNullable(dto.getTipo()).ifPresent(categoria::setTipo);
        Optional.ofNullable(dto.getActivo()).ifPresent(categoria::setActivo);
        Optional.ofNullable(dto.getOrden()).ifPresent(categoria::setOrden);
        Optional.ofNullable(dto.getDescripcion()).ifPresent(categoria::setDescripcion);
    }

    public CategoriaFinancieraResponseDto toDto(CategoriaFinanciera categoria) {
        return new CategoriaFinancieraResponseDto(
                categoria.getId(),
                categoria.getNombre(),
                categoria.getTipo(),
                Boolean.TRUE.equals(categoria.getActivo()),
                categoria.getOrden(),
                categoria.getDescripcion(),
                categoria.getCreatedAt(),
                categoria.getUpdatedAt()
        );
    }
}
