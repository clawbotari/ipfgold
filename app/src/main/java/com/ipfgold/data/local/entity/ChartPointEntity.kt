package com.ipfgold.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Entidad Room que almacena un punto histórico del precio del oro.
 *
 * La fecha actúa como clave primaria (solo un precio por día).
 */
@Entity(tableName = "chart_points")
data class ChartPointEntity(
    @PrimaryKey
    @ColumnInfo(name = "date")
    val date: LocalDate,
    @ColumnInfo(name = "price_usd")
    val priceUSD: Double,
    @ColumnInfo(name = "price_eur")
    val priceEUR: Double
)