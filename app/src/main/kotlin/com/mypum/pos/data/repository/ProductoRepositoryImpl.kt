package com.mypum.pos.data.repository
import com.mypum.pos.domain.repository.ProductoRepository
import com.mypum.pos.data.local.dao.ProductoDao
import com.mypum.pos.domain.model.*
import com.mypum.pos.data.mapper.*
import kotlinx.coroutines.flow.map
class ProductoRepositoryImpl(private val dao:ProductoDao):ProductoRepository{
 override fun observeAll()=dao.observeAll().map{it.map(ProductoEntity::toDomain)}
 override suspend fun buscarPorCodigo(codigo:String)=dao.byCodigo(codigo)?.toDomain()
 override suspend fun guardar(producto:Producto)=dao.insert(producto.toEntity())
}
