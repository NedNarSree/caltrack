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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caltrack.app.data.entity.MealEntity
import com.caltrack.app.ui.theme.CalTrackBorderSubtle
import com.caltrack.app.ui.theme.CalTrackGreen
import com.caltrack.app.ui.theme.CalTrackGreenBg
import com.caltrack.app.ui.theme.CalTrackSurface
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.AutoAwesome
import coil.compose.AsyncImage
import com.caltrack.app.ui.theme.CalTrackTextPrimary
import com.caltrack.app.ui.theme.CalTrackTextSecondary
import com.caltrack.app.ui.theme.CalTrackTextTertiary

@Composable
fun MealItemRow(
    meal: MealEntity,
    onDeleteClick: (MealEntity) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CalTrackSurface)
            .border(1.dp, CalTrackBorderSubtle, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Food Thumbnail / Avatar (Photo or Fallback Icon)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(CalTrackGreenBg),
                contentAlignment = Alignment.Center
            ) {
                if (!meal.imageUri.isNullOrBlank()) {
                    AsyncImage(
                        model = meal.imageUri,
                        contentDescription = meal.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Outlined.Restaurant,
                        contentDescription = null,
                        tint = CalTrackGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${meal.mealType.uppercase()} • ${meal.timeFormatted}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalTrackGreen,
                        letterSpacing = 0.5.sp
                    )

                    if (meal.aiConfidence != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFEDE9FE))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Scanned",
                                tint = Color(0xFF7C3AED),
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${(meal.aiConfidence * 100).toInt()}%",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF7C3AED)
                            )
                        }
                    }
                }

                Text(
                    text = meal.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CalTrackTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = "${meal.proteinGrams}g P • ${meal.carbsGrams}g C • ${meal.fatGrams}g F",
                    fontSize = 11.sp,
                    color = CalTrackTextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${meal.calories}",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalTrackTextPrimary
                )
                Text(
                    text = "kcal",
                    fontSize = 11.sp,
                    color = CalTrackTextSecondary
                )
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete Meal",
                    tint = CalTrackTextTertiary,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(18.dp)
                        .clickable { onDeleteClick(meal) }
                )
            }
        }
    }
}
