package com.mypum.pos.data.local

import com.mypum.pos.data.local.dao.ProductoDao
import com.mypum.pos.data.local.dao.UsuarioDao
import com.mypum.pos.data.local.entity.ProductoEntity
import com.mypum.pos.data.local.entity.UsuarioEntity
import com.mypum.pos.domain.model.enums.RolUsuario
import com.mypum.pos.domain.model.enums.UnidadMedida
import com.mypum.pos.domain.util.PinHasher
import java.math.BigDecimal
import java.time.Instant
import javax.inject.Inject

class DatabaseSeeder @Inject constructor(
    private val dao: ProductoDao,
    private val usuarioDao: UsuarioDao
) {
    suspend fun seedIfNeeded() {
        if (usuarioDao.find("admin") == null) {
            usuarioDao.insert(
                UsuarioEntity(
                    nombre = "admin",
                    pinHash = PinHasher.hash("1234"),
                    rol = RolUsuario.ADMIN
                )
            )
        }

        if (dao.byCodigo("750000000001") == null) {
            val now = Instant.now()

            dao.insert(
                ProductoEntity(
                    nombre = "Producto demo",
                    codigo = "750000000001",
                    precio = BigDecimal("10.00"),
                    costo = BigDecimal("5.00"),
                    stock = BigDecimal("10"),
                    stockMinimo = BigDecimal("2"),
                    categoria = "Demo",
                    esGranel = false,
                    unidadMedida = UnidadMedida.PIEZA,
                    activo = true,
                    createdAt = now,
                    updatedAt = now
                )
            )
        }
    }
}
