package com.github.clawbotari.ipfgold.data.remote.mapper

import com.github.clawbotari.ipfgold.data.remote.model.MetalsApiResponse
import com.github.clawbotari.ipfgold.domain.model.GoldPrice
import java.time.Instant
import javax.inject.Inject

/**
 * Mapea la respuesta de Metals-API a [GoldPrice].
 *
 * Metals-API devuelve el precio de 1 onza de oro (XAU) en USD y EUR.
 * No incluye variación de 24 h, por lo que se establece a cero.
 */
class MetalsApiMapper @Inject constructor() {

    fun toGoldPrice(response: MetalsApiResponse): GoldPrice {
        val rates = response.rates ?: throw IllegalArgumentException("Rates missing in Metals-API response")
        val usdRate = rates["USD"] ?: throw IllegalArgumentException("USD rate missing")
        val eurRate = rates["EUR"] ?: throw IllegalArgumentException("EUR rate missing")

        return GoldPrice(
            priceUSD = usdRate,
            priceEUR = eurRate,
            change24h = 0.0,
            changePercent24h = 0.0,
            timestamp = response.timestamp?.let { Instant.ofEpochSecond(it) } ?: Instant.now(),
            isDemo = false
        )
    }
}