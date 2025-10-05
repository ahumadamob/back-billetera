package com.ahumadamob.fnanz.repository;

import com.ahumadamob.fnanz.entity.PartidaPresupuestaria;
import com.ahumadamob.fnanz.repository.projection.PartidaPresupuestariaCategoriaResumenProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA para la entidad {@link PartidaPresupuestaria}.
 */
public interface PartidaPresupuestariaRepository extends JpaRepository<PartidaPresupuestaria, Long>,
        JpaSpecificationExecutor<PartidaPresupuestaria> {

    @Query("""
            select gr.categoria.id as categoriaId,
                   gr.categoria.nombre as categoriaNombre,
                   gr.categoria.tipo as tipo,
                   gr.categoria.orden as categoriaOrden,
                   coalesce(sum(gr.montoReservado), 0) as totalMontoReservado,
                   coalesce(sum(gr.montoAplicado), 0) as totalMontoAplicado
            from PartidaPresupuestaria gr
            where gr.periodo.id = :periodoId
            group by gr.categoria.id, gr.categoria.nombre, gr.categoria.tipo, gr.categoria.orden
            """)
    List<PartidaPresupuestariaCategoriaResumenProjection> sumByPeriodoId(@Param("periodoId") Long periodoId);

    List<PartidaPresupuestaria> findAllByPeriodoIdOrderByIdAsc(Long periodoId);
}
