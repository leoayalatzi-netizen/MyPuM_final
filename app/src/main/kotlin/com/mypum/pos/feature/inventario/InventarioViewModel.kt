package com.mypum.pos.feature.inventario

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypum.pos.domain.model.Producto
import com.mypum.pos.domain.repository.ProductoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class InventarioViewModel @Inject constructor(
    private val productoRepository: ProductoRepository
) : ViewModel() {

    val productos: StateFlow<List<Producto>> =
        productoRepository
            .observeAll()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    fun eliminar(producto: Producto) {
        viewModelScope.launch {
            productoRepository.eliminar(producto)
        }
    }
}
