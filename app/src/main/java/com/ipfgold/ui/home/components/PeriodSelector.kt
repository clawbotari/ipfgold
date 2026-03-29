package com.ipfgold.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ipfgold.R
import com.ipfgold.domain.model.PricePeriod
import com.ipfgold.ui.theme.IpfGoldTheme

/**
 * Selector horizontal de período para el gráfico.
 *
 * @param selectedPeriod Período actualmente seleccionado.
 * @param onPeriodSelected Callback cuando se selecciona un nuevo período.
 * @param modifier Modificador Compose.
 */
@Composable
fun PeriodSelector(
    selectedPeriod: PricePeriod,
    onPeriodSelected: (PricePeriod) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PeriodChip(
            period = PricePeriod.D1,
            label = stringResource(R.string.home_period_1d),
            selected = selectedPeriod == PricePeriod.D1,
            onSelected = onPeriodSelected
        )
        PeriodChip(
            period = PricePeriod.W1,
            label = stringResource(R.string.home_period_1w),
            selected = selectedPeriod == PricePeriod.W1,
            onSelected = onPeriodSelected
        )
        PeriodChip(
            period = PricePeriod.M1,
            label = stringResource(R.string.home_period_1m),
            selected = selectedPeriod == PricePeriod.M1,
            onSelected = onPeriodSelected
        )
        PeriodChip(
            period = PricePeriod.Y1,
            label = stringResource(R.string.home_period_1y),
            selected = selectedPeriod == PricePeriod.Y1,
            onSelected = onPeriodSelected
        )
        PeriodChip(
            period = PricePeriod.ALL,
            label = stringResource(R.string.home_period_all),
            selected = selectedPeriod == PricePeriod.ALL,
            onSelected = onPeriodSelected
        )
    }
}

@Composable
private fun PeriodChip(
    period: PricePeriod,
    label: String,
    selected: Boolean,
    onSelected: (PricePeriod) -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = { onSelected(period) },
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = androidx.compose.material3.MaterialTheme.colorScheme.onPrimaryContainer,
            containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant,
            labelColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant,
            selectedBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.primary,
            borderWidth = 1.dp
        )
    )
}

@Preview(showBackground = true)
@Composable
private fun PeriodSelectorPreview() {
    IpfGoldTheme {
        PeriodSelector(
            selectedPeriod = PricePeriod.ALL,
            onPeriodSelected = {}
        )
    }
}