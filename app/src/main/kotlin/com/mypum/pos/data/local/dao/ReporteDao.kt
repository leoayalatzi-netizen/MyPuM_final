package com.mypum.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.mypum.pos.domain.model.ProductoTop

@Dao
interface ReporteDao {
    @Query("""
        SELECT p.id AS productoId,
               p.nombre AS nombre,
               CAST(COALESCE(SUM(d.cantidad), 0) AS REAL) AS unidadesVendidas,
               CAST(COALESCE(SUM(d.subtotal), 0) AS REAL) AS totalVendido
        FROM detalle_venta d
        INNER JOIN productos p ON p.id = d.productoId
        INNER JOIN ventas v ON v.id = d.ventaId
        WHERE v.cancelada = 0
        GROUP BY p.id, p.nombre
        ORDER BY totalVendido DESC
        LIMIT 10
    """)
    suspend fun topProductos(): List<ProductoTop>
}
