package com.ipfgold.ui.home

import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.domain.model.Currency
import com.ipfgold.domain.model.GoldPrice
import com.ipfgold.domain.model.PricePeriod

/**
 * Estado de la UI de la pantalla de inicio.
 */
sealed class HomeUiState {

    /**
     * Cargando datos iniciales.
     */
    object Loading : HomeUiState()

    /**
     * Datos cargados exitosamente.
     *
     * @property price Precio actual del oro.
     * @property chartPoints Puntos históricos para el período seleccionado.
     * @property selectedCurrency Moneda de visualización (USD/EUR).
     * @property selectedPeriod Período del gráfico.
     * @property isOffline True si los datos provienen de la caché (sin conexión).
     */
    data class Success(
        val price: GoldPrice,
        val chartPoints: List<ChartPoint>,
        val selectedCurrency: Currency,
        val selectedPeriod: PricePeriod,
        val isOffline: Boolean = false
    ) : HomeUiState()

    /**
     * Error al cargar los datos.
     *
     * @property message Mensaje de error legible.
     * @property cause Causa técnica del error (opcional, para diagnóstico).
     */
    data class Error(
        val message: String,
        val cause: String? = null
    ) : HomeUiState()
}