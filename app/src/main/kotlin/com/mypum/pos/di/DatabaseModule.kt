package com.mypum.pos.di

import android.content.Context
import androidx.room.Room
import com.mypum.pos.data.local.PosDatabase
import com.mypum.pos.data.local.migration.MIGRATION_1_2
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
        @ApplicationContext c: Context
    ): PosDatabase =
        Room.databaseBuilder(
            c,
            PosDatabase::class.java,
            "mypum.db"
        )
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun product(d: PosDatabase) = d.productoDao()

    @Provides
    fun sale(d: PosDatabase) = d.ventaDao()

    @Provides
    fun egreso(d: PosDatabase) = d.egresoDao()

    @Provides
    fun turno(d: PosDatabase) = d.turnoDao()

    @Provides
    fun usuario(d: PosDatabase) = d.usuarioDao()

    @Provides
    fun servicio(d: PosDatabase) = d.servicioDao()

    @Provides
    fun precio(d: PosDatabase) = d.historialPrecioDao()

    @Provides
    fun sync(d: PosDatabase) = d.syncQueueDao()

    @Provides
    fun reporte(d: PosDatabase) = d.reporteDao()
}
