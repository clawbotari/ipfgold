package com.ipfgold.ui.home.components

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.domain.model.Currency
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberLineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.CartesianChartModelProducer

/**
 * Gráfico de línea para mostrar la evolución histórica del precio del oro.
 *
 * Usa Vico 1.13.0 con la API correcta.
 */
@Composable
fun GoldChart(points: List<ChartPoint>, currency: Currency) {
    val modelProducer = remember { CartesianChartModelProducer.build() }

    LaunchedEffect(points) {
        modelProducer.tryRunTransaction {
            lineSeries {
                series(points.map {
                    if (currency == Currency.EUR) it.priceEUR.toFloat()
                    else it.priceUSD.toFloat()
                })
            }
        }
    }

    CartesianChartHost(
        chart = rememberCartesianChart(
            rememberLineCartesianLayer()
        ),
        modelProducer = modelProducer,
        modifier = Modifier.fillMaxWidth().height(200.dp)
    )
}