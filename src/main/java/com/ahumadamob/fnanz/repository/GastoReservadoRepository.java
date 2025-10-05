package com.ahumadamob.fnanz.repository;

import com.ahumadamob.fnanz.entity.GastoReservado;
import com.ahumadamob.fnanz.repository.projection.GastoReservadoCategoriaResumenProjection;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA para la entidad {@link GastoReservado}.
 */
public interface GastoReservadoRepository extends JpaRepository<GastoReservado, Long>,
        JpaSpecificationExecutor<GastoReservado> {

    @Query("""
            select gr.categoria.id as categoriaId,
                   gr.categoria.nombre as categoriaNombre,
                   gr.categoria.tipo as tipo,
                   gr.categoria.orden as categoriaOrden,
                   coalesce(sum(gr.montoReservado), 0) as totalMontoReservado,
                   coalesce(sum(gr.montoAplicado), 0) as totalMontoAplicado
            from GastoReservado gr
            where gr.periodo.id = :periodoId
            group by gr.categoria.id, gr.categoria.nombre, gr.categoria.tipo, gr.categoria.orden
            """)
    List<GastoReservadoCategoriaResumenProjection> sumByPeriodoId(@Param("periodoId") Long periodoId);

    List<GastoReservado> findAllByPeriodoIdOrderByIdAsc(Long periodoId);
}
