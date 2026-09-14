package com.mypum.pos.data.mapper
import com.mypum.pos.data.local.entity.ProductoEntity
import com.mypum.pos.domain.model.Producto
fun ProductoEntity.toDomain()=Producto(id,nombre,codigo,precio,costo,stock,stockMinimo,categoria,esGranel,unidadMedida,activo,createdAt,updatedAt)
fun Producto.toEntity()=ProductoEntity(id,nombre,codigo,precio,costo,stock,stockMinimo,categoria,esGranel,unidadMedida,activo,createdAt,updatedAt)
