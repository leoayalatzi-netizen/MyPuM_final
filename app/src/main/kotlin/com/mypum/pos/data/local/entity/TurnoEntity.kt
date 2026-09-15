package com.mypum.pos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.Instant

@Entity(tableName = "turnos")
data class TurnoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val usuarioId: Long,

    val fondoInicial: BigDecimal,

    val abierto: Boolean,

    val openedAt: Instant,

    val closedAt: Instant?,

    val efectivoContado: BigDecimal? = null,

    val diferencia: BigDecimal? = null
)
