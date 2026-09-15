package com.mypum.pos.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.mypum.pos.domain.model.ProductoTop

@Dao
interface ReporteDao {

    @Query("""
        SELECT
            p.id AS productoId,
            p.nombre AS nombre,
            SUM(CAST(d.cantidad AS REAL)) AS unidadesVendidas,
            SUM(CAST(d.subtotal AS REAL)) AS totalVendido
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
