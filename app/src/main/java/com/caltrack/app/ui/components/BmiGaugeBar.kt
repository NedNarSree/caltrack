package com.caltrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caltrack.app.ui.CalTrackViewModel
import com.caltrack.app.ui.theme.BmiNormal
import com.caltrack.app.ui.theme.BmiObese
import com.caltrack.app.ui.theme.BmiOverweight
import com.caltrack.app.ui.theme.BmiUnderweight
import com.caltrack.app.ui.theme.CalTrackBorder
import com.caltrack.app.ui.theme.CalTrackGreen
import com.caltrack.app.ui.theme.CalTrackGreenBg
import com.caltrack.app.ui.theme.CalTrackGreenDark
import com.caltrack.app.ui.theme.CalTrackSurface
import com.caltrack.app.ui.theme.CalTrackTextPrimary
import com.caltrack.app.ui.theme.CalTrackTextSecondary

@Composable
fun BmiGaugeCard(
    heightCm: Float,
    currentWeightKg: Float?
) {
    val weight = currentWeightKg ?: 74.2f
    val bmi = CalTrackViewModel.calculateBmi(weight, heightCm)
    val (category, range) = CalTrackViewModel.getBmiCategory(bmi)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CalTrackSurface)
            .border(1.dp, CalTrackBorder, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Column {
            Text(
                text = "Profile & Health Metrics",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = CalTrackTextPrimary
            )
            Text(
                text = "Used to compute your BMI and calibrate personal tracking. Stored 100% locally.",
                fontSize = 12.sp,
                color = CalTrackTextSecondary,
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            // Current Indicator Header
            Text(
                text = "CURRENT INDICATOR",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = CalTrackTextSecondary,
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = "$bmi",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalTrackTextPrimary
                    )
                    Text(
                        text = " BMI",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = CalTrackTextSecondary,
                        modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                    )
                }

                // Category status pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CalTrackGreenBg)
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(CalTrackGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$category ($range)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CalTrackGreenDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-segment BMI Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                ) {
                    Box(modifier = Modifier.weight(3.5f).height(10.dp).background(BmiUnderweight))
                    Box(modifier = Modifier.weight(6.5f).height(10.dp).background(BmiNormal))
                    Box(modifier = Modifier.weight(5f).height(10.dp).background(BmiOverweight))
                    Box(modifier = Modifier.weight(5f).height(10.dp).background(BmiObese))
                }

                // Scale ticks & labels
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "18.5", fontSize = 10.sp, color = CalTrackTextSecondary)
                    Text(text = "25.0", fontSize = 10.sp, color = CalTrackTextSecondary)
                    Text(text = "30.0", fontSize = 10.sp, color = CalTrackTextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Formula explanation box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF8FAFC))
                    .padding(12.dp)
            ) {
                Column {
                    Text(
                        text = "BMI = weight (kg) / [height (m)]²",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CalTrackTextSecondary
                    )
                    Text(
                        text = "Calculated from: Height ${heightCm.toInt()} cm • Latest Weight ${String.format("%.1f", weight)} kg",
                        fontSize = 11.sp,
                        color = CalTrackTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}
