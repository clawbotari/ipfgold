package com.ipfgold.data.remote.mapper

import com.ipfgold.data.remote.model.CurrencyExchangeResponse
import com.ipfgold.data.remote.model.ExchangeRateDto
import com.ipfgold.data.remote.model.GlobalQuoteDto
import com.ipfgold.data.remote.model.GlobalQuoteResponse
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant

class GoldPriceMapperTest {

    private val mapper = GoldPriceMapper()

    @Test
    fun `toGoldPrice converts USD to EUR using exchange rate`() {
        // Given
        val quote = GlobalQuoteResponse(
            globalQuote = GlobalQuoteDto(
                symbol = "XAUUSD",
                price = "2150.50",
                change = "10.25",
                changePercent = "0.48%",
                latestTradingDay = "2026-03-29",
                previousClose = "2140.25"
            )
        )
        val exchange = CurrencyExchangeResponse(
            exchangeRate = ExchangeRateDto(
                fromCurrency = "USD",
                fromCurrencyName = "US Dollar",
                toCurrency = "EUR",
                toCurrencyName = "Euro",
                rate = "0.8688",
                lastRefreshed = "2026-03-29 10:00:00",
                timeZone = "UTC"
            )
        )
        val fixedTimestamp = Instant.parse("2026-03-29T10:00:00Z")

        // When
        val result = mapper.toGoldPrice(quote, exchange, fixedTimestamp)

        // Then
        assertEquals(2150.50, result.priceUSD, 0.001)
        // priceEUR = 2150.50 * 0.8688 = 1868.3472
        val expectedPriceEUR = 2150.50 * 0.8688
        assertEquals(expectedPriceEUR, result.priceEUR, 0.001)
        assertEquals(10.25, result.change24h, 0.001)
        assertEquals(0.48, result.changePercent24h, 0.001)
        assertEquals(fixedTimestamp, result.timestamp)
    }

    @Test
    fun `toGoldPrice handles negative change`() {
        // Given
        val quote = GlobalQuoteResponse(
            globalQuote = GlobalQuoteDto(
                symbol = "XAUUSD",
                price = "2100.00",
                change = "-5.75",
                changePercent = "-0.27%",
                latestTradingDay = "2026-03-29",
                previousClose = "2105.75"
            )
        )
        val exchange = CurrencyExchangeResponse(
            exchangeRate = ExchangeRateDto(
                fromCurrency = "USD",
                fromCurrencyName = "US Dollar",
                toCurrency = "EUR",
                toCurrencyName = "Euro",
                rate = "0.8700",
                lastRefreshed = "2026-03-29 10:00:00",
                timeZone = "UTC"
            )
        )

        // When
        val result = mapper.toGoldPrice(quote, exchange)

        // Then
        assertEquals(2100.00, result.priceUSD, 0.001)
        assertEquals(2100.00 * 0.8700, result.priceEUR, 0.001)
        assertEquals(-5.75, result.change24h, 0.001)
        assertEquals(-0.27, result.changePercent24h, 0.001)
    }

    @Test(expected = NumberFormatException::class)
    fun `toGoldPrice throws NumberFormatException on invalid numeric strings`() {
        // Given
        val quote = GlobalQuoteResponse(
            globalQuote = GlobalQuoteDto(
                symbol = "XAUUSD",
                price = "invalid",
                change = "10.25",
                changePercent = "0.48%",
                latestTradingDay = "2026-03-29",
                previousClose = "2140.25"
            )
        )
        val exchange = CurrencyExchangeResponse(
            exchangeRate = ExchangeRateDto(
                fromCurrency = "USD",
                fromCurrencyName = "US Dollar",
                toCurrency = "EUR",
                toCurrencyName = "Euro",
                rate = "0.8688",
                lastRefreshed = "2026-03-29 10:00:00",
                timeZone = "UTC"
            )
        )

        // When / Then
        mapper.toGoldPrice(quote, exchange)
    }

    @Test
    fun `toGoldPrice removes percent sign from changePercent`() {
        // Given
        val quote = GlobalQuoteResponse(
            globalQuote = GlobalQuoteDto(
                symbol = "XAUUSD",
                price = "2150.50",
                change = "10.25",
                changePercent = "1.25%",  // extra digits after decimal
                latestTradingDay = "2026-03-29",
                previousClose = "2140.25"
            )
        )
        val exchange = CurrencyExchangeResponse(
            exchangeRate = ExchangeRateDto(
                fromCurrency = "USD",
                fromCurrencyName = "US Dollar",
                toCurrency = "EUR",
                toCurrencyName = "Euro",
                rate = "0.85",
                lastRefreshed = "2026-03-29 10:00:00",
                timeZone = "UTC"
            )
        )

        // When
        val result = mapper.toGoldPrice(quote, exchange)

        // Then
        assertEquals(1.25, result.changePercent24h, 0.001)
    }
}