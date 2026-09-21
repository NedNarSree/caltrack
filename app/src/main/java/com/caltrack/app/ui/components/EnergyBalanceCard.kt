package com.caltrack.app.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caltrack.app.ui.theme.CalTrackBorder
import com.caltrack.app.ui.theme.CalTrackGreen
import com.caltrack.app.ui.theme.CalTrackGreenBg
import com.caltrack.app.ui.theme.CalTrackGreenDark
import com.caltrack.app.ui.theme.CalTrackSurface
import com.caltrack.app.ui.theme.CalTrackTextPrimary
import com.caltrack.app.ui.theme.CalTrackTextSecondary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun EnergyBalanceCard(
    caloriesConsumed: Int
) {
    val numberFormatter = NumberFormat.getNumberInstance(Locale.US)
    val formattedCalories = numberFormatter.format(caloriesConsumed)

    // Visual ring sweep based on intake (scaled up to 2500 for a full loop visual)
    val progressRatio = (caloriesConsumed / 2500f).coerceIn(0.05f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progressRatio, label = "energyProgress")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CalTrackSurface)
            .border(1.dp, CalTrackBorder, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ENERGY BALANCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalTrackTextSecondary,
                    letterSpacing = 1.sp
                )
                Row(
                    verticalAlignment = Alignment.Bottom,
                    modifier = Modifier.padding(top = 6.dp)
                ) {
                    Text(
                        text = formattedCalories,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalTrackTextPrimary
                    )
                    Text(
                        text = " kcal consumed",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = CalTrackTextSecondary,
                        modifier = Modifier.padding(bottom = 5.dp, start = 4.dp)
                    )
                }
                Text(
                    text = "Total energy logged for selected date",
                    fontSize = 12.sp,
                    color = CalTrackGreenDark,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // Circular energy ring matching Stitch design
            Box(
                modifier = Modifier.size(76.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(76.dp)) {
                    val strokeWidth = 8.dp.toPx()
                    // Track background
                    drawCircle(
                        color = Color(0xFFE2E8F0),
                        style = Stroke(width = strokeWidth)
                    )
                    // Active energy sweep
                    drawArc(
                        color = CalTrackGreen,
                        startAngle = -90f,
                        sweepAngle = animatedProgress * 360f,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (caloriesConsumed > 0) "ACTIVE" else "0",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalTrackGreenDark
                    )
                    Text(
                        text = "Tracked",
                        fontSize = 9.sp,
                        color = CalTrackTextSecondary
                    )
                }
            }
        }
    }
}
