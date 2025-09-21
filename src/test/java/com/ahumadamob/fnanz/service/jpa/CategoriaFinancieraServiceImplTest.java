package com.ahumadamob.fnanz.service.jpa;

import com.ahumadamob.fnanz.entity.CategoriaFinanciera;
import com.ahumadamob.fnanz.enums.TipoFin;
import com.ahumadamob.fnanz.error.ResourceConflictException;
import com.ahumadamob.fnanz.error.ResourceNotFoundException;
import com.ahumadamob.fnanz.repository.CategoriaFinancieraRepository;
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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaFinancieraServiceImplTest {

    @Mock
    private CategoriaFinancieraRepository categoriaFinancieraRepository;

    @InjectMocks
    private CategoriaFinancieraServiceImpl service;

    @Test
    void createShouldPersistWhenNombreIsUnique() {
        CategoriaFinanciera toCreate = buildCategoria(null, "Salario", TipoFin.INGRESO);
        CategoriaFinanciera saved = buildCategoria(1L, "Salario", TipoFin.INGRESO);

        when(categoriaFinancieraRepository.findByNombre("Salario")).thenReturn(Optional.empty());
        when(categoriaFinancieraRepository.save(toCreate)).thenReturn(saved);

        CategoriaFinanciera result = service.create(toCreate);

        assertThat(result).isSameAs(saved);
    }

    @Test
    void createShouldThrowConflictWhenNombreAlreadyExists() {
        CategoriaFinanciera toCreate = buildCategoria(null, "Salario", TipoFin.INGRESO);
        CategoriaFinanciera existing = buildCategoria(99L, "Salario", TipoFin.INGRESO);

        when(categoriaFinancieraRepository.findByNombre("Salario")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> service.create(toCreate))
                .isInstanceOf(ResourceConflictException.class);
        verify(categoriaFinancieraRepository, never()).save(any());
    }

    @Test
    void getShouldReturnCategoriaWhenExists() {
        CategoriaFinanciera categoria = buildCategoria(4L, "Servicios", TipoFin.EGRESO);
        when(categoriaFinancieraRepository.findById(4L)).thenReturn(Optional.of(categoria));

        CategoriaFinanciera result = service.get(4L);

        assertThat(result).isSameAs(categoria);
    }

    @Test
    void getShouldThrowWhenCategoriaDoesNotExist() {
        when(categoriaFinancieraRepository.findById(4L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.get(4L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateShouldApplyProvidedChanges() {
        CategoriaFinanciera existing = buildCategoria(5L, "Salario", TipoFin.INGRESO);
        existing.setDescripcion("Ingresos principales");
        existing.setOrden(1);

        CategoriaFinanciera cambios = new CategoriaFinanciera();
        cambios.setNombre("Renta");
        cambios.setTipo(TipoFin.EGRESO);
        cambios.setActivo(false);
        cambios.setOrden(3);
        cambios.setDescripcion("Pagos mensuales");

        when(categoriaFinancieraRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(categoriaFinancieraRepository.findByNombre("Renta")).thenReturn(Optional.empty());
        when(categoriaFinancieraRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaFinanciera result = service.update(5L, cambios);

        assertThat(result.getNombre()).isEqualTo("Renta");
        assertThat(result.getTipo()).isEqualTo(TipoFin.EGRESO);
        assertThat(result.getActivo()).isFalse();
        assertThat(result.getOrden()).isEqualTo(3);
        assertThat(result.getDescripcion()).isEqualTo("Pagos mensuales");
    }

    @Test
    void updateShouldThrowConflictWhenNombreBelongsToAnotherCategoria() {
        CategoriaFinanciera existing = buildCategoria(5L, "Salario", TipoFin.INGRESO);
        CategoriaFinanciera cambios = new CategoriaFinanciera();
        cambios.setNombre("Renta");

        CategoriaFinanciera another = buildCategoria(8L, "Renta", TipoFin.EGRESO);

        when(categoriaFinancieraRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(categoriaFinancieraRepository.findByNombre("Renta")).thenReturn(Optional.of(another));

        assertThatThrownBy(() -> service.update(5L, cambios))
                .isInstanceOf(ResourceConflictException.class);
        verify(categoriaFinancieraRepository, never()).save(any());
    }

    @Test
    void updateShouldNotQueryNombreWhenUnchanged() {
        CategoriaFinanciera existing = buildCategoria(5L, "Salario", TipoFin.INGRESO);
        existing.setDescripcion("Ingresos principales");

        CategoriaFinanciera cambios = new CategoriaFinanciera();
        cambios.setNombre("Salario");
        cambios.setDescripcion("Actualizado");

        when(categoriaFinancieraRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(categoriaFinancieraRepository.save(existing)).thenAnswer(invocation -> invocation.getArgument(0));

        CategoriaFinanciera result = service.update(5L, cambios);

        assertThat(result.getDescripcion()).isEqualTo("Actualizado");
        verify(categoriaFinancieraRepository, never()).findByNombre(anyString());
    }

    @Test
    void updateShouldThrowWhenCategoriaDoesNotExist() {
        CategoriaFinanciera cambios = new CategoriaFinanciera();
        cambios.setNombre("Renta");

        when(categoriaFinancieraRepository.findById(5L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(5L, cambios))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteShouldRemoveCategoriaWhenExists() {
        when(categoriaFinancieraRepository.existsById(10L)).thenReturn(true);

        service.delete(10L);

        verify(categoriaFinancieraRepository).deleteById(10L);
    }

    @Test
    void deleteShouldThrowWhenCategoriaDoesNotExist() {
        when(categoriaFinancieraRepository.existsById(10L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(10L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void listShouldUseNullSpecificationWhenQueryIsBlank() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CategoriaFinanciera> expected = new PageImpl<>(List.of());
        when(categoriaFinancieraRepository.findAll((Specification<CategoriaFinanciera>) any(), eq(pageable)))
                .thenReturn(expected);

        Page<CategoriaFinanciera> result = service.list("   ", pageable);

        assertThat(result).isSameAs(expected);
        verify(categoriaFinancieraRepository).findAll(isNull(), eq(pageable));
        verifyNoMoreInteractions(categoriaFinancieraRepository);
    }

    @Test
    void listShouldBuildSpecificationWhenQueryIsPresent() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<CategoriaFinanciera> expected = new PageImpl<>(List.of());
        when(categoriaFinancieraRepository.findAll((Specification<CategoriaFinanciera>) any(), eq(pageable)))
                .thenReturn(expected);

        Page<CategoriaFinanciera> result = service.list("ing", pageable);

        assertThat(result).isSameAs(expected);
        ArgumentCaptor<Specification<CategoriaFinanciera>> captor = ArgumentCaptor.forClass(Specification.class);
        verify(categoriaFinancieraRepository).findAll(captor.capture(), eq(pageable));
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
}
