package com.ipfgold.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ipfgold.domain.model.Currency
import com.ipfgold.domain.model.PricePeriod
import com.ipfgold.domain.repository.GoldPriceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

/**
 * ViewModel para la pantalla de inicio.
 *
 * Gestiona la carga del precio actual y del histórico,
 * refresco automático cada 5 minutos, y cambios de moneda/período.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: GoldPriceRepository
) : ViewModel() {

    // Estado interno mutable
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    // Configuración actual
    private var selectedCurrency: Currency = Currency.USD
    private var selectedPeriod: PricePeriod = PricePeriod.ALL

    // Job del refresco automático
    private var refreshJob: Job? = null

    init {
        loadData()
        startAutoRefresh(intervalMinutes = 5)
    }

    /**
     * Carga el precio actual y el histórico según la configuración actual.
     */
    fun loadData(
        currency: Currency? = null,
        period: PricePeriod? = null
    ) {
        currency?.let { selectedCurrency = it }
        period?.let { selectedPeriod = it }

        viewModelScope.launch {
            _uiState.update { HomeUiState.Loading }

            try {
                val price = repository.getCurrentPrice()
                val chartPoints = repository.getHistoricalPrices(selectedPeriod)

                _uiState.update {
                    HomeUiState.Success(
                        price = price,
                        chartPoints = chartPoints,
                        selectedCurrency = selectedCurrency,
                        selectedPeriod = selectedPeriod,
                        isOffline = false // TODO: detectar offline (network monitor)
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    HomeUiState.Error(
                        message = "No se pudieron cargar los datos: ${e.message ?: "Error desconocido"}",
                        canRetry = true
                    )
                }
            }
        }
    }

    /**
     * Cambia la moneda de visualización y recarga los datos (si es necesario).
     */
    fun setCurrency(currency: Currency) {
        if (currency == selectedCurrency) return
        selectedCurrency = currency
        // No es necesario recargar datos, solo cambiar la visualización.
        // Actualizamos el estado actual si es Success.
        val current = _uiState.value
        if (current is HomeUiState.Success) {
            _uiState.update {
                current.copy(selectedCurrency = currency)
            }
        }
    }

    /**
     * Cambia el período del gráfico y recarga el histórico.
     */
    fun setPeriod(period: PricePeriod) {
        if (period == selectedPeriod) return
        selectedPeriod = period
        // Recargar histórico para el nuevo período.
        viewModelScope.launch {
            val current = _uiState.value
            if (current is HomeUiState.Success) {
                _uiState.update { HomeUiState.Loading }
                try {
                    val chartPoints = repository.getHistoricalPrices(period)
                    _uiState.update {
                        current.copy(
                            chartPoints = chartPoints,
                            selectedPeriod = period
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        current.copy(isOffline = true) // mantener datos anteriores pero marcar offline
                    }
                }
            } else {
                // Si no estamos en Success, hacer una carga completa.
                loadData(period = period)
            }
        }
    }

    /**
     * Inicia el refresco automático cada `intervalMinutes` minutos.
     */
    private fun startAutoRefresh(intervalMinutes: Long) {
        refreshJob = viewModelScope.launch {
            while (true) {
                delay(intervalMinutes * 60 * 1000)
                // Solo refrescar si estamos en estado Success.
                if (_uiState.value is HomeUiState.Success) {
                    loadData()
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }
}