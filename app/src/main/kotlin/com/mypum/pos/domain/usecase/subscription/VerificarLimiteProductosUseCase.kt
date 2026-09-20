package com.mypum.pos.domain.usecase.subscription

import com.mypum.pos.domain.model.subscription.Plan
import com.mypum.pos.domain.model.subscription.PlanEntitlements
import com.mypum.pos.domain.repository.ProductoRepository
import com.mypum.pos.domain.repository.subscription.SubscriptionRepository
import javax.inject.Inject

class VerificarLimiteProductosUseCase @Inject constructor(
    private val productoRepository: ProductoRepository,
    private val subscriptionRepository: SubscriptionRepository
) {

    suspend operator fun invoke(): Resultado {

        val plan = subscriptionRepository.getPlan()
        val entitlements = PlanEntitlements.forPlan(plan)

        val limite = entitlements.maxProducts

        if (limite == null) {
            return Resultado.Permitido(
                plan = plan,
                productosActuales = productoRepository.contarProductos(),
                limite = null
            )
        }

        val actuales = productoRepository.contarProductos()

        return if (actuales < limite) {
            Resultado.Permitido(
                plan = plan,
                productosActuales = actuales,
                limite = limite
            )
        } else {
            Resultado.LimiteAlcanzado(
                plan = plan,
                productosActuales = actuales,
                limite = limite
            )
        }
    }

    sealed interface Resultado {

        data class Permitido(
            val plan: Plan,
            val productosActuales: Int,
            val limite: Int?
        ) : Resultado

        data class LimiteAlcanzado(
            val plan: Plan,
            val productosActuales: Int,
            val limite: Int
        ) : Resultado
    }
}
