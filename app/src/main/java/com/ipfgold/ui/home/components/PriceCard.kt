package com.ipfgold.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ipfgold.R
import com.ipfgold.domain.model.Currency
import com.ipfgold.domain.model.GoldPrice
import com.ipfgold.ui.theme.ChangeNegative
import com.ipfgold.ui.theme.ChangePositive
import com.ipfgold.ui.theme.IpfGoldTheme
import com.ipfgold.ui.theme.PriceLarge
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

/**
 * Tarjeta que muestra el precio actual del oro con variación 24h y selector de moneda.
 *
 * @param price Datos del precio actual.
 * @param selectedCurrency Moneda seleccionada (USD/EUR).
 * @param onCurrencyToggle Callback cuando se cambia la moneda.
 * @param modifier Modificador Compose.
 */
@Composable
fun PriceCard(
    price: GoldPrice,
    selectedCurrency: Currency,
    onCurrencyToggle: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Título y subtítulo
            Text(
                text = stringResource(R.string.home_price_card_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = stringResource(R.string.home_price_card_subtitle) + " " +
                        formatTimestamp(price.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Fila: precio + selector de moneda
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    // Precio grande
                    Text(
                        text = formatPrice(
                            value = if (selectedCurrency == Currency.USD) price.priceUSD else price.priceEUR,
                            currency = selectedCurrency
                        ),
                        style = PriceLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Variación 24h
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = formatChange(price.change24h, selectedCurrency),
                            style = if (price.change24h >= 0) ChangePositive else ChangeNegative
                        )
                        Text(
                            text = "(${formatPercent(price.changePercent24h)})",
                            style = if (price.changePercent24h >= 0) ChangePositive else ChangeNegative
                        )
                    }
                }

                // Selector de moneda (icon buttons)
                CurrencySelector(
                    items = listOf(
                        Currency.USD to Icons.Default.AttachMoney,
                        Currency.EUR to Icons.Default.Euro
                    ),
                    selectedItem = selectedCurrency,
                    onItemSelect = onCurrencyToggle,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}

/**
 * Selector de moneda con iconos.
 */
@Composable
private fun CurrencySelector(
    items: List<Pair<Currency, androidx.compose.ui.graphics.vector.ImageVector>>,
    selectedItem: Currency,
    onItemSelect: (Currency) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { (currency, icon) ->
            IconButton(
                onClick = { onItemSelect(currency) },
                modifier = Modifier.size(48.dp),
                enabled = currency != selectedItem
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = when (currency) {
                        Currency.USD -> stringResource(R.string.home_currency_toggle_usd)
                        Currency.EUR -> stringResource(R.string.home_currency_toggle_eur)
                    },
                    tint = if (currency == selectedItem) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    }
                )
            }
        }
    }
}

/**
 * Formatea un timestamp a texto legible (ej. "28/03/2026 22:30").
 */
private fun formatTimestamp(timestamp: Instant): String {
    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
        .withZone(ZoneId.systemDefault())
    return formatter.format(timestamp)
}

/**
 * Formatea un precio con símbolo de moneda y separadores de miles.
 */
private fun formatPrice(value: Double, currency: Currency): String {
    val symbol = when (currency) {
        Currency.USD -> "$"
        Currency.EUR -> "€"
    }
    val formattedValue = "%,.2f".format(value).replace(',', ' ')
    return "$symbol$formattedValue"
}

/**
 * Formatea un cambio absoluto con signo y símbolo de moneda.
 */
private fun formatChange(change: Double, currency: Currency): String {
    val symbol = when (currency) {
        Currency.USD -> "$"
        Currency.EUR -> "€"
    }
    val sign = if (change >= 0) "+" else ""
    return "$sign$symbol${"%,.2f".format(change)}"
}

/**
 * Formatea un porcentaje con signo.
 */
private fun formatPercent(percent: Double): String {
    val sign = if (percent >= 0) "+" else ""
    return "$sign${"%.2f".format(percent)}%"
}

@Preview(showBackground = true)
@Composable
private fun PriceCardPreview() {
    IpfGoldTheme {
        PriceCard(
            price = GoldPrice(
                priceUSD = 4495.15,
                priceEUR = 3902.34,
                change24h = 89.84,
                changePercent24h = 2.04,
                timestamp = Instant.now()
            ),
            selectedCurrency = Currency.USD,
            onCurrencyToggle = {}
        )
    }
}