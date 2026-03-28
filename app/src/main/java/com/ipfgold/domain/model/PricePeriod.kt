package com.ipfgold.domain.model

/**
 * Periodos de tiempo para el gráfico histórico.
 */
enum class PricePeriod {
    D1,   // 1 día
    W1,   // 1 semana (7 días)
    M1,   // 1 mes (30 días)
    Y1,   // 1 año (365 días)
    ALL   // todos los datos disponibles
}