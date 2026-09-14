package com.mypum.pos.domain.usecase.venta
import com.mypum.pos.domain.repository.VentaRepository
import com.mypum.pos.domain.model.Venta
class RegistrarVentaUseCase(private val repo:VentaRepository){ suspend operator fun invoke(v:Venta)=repo.registrar(v) }