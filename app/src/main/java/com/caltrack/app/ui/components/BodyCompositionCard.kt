package com.caltrack.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caltrack.app.ui.CalTrackViewModel
import com.caltrack.app.ui.theme.CalTrackBorder
import com.caltrack.app.ui.theme.CalTrackGreen
import com.caltrack.app.ui.theme.CalTrackGreenBg
import com.caltrack.app.ui.theme.CalTrackGreenDark
import com.caltrack.app.ui.theme.CalTrackSurface
import com.caltrack.app.ui.theme.CalTrackTextPrimary
import com.caltrack.app.ui.theme.CalTrackTextSecondary

@Composable
fun BodyCompositionCard(
    heightCm: Float,
    currentWeightKg: Float?,
    latestTimestampFormatted: String?,
    onUpdateClick: () -> Unit
) {
    val weight = currentWeightKg ?: 74.2f
    val bmi = CalTrackViewModel.calculateBmi(weight, heightCm)
    val (bmiCategory, _) = CalTrackViewModel.getBmiCategory(bmi)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CalTrackSurface)
            .border(1.dp, CalTrackBorder, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Body Composition",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalTrackTextPrimary
                    )
                    Text(
                        text = "Height: ${heightCm.toInt()} cm",
                        fontSize = 11.sp,
                        color = CalTrackTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // Update Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CalTrackGreenBg)
                        .clickable { onUpdateClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Update",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CalTrackGreenDark
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${String.format("%.1f", weight)} kg",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalTrackTextPrimary
                    )
                    Text(
                        text = latestTimestampFormatted ?: "Latest entry",
                        fontSize = 11.sp,
                        color = CalTrackTextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }

                // BMI status pill matching Stitch
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(CalTrackGreenBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
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
                            text = "BMI $bmi • $bmiCategory",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CalTrackGreenDark
                        )
                    }
                }
            }
        }
    }
}
