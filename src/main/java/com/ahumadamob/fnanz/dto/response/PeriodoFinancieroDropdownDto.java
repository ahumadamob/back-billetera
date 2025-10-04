package com.ahumadamob.fnanz.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO con los datos mínimos de un {@link com.ahumadamob.fnanz.entity.PeriodoFinanciero}
 * para ser utilizado en componentes de selección.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeriodoFinancieroDropdownDto {

    private Long id;

    private String nombre;
}
