package com.github.clawbotari.ipfgold.data.remote.mapper

import com.github.clawbotari.ipfgold.data.remote.model.GoldApiResponse
import com.github.clawbotari.ipfgold.domain.model.GoldPrice
import java.time.Instant
import javax.inject.Inject

/**
 * Mapea la respuesta de GoldAPI.io a [GoldPrice].
 *
 * GoldAPI.io devuelve el precio en USD, cambio absoluto (ch) y porcentual (chp).
 * La conversión a EUR se realiza con una tasa fija de respaldo (0.92).
 */
class GoldApiMapper @Inject constructor() {

    companion object {
        private const val FALLBACK_EXCHANGE_RATE = 0.92
    }

    fun toGoldPrice(response: GoldApiResponse): GoldPrice {
        val priceUSD = response.price
        val change24h = response.ch ?: 0.0
        val changePercent24h = response.chp ?: 0.0
        val priceEUR = priceUSD * FALLBACK_EXCHANGE_RATE

        return GoldPrice(
            priceUSD = priceUSD,
            priceEUR = priceEUR,
            change24h = change24h,
            changePercent24h = changePercent24h,
            timestamp = Instant.ofEpochSecond(response.timestamp),
            isDemo = false
        )
    }
}