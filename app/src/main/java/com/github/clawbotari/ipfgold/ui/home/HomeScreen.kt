package com.github.clawbotari.ipfgold.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.clawbotari.ipfgold.utils.DebugLog
import com.github.clawbotari.ipfgold.utils.LogType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.Row
import com.github.clawbotari.ipfgold.R
import com.github.clawbotari.ipfgold.ui.theme.WarningYellow
import com.github.clawbotari.ipfgold.ui.home.components.ErrorCard
import com.github.clawbotari.ipfgold.ui.home.components.GoldChart
import com.github.clawbotari.ipfgold.ui.home.components.OfflineBanner
import com.github.clawbotari.ipfgold.ui.home.components.PeriodSelector
import com.github.clawbotari.ipfgold.ui.home.components.PriceCard
import com.github.clawbotari.ipfgold.ui.theme.IpfGoldTheme

/**
 * Pantalla principal de la aplicación.
 *
 * Muestra el precio actual del oro, gráfico histórico y controles de período/moneda.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(initialValue = HomeUiState.Loading)
    val isDebugMode by viewModel.isDebugMode.collectAsStateWithLifecycle(initialValue = false)
    val debugLogs by viewModel.debugLogs.collectAsStateWithLifecycle(initialValue = emptyList<DebugLog>())



    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.loadData() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Refresh, contentDescription = stringResource(R.string.home_pull_to_refresh))
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {


            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    LoadingScreen()
                }

                is HomeUiState.Success -> {
                    SuccessScreen(
                        price = state.price,
                        chartPoints = state.chartPoints,
                        selectedCurrency = state.selectedCurrency,
                        selectedPeriod = state.selectedPeriod,
                        isOffline = state.isOffline,
                        isDemo = state.isDemo,
                        isDebugMode = isDebugMode,
                        debugLogs = debugLogs,
                        onCurrencyToggle = viewModel::setCurrency,
                        onPeriodSelected = viewModel::setPeriod,
                        onClearDebugLogs = { viewModel.clearDebugLogs() },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is HomeUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        cause = state.cause,
                        onRetry = { viewModel.loadData() }
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(48.dp),
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = "Cargando datos del oro...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun DemoBanner(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = WarningYellow.copy(alpha = 0.9f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Datos de demostración — API no disponible",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun SuccessScreen(
    price: com.github.clawbotari.ipfgold.domain.model.GoldPrice,
    chartPoints: List<com.github.clawbotari.ipfgold.domain.model.ChartPoint>,
    selectedCurrency: com.github.clawbotari.ipfgold.domain.model.Currency,
    selectedPeriod: com.github.clawbotari.ipfgold.domain.model.PricePeriod,
    isOffline: Boolean,
    isDemo: Boolean,
    isDebugMode: Boolean,
    debugLogs: List<DebugLog>,
    onCurrencyToggle: (com.github.clawbotari.ipfgold.domain.model.Currency) -> Unit,
    onPeriodSelected: (com.github.clawbotari.ipfgold.domain.model.PricePeriod) -> Unit,
    onClearDebugLogs: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner de datos de demostración (si aplica)
        if (isDemo) {
            DemoBanner(modifier = Modifier.padding(horizontal = 16.dp))
        } else if (isOffline) {
            // Banner offline (si aplica)
            OfflineBanner(modifier = Modifier.padding(horizontal = 16.dp))
        }

        // Tarjeta de precio
        PriceCard(
            price = price,
            selectedCurrency = selectedCurrency,
            onCurrencyToggle = onCurrencyToggle,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Selector de período
        PeriodSelector(
            selectedPeriod = selectedPeriod,
            onPeriodSelected = onPeriodSelected
        )

        // Gráfico
        GoldChart(
            points = chartPoints,
            currency = selectedCurrency,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Panel de debug
        if (isDebugMode) {
            DebugPanel(
                logs = debugLogs,
                onClear = onClearDebugLogs
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    cause: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ErrorCard(
            message = message,
            onRetry = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
        )
        if (cause != null) {
            Text(
                text = cause,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun DebugPanel(
    logs: List<DebugLog>,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(300.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Debug", style = MaterialTheme.typography.labelMedium)
                androidx.compose.material3.TextButton(onClick = onClear) { 
                    Text("Limpiar") 
                }
            }
            androidx.compose.foundation.lazy.LazyColumn(
                modifier = Modifier.fillMaxSize(),
                reverseLayout = true
            ) {
                items(logs.reversed()) { log ->
                    DebugLogItem(log)
                }
            }
        }
    }
}

@Composable
private fun DebugLogItem(log: DebugLog) {
    val time = java.time.Instant.ofEpochMilli(log.timestamp)
        .atZone(java.time.ZoneId.systemDefault())
        .toLocalTime()
        .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"))
    val color = when (log.type) {
        LogType.REQUEST -> MaterialTheme.colorScheme.primary
        LogType.RESPONSE -> MaterialTheme.colorScheme.secondary
        LogType.ERROR -> MaterialTheme.colorScheme.error
        LogType.INFO -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    androidx.compose.material3.Text(
        text = "$time [${log.type.name}] ${log.message}",
        style = MaterialTheme.typography.bodySmall,
        color = color,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@Preview(showBackground = true, device = "spec:parent=pixel_5")
@Composable
private fun HomeScreenPreview() {
    IpfGoldTheme {
        HomeScreen()
    }
}