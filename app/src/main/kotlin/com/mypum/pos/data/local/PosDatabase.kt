package com.mypum.pos.data.local
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mypum.pos.data.local.entity.*
import com.mypum.pos.data.local.dao.*
@Database(entities=[ProductoEntity::class,VentaEntity::class,DetalleVentaEntity::class,EgresoEntity::class,TurnoEntity::class,UsuarioEntity::class,ServicioEntity::class,HistorialPrecioEntity::class,SyncQueueEntity::class],version=1,exportSchema=true)
@TypeConverters(Converters::class)
abstract class PosDatabase:RoomDatabase(){
 abstract fun productoDao():ProductoDao; abstract fun ventaDao():VentaDao; abstract fun egresoDao():EgresoDao; abstract fun turnoDao():TurnoDao; abstract fun usuarioDao():UsuarioDao; abstract fun servicioDao():ServicioDao; abstract fun historialPrecioDao():HistorialPrecioDao; abstract fun syncQueueDao():SyncQueueDao; abstract fun reporteDao():ReporteDao
}
