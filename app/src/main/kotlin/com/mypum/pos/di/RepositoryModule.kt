package com.mypum.pos.di
import com.mypum.pos.domain.repository.*
import com.mypum.pos.domain.usecase.egresos.RegistrarEgresoUseCase
import com.mypum.pos.domain.usecase.turno.AbrirTurnoUseCase
import com.mypum.pos.domain.usecase.turno.CerrarTurnoUseCase
import com.mypum.pos.domain.usecase.turno.CalcularCierreUseCase
import com.mypum.pos.data.repository.*
import com.mypum.pos.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
@Module @InstallIn(SingletonComponent::class) object RepositoryModule {
 @Provides fun product(d:ProductoDao):ProductoRepository=ProductoRepositoryImpl(d)
 @Provides fun sale(d:VentaDao,p:ProductoDao,db:com.mypum.pos.data.local.PosDatabase):VentaRepository=VentaRepositoryImpl(d,p,db)
 @Provides fun egreso(d:EgresoDao):EgresoRepository=EgresoRepositoryImpl(d)
 @Provides fun turno(d:TurnoDao):TurnoRepository=TurnoRepositoryImpl(d)
 @Provides fun usuario(d:UsuarioDao):UsuarioRepository=UsuarioRepositoryImpl(d)
 @Provides fun servicio(d:ServicioDao):ServicioRepository=ServicioRepositoryImpl(d)
 @Provides fun precio(p:ProductoDao,h:HistorialPrecioDao):PrecioRepository=PrecioRepositoryImpl(p,h)
 @Provides fun reporte(d:ReporteDao):ReporteRepository=ReporteRepositoryImpl(d)

 @Provides fun registrarEgresoUseCase(
     r:EgresoRepository
 ):RegistrarEgresoUseCase=RegistrarEgresoUseCase(r)

 @Provides fun abrirTurnoUseCase(
     r:TurnoRepository
 ):AbrirTurnoUseCase=AbrirTurnoUseCase(r)

 @Provides fun cerrarTurnoUseCase(
     r:TurnoRepository
 ):CerrarTurnoUseCase=CerrarTurnoUseCase(r)

 @Provides fun calcularCierreUseCase():CalcularCierreUseCase=CalcularCierreUseCase()
}
