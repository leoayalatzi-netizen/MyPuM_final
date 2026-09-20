package com.mypum.pos.di

import android.content.Context
import androidx.room.Room
import com.mypum.pos.data.local.DatabaseSeeder
import com.mypum.pos.data.local.PosDatabase
import com.mypum.pos.data.local.migration.MIGRATION_1_2
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun db(
        @ApplicationContext context: Context
    ): PosDatabase =
        Room.databaseBuilder(
            context,
            PosDatabase::class.java,
            "mypum.db"
        ).addMigrations(MIGRATION_1_2).build()

    @Provides
    fun product(d: PosDatabase): ProductoDao =
        d.productoDao()

    @Provides
    fun sale(d: PosDatabase): VentaDao =
        d.ventaDao()

    @Provides
    fun detalleVenta(d: PosDatabase): DetalleVentaDao =
        d.detalleVentaDao()

    @Provides
    fun egreso(d: PosDatabase): EgresoDao =
        d.egresoDao()

    @Provides
    fun turno(d: PosDatabase): TurnoDao =
        d.turnoDao()

    @Provides
    fun usuario(d: PosDatabase): UsuarioDao =
        d.usuarioDao()

    @Provides
    fun servicio(d: PosDatabase): ServicioDao =
        d.servicioDao()

    @Provides
    fun precio(d: PosDatabase): HistorialPrecioDao =
        d.historialPrecioDao()

    @Provides
    fun sync(d: PosDatabase): SyncQueueDao =
        d.syncQueueDao()

    @Provides
    fun reporte(d: PosDatabase): ReporteDao =
        d.reporteDao()

    @Provides
    fun seeder(
        d: ProductoDao,
        u: UsuarioDao
    ): DatabaseSeeder =
        DatabaseSeeder(d, u)
}
