package com.caltrack.app.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caltrack.app.data.entity.WeightEntryEntity
import com.caltrack.app.ui.CalTrackViewModel
import com.caltrack.app.ui.theme.CalTrackBorder
import com.caltrack.app.ui.theme.CalTrackGreen
import com.caltrack.app.ui.theme.CalTrackGreenBg
import com.caltrack.app.ui.theme.CalTrackGreenDark
import com.caltrack.app.ui.theme.CalTrackSurface
import com.caltrack.app.ui.theme.CalTrackTextPrimary
import com.caltrack.app.ui.theme.CalTrackTextSecondary

@Composable
fun WeightChartCard(
    weights: List<WeightEntryEntity>,
    heightCm: Float,
    onLogWeightClick: () -> Unit
) {
    val currentWeight = weights.lastOrNull()?.weightKg ?: 74.2f
    val currentBmi = CalTrackViewModel.calculateBmi(currentWeight, heightCm)
    val (category, _) = CalTrackViewModel.getBmiCategory(currentBmi)

    // Calculate delta if more than 1 entry
    val weightDelta = if (weights.size >= 2) {
        val first = weights.first().weightKg
        val last = weights.last().weightKg
        last - first
    } else -0.4f

    val deltaText = if (weightDelta <= 0f) {
        "${String.format("%.1f", weightDelta)} kg overall"
    } else {
        "+${String.format("%.1f", weightDelta)} kg overall"
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CalTrackSurface)
            .border(1.dp, CalTrackBorder, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WEIGHT PROGRESS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalTrackTextSecondary,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(CalTrackGreenBg)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = deltaText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CalTrackGreenDark
                    )
                }
            }

            // Current weight label
            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier.padding(top = 6.dp)
            ) {
                Text(
                    text = "${String.format("%.1f", currentWeight)} kg",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalTrackTextPrimary
                )
                Text(
                    text = " current",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = CalTrackTextSecondary,
                    modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Line Chart
            val chartData = if (weights.isEmpty()) {
                listOf(75.4f, 75.0f, 74.6f, 74.2f)
            } else {
                weights.map { it.weightKg }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                ) {
                    val minVal = (chartData.minOrNull() ?: 70f) - 0.5f
                    val maxVal = (chartData.maxOrNull() ?: 76f) + 0.5f
                    val range = (maxVal - minVal).coerceAtLeast(1f)

                    val w = size.width
                    val h = size.height - 30f // reserve space for bottom labels

                    // Draw subtle grid line
                    drawLine(
                        color = Color(0xFFF1F5F9),
                        start = Offset(0f, h * 0.5f),
                        end = Offset(w, h * 0.5f),
                        strokeWidth = 2f
                    )

                    val stepX = if (chartData.size > 1) w / (chartData.size - 1) else w

                    val points = chartData.mapIndexed { index, weight ->
                        val x = index * stepX
                        val normalized = (weight - minVal) / range
                        val y = h - (normalized * h)
                        Offset(x, y)
                    }

                    // Draw gradient area under curve
                    val fillPath = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                            lineTo(points.last().x, h)
                            lineTo(points.first().x, h)
                            close()
                        }
                    }
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                CalTrackGreen.copy(alpha = 0.25f),
                                CalTrackGreen.copy(alpha = 0.02f)
                            ),
                            startY = 0f,
                            endY = h
                        )
                    )

                    // Draw trend line
                    val strokePath = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points.first().x, points.first().y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }
                    }
                    drawPath(
                        path = strokePath,
                        color = CalTrackGreen,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw dots at each entry
                    points.forEach { pt ->
                        drawCircle(
                            color = Color.White,
                            radius = 5.dp.toPx(),
                            center = pt
                        )
                        drawCircle(
                            color = CalTrackGreen,
                            radius = 3.5.dp.toPx(),
                            center = pt
                        )
                    }
                }
            }

            // Stats under chart
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Average BMI: $currentBmi",
                    fontSize = 11.sp,
                    color = CalTrackTextSecondary
                )
                Text(
                    text = category,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = CalTrackGreenDark
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // + Log Today's Weight Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(CalTrackGreen)
                    .clickable { onLogWeightClick() }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ Log Today's Weight",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
