package com.ipfgold.data.remote.mapper

import com.ipfgold.data.remote.model.GlobalQuoteResponse
import com.ipfgold.domain.model.GoldPrice
import java.time.Instant
import javax.inject.Inject

/**
 * Mapea los DTOs de Alpha Vantage a un modelo de dominio [GoldPrice].
 */
class GoldPriceMapper @Inject constructor() {

    /**
     * Combina la cotización del oro (USD) con la tasa de cambio USD→EUR
     * para producir un [GoldPrice] con precios en ambas monedas.
     *
     * @param quote Respuesta de `GLOBAL_QUOTE` (precio en USD).
     * @param exchangeRate Tasa de cambio USD → EUR (ej. 0.8688).
     * @param timestamp Momento en que se obtuvieron los datos (por defecto ahora).
     * @throws NumberFormatException Si algún campo numérico no puede ser parseado.
     */
    fun toGoldPrice(
        quote: GlobalQuoteResponse,
        exchangeRate: Double,
        timestamp: Instant = Instant.now()
    ): GoldPrice {
        val quoteDto = quote.globalQuote
            ?: throw IllegalStateException("API rate limit reached or invalid response. Check your Alpha Vantage plan.")

        val priceUSD = quoteDto.price
            ?.toDoubleOrNull()
            ?: throw NumberFormatException("quote.price is null or not a number")
        val change24h = quoteDto.change
            ?.toDoubleOrNull()
            ?: throw NumberFormatException("quote.change is null or not a number")
        val changePercent24h = quoteDto.changePercent
            ?.removeSuffix("%")
            ?.toDoubleOrNull()
            ?: throw NumberFormatException("quote.changePercent is null or not a number")

        val priceEUR = priceUSD * exchangeRate

        return GoldPrice(
            priceUSD = priceUSD,
            priceEUR = priceEUR,
            change24h = change24h,
            changePercent24h = changePercent24h,
            timestamp = timestamp
        )
    }
}