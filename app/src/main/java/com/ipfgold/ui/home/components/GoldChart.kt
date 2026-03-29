package com.ipfgold.ui.home.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.domain.model.Currency
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries

@Composable
fun GoldChart(
 points: List<ChartPoint>,
 currency: Currency,
 modifier: Modifier = Modifier
) {
 val modelProducer = remember { CartesianChartModelProducer.build() }

 LaunchedEffect(points, currency) {
 modelProducer.tryRunTransaction {
 lineSeries {
 series(
 points.map {
 if (currency == Currency.EUR) it.priceEUR.toFloat()
 else it.priceUSD.toFloat()
 }
 )
 }
 }
 }

 CartesianChartHost(
 chart = rememberCartesianChart(
 rememberLineCartesianLayer()
 ),
 modelProducer = modelProducer,
 modifier = modifier.fillMaxWidth().height(200.dp)
 )
}