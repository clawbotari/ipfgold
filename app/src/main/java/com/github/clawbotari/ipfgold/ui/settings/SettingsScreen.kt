package com.github.clawbotari.ipfgold.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import com.github.clawbotari.ipfgold.R
import com.github.clawbotari.ipfgold.domain.model.Currency
import com.github.clawbotari.ipfgold.domain.model.DataSource
import com.github.clawbotari.ipfgold.domain.model.PricePeriod
import com.github.clawbotari.ipfgold.ui.theme.IpfGoldTheme

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
    val dataSource by viewModel.dataSource.collectAsStateWithLifecycle(initialValue = DataSource.ALPHA_VANTAGE)
    val alphaVantageApiKey by viewModel.alphaVantageApiKey.collectAsStateWithLifecycle(initialValue = "")
    val metalsApiKey by viewModel.metalsApiKey.collectAsStateWithLifecycle(initialValue = "")
    val goldApiKey by viewModel.goldApiKey.collectAsStateWithLifecycle(initialValue = "")
    val debugMode by viewModel.debugMode.collectAsStateWithLifecycle(initialValue = false)

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

            // Sección: Fuente de datos
            SettingsSectionTitle(text = "Fuente de datos")
            DataSourceOption(
                label = "Alpha Vantage — Gratuito, 25 llamadas/día",
                selected = dataSource == DataSource.ALPHA_VANTAGE,
                onClick = { viewModel.setDataSource(DataSource.ALPHA_VANTAGE) }
            )
            DataSourceOption(
                label = "Metals-API — Gratuito, 100 llamadas/mes",
                selected = dataSource == DataSource.METALS_API,
                onClick = { viewModel.setDataSource(DataSource.METALS_API) }
            )
            DataSourceOption(
                label = "GoldAPI.io — Gratuito, 100 llamadas/mes",
                selected = dataSource == DataSource.GOLD_API,
                onClick = { viewModel.setDataSource(DataSource.GOLD_API) }
            )
            Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            // Campo de API key visible solo para la fuente seleccionada
            when (dataSource) {
                DataSource.ALPHA_VANTAGE -> ApiKeyField(
                    label = "Alpha Vantage API Key",
                    value = alphaVantageApiKey,
                    onValueChange = { viewModel.setAlphaVantageApiKey(it) },
                    hint = "Ej: ABC123XYZ",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                DataSource.METALS_API -> ApiKeyField(
                    label = "Metals-API Key",
                    value = metalsApiKey,
                    onValueChange = { viewModel.setMetalsApiKey(it) },
                    hint = "Ej: abc123xyz456",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
                DataSource.GOLD_API -> ApiKeyField(
                    label = "GoldAPI.io Key",
                    value = goldApiKey,
                    onValueChange = { viewModel.setGoldApiKey(it) },
                    hint = "Ej: goldapi-xxxx",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

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
            Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))

            // Sección: Modo debug
            SettingsSectionTitle(text = "Modo debug")
            DebugOption(
                label = "Activar panel de debug en la pantalla principal",
                enabled = debugMode,
                onCheckedChange = { viewModel.setDebugMode(it) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DataSourceOption(
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
private fun DebugOption(
    label: String,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = {
            Switch(
                checked = enabled,
                onCheckedChange = onCheckedChange
            )
        },
        modifier = Modifier.padding(horizontal = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(hint) },
        modifier = modifier.fillMaxWidth(),
        visualTransformation = if (visible) VisualTransformation.None
        else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Filled.Visibility
                    else Icons.Filled.VisibilityOff,
                    contentDescription = if (visible) "Ocultar key" else "Mostrar key"
                )
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingsScreenPreview() {
    IpfGoldTheme {
        SettingsScreen()
    }
}