package com.ipfgold.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ipfgold.R
import com.ipfgold.domain.model.Currency
import com.ipfgold.domain.model.PricePeriod
import com.ipfgold.ui.theme.IpfGoldTheme

/**
 * Pantalla de configuración de la aplicación.
 *
 * Permite ajustar moneda, período del gráfico, tema e intervalo de actualización.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val currency by viewModel.currency.collectAsStateWithLifecycle(initialValue = Currency.USD)
    val period by viewModel.period.collectAsStateWithLifecycle(initialValue = PricePeriod.ALL)
    val theme by viewModel.theme.collectAsStateWithLifecycle(initialValue = "system")
    val refreshInterval by viewModel.refreshInterval.collectAsStateWithLifecycle(initialValue = 5)

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Título
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 24.dp)
            )

            // Sección: Moneda
            SettingsSectionTitle(text = stringResource(R.string.settings_currency_title))
            CurrencyOption(
                label = stringResource(R.string.settings_currency_usd),
                selected = currency == Currency.USD,
                onClick = { viewModel.setCurrency(Currency.USD) }
            )
            CurrencyOption(
                label = stringResource(R.string.settings_currency_eur),
                selected = currency == Currency.EUR,
                onClick = { viewModel.setCurrency(Currency.EUR) }
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            // Sección: Período del gráfico
            SettingsSectionTitle(text = stringResource(R.string.settings_period_title))
            PeriodOption(
                label = stringResource(R.string.home_period_1d),
                selected = period == PricePeriod.D1,
                onClick = { viewModel.setPeriod(PricePeriod.D1) }
            )
            PeriodOption(
                label = stringResource(R.string.home_period_1w),
                selected = period == PricePeriod.W1,
                onClick = { viewModel.setPeriod(PricePeriod.W1) }
            )
            PeriodOption(
                label = stringResource(R.string.home_period_1m),
                selected = period == PricePeriod.M1,
                onClick = { viewModel.setPeriod(PricePeriod.M1) }
            )
            PeriodOption(
                label = stringResource(R.string.home_period_1y),
                selected = period == PricePeriod.Y1,
                onClick = { viewModel.setPeriod(PricePeriod.Y1) }
            )
            PeriodOption(
                label = stringResource(R.string.home_period_all),
                selected = period == PricePeriod.ALL,
                onClick = { viewModel.setPeriod(PricePeriod.ALL) }
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            // Sección: Tema
            SettingsSectionTitle(text = stringResource(R.string.settings_theme_title))
            ThemeOption(
                label = stringResource(R.string.settings_theme_system),
                selected = theme == "system",
                onClick = { viewModel.setTheme("system") }
            )
            ThemeOption(
                label = stringResource(R.string.settings_theme_light),
                selected = theme == "light",
                onClick = { viewModel.setTheme("light") }
            )
            ThemeOption(
                label = stringResource(R.string.settings_theme_dark),
                selected = theme == "dark",
                onClick = { viewModel.setTheme("dark") }
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            // Sección: Intervalo de actualización
            SettingsSectionTitle(text = stringResource(R.string.settings_refresh_interval_title))
            RefreshIntervalOption(
                label = stringResource(R.string.settings_refresh_interval_5m),
                selected = refreshInterval == 5,
                onClick = { viewModel.setRefreshInterval(5) }
            )
            RefreshIntervalOption(
                label = stringResource(R.string.settings_refresh_interval_15m),
                selected = refreshInterval == 15,
                onClick = { viewModel.setRefreshInterval(15) }
            )
            RefreshIntervalOption(
                label = stringResource(R.string.settings_refresh_interval_30m),
                selected = refreshInterval == 30,
                onClick = { viewModel.setRefreshInterval(30) }
            )
            RefreshIntervalOption(
                label = stringResource(R.string.settings_refresh_interval_1h),
                selected = refreshInterval == 60,
                onClick = { viewModel.setRefreshInterval(60) }
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun SettingsSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
        },
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PeriodOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
        },
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ThemeOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
        },
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RefreshIntervalOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = {
            RadioButton(
                selected = selected,
                onClick = onClick
            )
        },
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    IpfGoldTheme {
        SettingsScreen()
    }
}