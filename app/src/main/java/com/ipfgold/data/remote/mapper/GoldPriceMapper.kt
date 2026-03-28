package com.ipfgold.data.remote.mapper

import com.ipfgold.data.remote.model.CurrencyExchangeResponse
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
     * @param exchange Respuesta de `CURRENCY_EXCHANGE_RATE`.
     * @param timestamp Momento en que se obtuvieron los datos (por defecto ahora).
     * @throws NumberFormatException Si algún campo numérico no puede ser parseado.
     */
    fun toGoldPrice(
        quote: GlobalQuoteResponse,
        exchange: CurrencyExchangeResponse,
        timestamp: Instant = Instant.now()
    ): GoldPrice {
        val quoteDto = quote.globalQuote
        val exchangeDto = exchange.exchangeRate

        val priceUSD = quoteDto.price.toDouble()
        val change24h = quoteDto.change.toDouble()
        val changePercent24h = quoteDto.changePercent.removeSuffix("%").toDouble()
        val exchangeRate = exchangeDto.rate.toDouble()

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