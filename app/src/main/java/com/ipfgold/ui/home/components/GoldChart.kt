package com.ipfgold.ui.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.ui.theme.BullishGreen
import com.ipfgold.ui.theme.GoldPrimary
import com.ipfgold.ui.theme.IpfGoldTheme
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shapeComponent
import com.patrykandpatrick.vico.compose.dimensions.dimensionsOf
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import com.patrykandpatrick.vico.core.chart.decoration.ThresholdLine
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.component.shape.LineComponent
import com.patrykandpatrick.vico.core.component.shape.ShapeComponent
import com.patrykandpatrick.vico.core.component.shape.Shapes
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Gráfica de línea del precio histórico del oro usando Vico.
 *
 * @param points Lista de puntos históricos ordenados por fecha ascendente.
 * @param currency Moneda de visualización (para tooltip).
 * @param modifier Modificador Compose.
 */
@Composable
fun GoldChart(
    points: List<ChartPoint>,
    currency: com.ipfgold.domain.model.Currency,
    modifier: Modifier = Modifier
) {
    if (points.isEmpty()) {
        // Placeholder para datos vacíos
        return
    }

    val modelProducer = remember { ChartEntryModelProducer() }
    val model = remember(points, currency) {
        // Convertir puntos a entradas de Vico (índice, precio)
        val entries = points.mapIndexed { index, point ->
            FloatEntry(
                x = index.toFloat(),
                y = (if (currency == com.ipfgold.domain.model.Currency.USD) point.priceUSD else point.priceEUR).toFloat()
            )
        }
        entryModelOf(entries)
    }

    // Formateador del eje X: muestra fechas cada N puntos para evitar saturación
    val xAxisValueFormatter = remember(points) {
        AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _, _ ->
            val index = value.toInt()
            if (index in points.indices) {
                formatDateShort(points[index].date)
            } else {
                ""
            }
        }
    }

    // Formateador del eje Y: precios con separadores de miles
    val yAxisValueFormatter = remember(currency) {
        AxisValueFormatter<AxisPosition.Vertical.Start> { value, _, _ ->
            val symbol = when (currency) {
                com.ipfgold.domain.model.Currency.USD -> "$"
                com.ipfgold.domain.model.Currency.EUR -> "€"
            }
            "$symbol${"%,.0f".format(value)}"
        }
    }

    // Componente de tooltip personalizado
    val marker = remember(currency) {
        shapeComponent(
            shape = Shapes.rectShape,
            color = androidx.compose.material3.MaterialTheme.colorScheme.surface,
            strokeColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
            strokeWidth = 1.dp
        )
    }

    Chart(
        chart = lineChart(
            lines = listOf(
                lineSpec(
                    lineColor = GoldPrimary,
                    lineBackgroundShader = null,
                    lineThickness = 3.dp,
                    pointSize = 0.dp // Sin puntos en la línea
                )
            ),
            axisValuesOverrider = AxisValuesOverrider.fixed(
                minY = points.minOf { if (currency == com.ipfgold.domain.model.Currency.USD) it.priceUSD else it.priceEUR } * 0.95f,
                maxY = points.maxOf { if (currency == com.ipfgold.domain.model.Currency.USD) it.priceUSD else it.priceEUR } * 1.05f
            ),
            decorations = listOf(
                // Línea de umbral en el último precio (opcional)
                ThresholdLine(
                    thresholdValue = points.last().let {
                        if (currency == com.ipfgold.domain.model.Currency.USD) it.priceUSD else it.priceEUR
                    }.toFloat(),
                    lineComponent = LineComponent(
                        color = BullishGreen.copy(alpha = 0.5f),
                        lineThickness = 1.dp,
                        shape = Shapes.rectShape
                    )
                )
            )
        ),
        modelProducer = modelProducer,
        model = model,
        marker = marker,
        startAxis = startAxis(
            tickLength = 0.dp,
            axis = null,
            valueFormatter = yAxisValueFormatter,
            guideline = null
        ),
        bottomAxis = bottomAxis(
            tickLength = 0.dp,
            axis = null,
            valueFormatter = xAxisValueFormatter,
            guideline = null
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}

/**
 * Formatea una fecha a formato corto (ej. "28/03").
 */
private fun formatDateShort(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("dd/MM")
    return date.format(formatter)
}

@Preview(showBackground = true)
@Composable
private fun GoldChartPreview() {
    val points = listOf(
        ChartPoint(
            date = LocalDate.of(2026, 3, 24),
            priceUSD = 4400.0,
            priceEUR = 3820.0
        ),
        ChartPoint(
            date = LocalDate.of(2026, 3, 25),
            priceUSD = 4450.0,
            priceEUR = 3865.0
        ),
        ChartPoint(
            date = LocalDate.of(2026, 3, 26),
            priceUSD = 4470.0,
            priceEUR = 3880.0
        ),
        ChartPoint(
            date = LocalDate.of(2026, 3, 27),
            priceUSD = 4495.0,
            priceEUR = 3902.0
        ),
        ChartPoint(
            date = LocalDate.of(2026, 3, 28),
            priceUSD = 4510.0,
            priceEUR = 3915.0
        )
    )
    IpfGoldTheme {
        GoldChart(
            points = points,
            currency = com.ipfgold.domain.model.Currency.USD
        )
    }
}