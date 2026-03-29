package com.ipfgold.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.ipfgold.domain.model.ChartPoint
import com.ipfgold.domain.model.Currency

@Composable
fun GoldChart(
 points: List<ChartPoint>,
 currency: Currency,
 modifier: Modifier = Modifier
) {
 if (points.isEmpty()) return

 Canvas(modifier = modifier.fillMaxWidth().height(200.dp)) {
 val values = points.map {
 if (currency == Currency.EUR) it.priceEUR.toFloat()
 else it.priceUSD.toFloat()
 }
 val minVal = values.min()
 val maxVal = values.max()
 val range = if (maxVal - minVal == 0f) 1f else maxVal - minVal

 val path = Path()
 values.forEachIndexed { index, value ->
 val x = index / (values.size - 1).toFloat() * size.width
 val y = size.height - ((value - minVal) / range * size.height)
 if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
 }
 drawPath(path, color = Color(0xFFD4A017), style = Stroke(width = 3f))
 }
}