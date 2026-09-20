package com.mypum.pos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mypum.pos.data.local.dao.DetalleVentaDao
import com.mypum.pos.data.local.dao.EgresoDao
import com.mypum.pos.data.local.dao.HistorialPrecioDao
import com.mypum.pos.data.local.dao.ProductoDao
import com.mypum.pos.data.local.dao.ReporteDao
import com.mypum.pos.data.local.dao.ServicioDao
import com.mypum.pos.data.local.dao.SyncQueueDao
import com.mypum.pos.data.local.dao.TurnoDao
import com.mypum.pos.data.local.dao.UsuarioDao
import com.mypum.pos.data.local.dao.VentaDao
import com.mypum.pos.data.local.entity.DetalleVentaEntity
import com.mypum.pos.data.local.entity.EgresoEntity
import com.mypum.pos.data.local.entity.HistorialPrecioEntity
import com.mypum.pos.data.local.entity.ProductoEntity
import com.mypum.pos.data.local.entity.ServicioEntity
import com.mypum.pos.data.local.entity.SyncQueueEntity
import com.mypum.pos.data.local.entity.TurnoEntity
import com.mypum.pos.data.local.entity.UsuarioEntity
import com.mypum.pos.data.local.entity.VentaEntity

@Database(
    entities = [
        ProductoEntity::class,
        VentaEntity::class,
        DetalleVentaEntity::class,
        EgresoEntity::class,
        TurnoEntity::class,
        UsuarioEntity::class,
        ServicioEntity::class,
        HistorialPrecioEntity::class,
        SyncQueueEntity::class
    ],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class PosDatabase : RoomDatabase() {

    abstract fun productoDao(): ProductoDao
    abstract fun ventaDao(): VentaDao
    abstract fun detalleVentaDao(): DetalleVentaDao
    abstract fun egresoDao(): EgresoDao
    abstract fun turnoDao(): TurnoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun servicioDao(): ServicioDao
    abstract fun historialPrecioDao(): HistorialPrecioDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun reporteDao(): ReporteDao
}
