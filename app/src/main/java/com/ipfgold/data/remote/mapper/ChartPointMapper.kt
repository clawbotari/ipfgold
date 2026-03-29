package com.ipfgold.data.remote.mapper

import com.ipfgold.data.remote.model.TimeSeriesDailyResponse
import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.domain.model.PricePeriod
import java.time.LocalDate
import javax.inject.Inject

/**
 * Mapea el DTO de series temporales de Alpha Vantage a una lista de [ChartPoint].
 */
class ChartPointMapper @Inject constructor() {

    /**
     * Convierte la respuesta diaria a puntos de gráfico, aplicando un filtro de período
     * y convirtiendo los precios a EUR mediante la tasa proporcionada.
     *
     * @param response Respuesta de `TIME_SERIES_DAILY`.
     * @param exchangeRate Tasa de cambio USD → EUR (ej. 0.8688).
     * @param period Período a filtrar (por defecto todos los datos).
     * @return Lista de puntos ordenados por fecha ascendente.
     * @throws NumberFormatException Si algún precio no puede ser parseado.
     */
    fun toChartPoints(
        response: TimeSeriesDailyResponse,
        exchangeRate: Double,
        period: PricePeriod = PricePeriod.ALL
    ): List<ChartPoint> {
        val startDate = calculateStartDate(period)

        return response.timeSeries
            .mapNotNull { (dateStr, dailyData) ->
                val date = try {
                    LocalDate.parse(dateStr)
                } catch (e: java.time.format.DateTimeParseException) {
                    return@mapNotNull null
                }
                if (date.isBefore(startDate)) return@mapNotNull null

                val priceUSD = dailyData.close.toDouble()
                val priceEUR = priceUSD * exchangeRate

                ChartPoint(
                    date = date,
                    priceUSD = priceUSD,
                    priceEUR = priceEUR
                )
            }
            .sortedBy { it.date }
    }

    private fun calculateStartDate(period: PricePeriod): LocalDate {
        val today = LocalDate.now()
        return when (period) {
            PricePeriod.D1 -> today.minusDays(1)
            PricePeriod.W1 -> today.minusWeeks(1)
            PricePeriod.M1 -> today.minusMonths(1)
            PricePeriod.Y1 -> today.minusYears(1)
            PricePeriod.ALL -> LocalDate.MIN
        }
    }
}