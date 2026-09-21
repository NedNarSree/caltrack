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
import com.caltrack.app.ui.theme.CalTrackBorder
import com.caltrack.app.ui.theme.CalTrackSurface
import com.caltrack.app.ui.theme.CalTrackTextPrimary
import com.caltrack.app.ui.theme.CalTrackTextSecondary
import com.caltrack.app.ui.theme.MacroCarbsAmber
import com.caltrack.app.ui.theme.MacroFatRed
import com.caltrack.app.ui.theme.MacroProteinBlue

@Composable
fun MacroDistributionBar(
    proteinGrams: Int,
    carbsGrams: Int,
    fatGrams: Int,
    totalCalories: Int
) {
    val totalGrams = (proteinGrams + carbsGrams + fatGrams).coerceAtLeast(1)
    val proteinPercent = ((proteinGrams.toFloat() / totalGrams) * 100).toInt()
    val carbsPercent = ((carbsGrams.toFloat() / totalGrams) * 100).toInt()
    val fatPercent = (100 - proteinPercent - carbsPercent).coerceAtLeast(0)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CalTrackSurface)
            .border(1.dp, CalTrackBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Macro distribution",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CalTrackTextPrimary
                )
                Text(
                    text = "$totalCalories kcal",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalTrackTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Multi-color segmented ratio bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                if (proteinGrams == 0 && carbsGrams == 0 && fatGrams == 0) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .background(Color(0xFFE2E8F0))
                    )
                } else {
                    if (proteinPercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(proteinPercent.toFloat().coerceAtLeast(1f))
                                .height(8.dp)
                                .background(MacroProteinBlue)
                        )
                    }
                    if (carbsPercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(carbsPercent.toFloat().coerceAtLeast(1f))
                                .height(8.dp)
                                .background(MacroCarbsAmber)
                        )
                    }
                    if (fatPercent > 0) {
                        Box(
                            modifier = Modifier
                                .weight(fatPercent.toFloat().coerceAtLeast(1f))
                                .height(8.dp)
                                .background(MacroFatRed)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Three Macro Columns matching Stitch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MacroStatColumn(
                    title = "Protein",
                    grams = proteinGrams,
                    percent = proteinPercent,
                    color = MacroProteinBlue
                )
                MacroStatColumn(
                    title = "Carbs",
                    grams = carbsGrams,
                    percent = carbsPercent,
                    color = MacroCarbsAmber
                )
                MacroStatColumn(
                    title = "Fat",
                    grams = fatGrams,
                    percent = fatPercent,
                    color = MacroFatRed
                )
            }
        }
    }
}

@Composable
private fun MacroStatColumn(
    title: String,
    grams: Int,
    percent: Int,
    color: Color
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = CalTrackTextSecondary
            )
        }
        Text(
            text = "${grams}g",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = CalTrackTextPrimary,
            modifier = Modifier.padding(top = 2.dp)
        )
        Text(
            text = "$percent% ratio",
            fontSize = 11.sp,
            color = CalTrackTextSecondary
        )
    }
}
