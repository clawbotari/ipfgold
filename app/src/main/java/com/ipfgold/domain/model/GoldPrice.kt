package com.ipfgold.domain.model

import java.time.Instant

/**
 * Precio actual del oro en USD y EUR, con variación en las últimas 24 horas.
 */
data class GoldPrice(
    val priceUSD: Double,
    val priceEUR: Double,
    val change24h: Double,
    val changePercent24h: Double,
    val timestamp: Instant,
    val isDemo: Boolean = false
) {
    /**
     * Devuelve el precio en la moneda solicitada.
     */
    fun priceIn(currency: Currency): Double = when (currency) {
        Currency.USD -> priceUSD
        Currency.EUR -> priceEUR
    }
}