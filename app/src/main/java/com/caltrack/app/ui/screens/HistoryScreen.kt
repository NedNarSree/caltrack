package com.caltrack.app.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.caltrack.app.ui.CalTrackViewModel
import com.caltrack.app.ui.DailyHistoryItem
import com.caltrack.app.ui.components.CalTrackTopBar
import com.caltrack.app.ui.components.LogWeightDialog
import com.caltrack.app.ui.components.WeightChartCard
import com.caltrack.app.ui.theme.CalTrackBg
import com.caltrack.app.ui.theme.CalTrackBorderSubtle
import com.caltrack.app.ui.theme.CalTrackGreen
import com.caltrack.app.ui.theme.CalTrackSurface
import com.caltrack.app.ui.theme.CalTrackTextPrimary
import com.caltrack.app.ui.theme.CalTrackTextSecondary
import com.caltrack.app.ui.theme.CalTrackTextTertiary
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: CalTrackViewModel,
    onNavigateToProfile: () -> Unit
) {
    val weightsAsc by viewModel.allWeightsAsc.collectAsState()
    val historyItems by viewModel.historyItems.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val totalEntries by viewModel.totalDatabaseEntries.collectAsState()

    var showLogWeightDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalTrackBg)
    ) {
        CalTrackTopBar(
            screenTitle = "History",
            onProfileClick = onNavigateToProfile
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Weight Trend Chart Card
            item {
                WeightChartCard(
                    weights = weightsAsc,
                    heightCm = profile.heightCm,
                    onLogWeightClick = { showLogWeightDialog = true }
                )
            }

            // Daily Logged History Section Title (single continuous list per reduced scope)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Daily Logged History",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalTrackTextPrimary
                    )
                    Text(
                        text = "Newest first",
                        fontSize = 11.sp,
                        color = CalTrackTextSecondary
                    )
                }
            }

            // History Items List
            if (historyItems.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No history recorded yet.\nLog meals or weight to build your history.",
                            fontSize = 13.sp,
                            color = CalTrackTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(historyItems, key = { it.dateString }) { item ->
                    DailyHistoryCard(item = item)
                }
            }

            // Database summary footer matching Stitch
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = CalTrackTextTertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text(
                        text = "Stored on device • SQLite database: $totalEntries entries",
                        fontSize = 11.sp,
                        color = CalTrackTextSecondary
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showLogWeightDialog) {
        val latestWeight = weightsAsc.lastOrNull()?.weightKg
        LogWeightDialog(
            heightCm = profile.heightCm,
            currentWeightKg = latestWeight,
            onDismiss = { showLogWeightDialog = false },
            onSaveWeight = { newWeight ->
                viewModel.logWeight(
                    weightKg = newWeight,
                    onSuccess = { showLogWeightDialog = false }
                )
            }
        )
    }
}

@Composable
private fun DailyHistoryCard(item: DailyHistoryItem) {
    var expanded by remember { mutableStateOf(false) }
    val numberFormatter = NumberFormat.getNumberInstance(Locale.US)
    val formattedCals = numberFormatter.format(item.totalCalories)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CalTrackSurface)
            .border(1.dp, CalTrackBorderSubtle, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.displayDate,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalTrackTextPrimary
                    )
                    if (item.weightKg != null && item.bmi != null) {
                        Text(
                            text = "${item.weightKg} kg • BMI ${item.bmi}",
                            fontSize = 11.sp,
                            color = CalTrackTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = formattedCals,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = CalTrackGreen
                        )
                        Text(
                            text = " kcal",
                            fontSize = 11.sp,
                            color = CalTrackTextSecondary,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable { expanded = !expanded }
                            .padding(top = 2.dp)
                    ) {
                        Text(
                            text = "${item.meals.size} meals",
                            fontSize = 11.sp,
                            color = CalTrackTextSecondary
                        )
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand",
                            tint = CalTrackTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Macro summary line
            Text(
                text = "P: ${item.totalProtein}g • C: ${item.totalCarbs}g • F: ${item.totalFat}g",
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = CalTrackTextSecondary
            )

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 10.dp)) {
                    HorizontalDivider(color = CalTrackBorderSubtle, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))
                    item.meals.forEach { meal ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (!meal.imageUri.isNullOrBlank()) {
                                    AsyncImage(
                                        model = meal.imageUri,
                                        contentDescription = meal.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                Column {
                                    Text(
                                        text = meal.title,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CalTrackTextPrimary
                                    )
                                    Text(
                                        text = "${meal.mealType} • ${meal.proteinGrams}g P • ${meal.carbsGrams}g C • ${meal.fatGrams}g F",
                                        fontSize = 10.sp,
                                        color = CalTrackTextSecondary
                                    )
                                }
                            }
                            Text(
                                text = "${meal.calories} kcal",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CalTrackTextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}
