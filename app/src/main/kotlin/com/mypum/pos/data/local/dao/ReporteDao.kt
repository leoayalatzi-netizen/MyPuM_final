package com.mypum.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.mypum.pos.domain.model.ProductoTop

@Dao
interface ReporteDao {

    @Query(
        """
        SELECT
            p.id AS productoId,
            p.nombre AS nombre,
            COALESCE(SUM(d.cantidad), 0.0) AS unidadesVendidas,
            COALESCE(SUM(d.subtotal), 0.0) AS totalVendido
        FROM detalle_venta d
        INNER JOIN ventas v ON v.id = d.ventaId
        INNER JOIN productos p ON p.id = d.productoId
        WHERE v.cancelada = 0
        GROUP BY p.id, p.nombre
        ORDER BY totalVendido DESC
        """
    )
    suspend fun topProductos(): List<ProductoTop>
}
