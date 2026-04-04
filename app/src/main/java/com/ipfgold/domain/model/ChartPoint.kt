package com.ipfgold.domain.model

import java.time.LocalDate

/**
 * Punto de un gráfico histórico: fecha y precio en USD/EUR.
 */
data class ChartPoint(
    val date: LocalDate,
    val priceUSD: Double,
    val priceEUR: Double,
    val isDemo: Boolean = false
)