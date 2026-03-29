package com.ipfgold.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.ipfgold.R
import com.ipfgold.ui.home.components.ErrorCard
import com.ipfgold.ui.home.components.GoldChart
import com.ipfgold.ui.home.components.OfflineBanner
import com.ipfgold.ui.home.components.PeriodSelector
import com.ipfgold.ui.home.components.PriceCard
import com.ipfgold.ui.theme.IpfGoldTheme

/**
 * Pantalla principal de la aplicación.
 *
 * Muestra el precio actual del oro, gráfico histórico y controles de período/moneda.
 */
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()



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
                .padding(innerPadding)

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
                        onCurrencyToggle = viewModel::setCurrency,
                        onPeriodSelected = viewModel::setPeriod,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is HomeUiState.Error -> {
                    ErrorScreen(
                        message = state.message,
                        canRetry = state.canRetry,
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
private fun SuccessScreen(
    price: com.ipfgold.domain.model.GoldPrice,
    chartPoints: List<com.ipfgold.domain.model.ChartPoint>,
    selectedCurrency: com.ipfgold.domain.model.Currency,
    selectedPeriod: com.ipfgold.domain.model.PricePeriod,
    isOffline: Boolean,
    onCurrencyToggle: (com.ipfgold.domain.model.Currency) -> Unit,
    onPeriodSelected: (com.ipfgold.domain.model.PricePeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Banner offline (si aplica)
        if (isOffline) {
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
    }
}

@Composable
private fun ErrorScreen(
    message: String,
    canRetry: Boolean,
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
                .padding(32.dp)
        )
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_5")
@Composable
private fun HomeScreenPreview() {
    IpfGoldTheme {
        HomeScreen()
    }
}