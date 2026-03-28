package com.ipfgold.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant

/**
 * Entidad Room que almacena en caché el precio actual del oro.
 *
 * La caché tiene un TTL (time‑to‑live) de 5 minutos.
 */
@Entity(tableName = "gold_prices")
data class GoldPriceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "price_usd")
    val priceUSD: Double,
    @ColumnInfo(name = "price_eur")
    val priceEUR: Double,
    @ColumnInfo(name = "change_24h")
    val change24h: Double,
    @ColumnInfo(name = "change_percent_24h")
    val changePercent24h: Double,
    @ColumnInfo(name = "timestamp")
    val timestamp: Instant,
    @ColumnInfo(name = "expires_at")
    val expiresAt: Instant
) {
    companion object {
        /**
         * Crea una nueva entidad con un TTL de 5 minutos a partir de `timestamp`.
         */
        fun create(
            priceUSD: Double,
            priceEUR: Double,
            change24h: Double,
            changePercent24h: Double,
            timestamp: Instant,
            ttlMinutes: Long = 5
        ): GoldPriceEntity = GoldPriceEntity(
            priceUSD = priceUSD,
            priceEUR = priceEUR,
            change24h = change24h,
            changePercent24h = changePercent24h,
            timestamp = timestamp,
            expiresAt = timestamp.plusSeconds(ttlMinutes * 60)
        )
    }
}