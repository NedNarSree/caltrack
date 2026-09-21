package com.caltrack.app.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import com.caltrack.app.ui.CalTrackViewModel
import com.caltrack.app.ui.components.BodyCompositionCard
import com.caltrack.app.ui.components.CalTrackTopBar
import com.caltrack.app.ui.components.EnergyBalanceCard
import com.caltrack.app.ui.components.LogWeightDialog
import com.caltrack.app.ui.components.MacroDistributionBar
import com.caltrack.app.ui.components.MealItemRow
import com.caltrack.app.ui.components.QuickAddMealDialog
import com.caltrack.app.ui.components.SnapAiBanner
import com.caltrack.app.ui.theme.CalTrackBg
import com.caltrack.app.ui.theme.CalTrackGreen
import com.caltrack.app.ui.theme.CalTrackGreenBg
import com.caltrack.app.ui.theme.CalTrackGreenDark
import com.caltrack.app.ui.theme.CalTrackTextPrimary
import com.caltrack.app.ui.theme.CalTrackTextSecondary

@Composable
fun TodayScreen(
    viewModel: CalTrackViewModel,
    onNavigateToSnapLog: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val meals by viewModel.selectedDateMeals.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val latestWeight by viewModel.latestWeight.collectAsState()

    var showQuickAddDialog by remember { mutableStateOf(false) }
    var showLogWeightDialog by remember { mutableStateOf(false) }

    val totalCalories = meals.sumOf { it.calories }
    val totalProtein = meals.sumOf { it.proteinGrams }
    val totalCarbs = meals.sumOf { it.carbsGrams }
    val totalFat = meals.sumOf { it.fatGrams }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalTrackBg)
    ) {
        CalTrackTopBar(
            screenTitle = "Today",
            onProfileClick = onNavigateToProfile
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Date Switcher Header matching Stitch
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.ChevronLeft,
                                contentDescription = "Previous Day",
                                tint = CalTrackTextSecondary,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { viewModel.previousDay() }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = viewModel.getSelectedDateLabel(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = CalTrackTextPrimary,
                                modifier = Modifier.clickable { viewModel.resetToToday() }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "Next Day",
                                tint = CalTrackTextSecondary,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clickable { viewModel.nextDay() }
                            )
                        }
                        Text(
                            text = viewModel.getSelectedDateSubLabel(),
                            fontSize = 11.sp,
                            color = CalTrackTextSecondary,
                            modifier = Modifier.padding(start = 28.dp, top = 2.dp)
                        )
                    }

                    // SQLite offline pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CalTrackGreenBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(CalTrackGreen)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Offline SQLite",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CalTrackGreenDark
                            )
                        }
                    }
                }
            }

            // Energy Hero Card
            item {
                EnergyBalanceCard(
                    caloriesConsumed = totalCalories
                )
            }

            // Macro Breakdown Card
            item {
                MacroDistributionBar(
                    proteinGrams = totalProtein,
                    carbsGrams = totalCarbs,
                    fatGrams = totalFat,
                    totalCalories = totalCalories
                )
            }

            // Body Composition Card
            item {
                BodyCompositionCard(
                    heightCm = profile.heightCm,
                    currentWeightKg = latestWeight?.weightKg,
                    latestTimestampFormatted = latestWeight?.timeFormatted,
                    onUpdateClick = { showLogWeightDialog = true }
                )
            }

            // Snap & Log with AI Banner
            item {
                SnapAiBanner(
                    onTakePhotoClick = onNavigateToSnapLog,
                    onUploadClick = onNavigateToSnapLog
                )
            }

            // Logged Meals Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Logged Meals",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CalTrackTextPrimary
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CalTrackGreenBg)
                            .clickable { showQuickAddDialog = true }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = "Quick Add +",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CalTrackGreenDark
                        )
                    }
                }
            }

            // Meals List
            if (meals.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No meals logged for this day.\nTap 'Quick Add +' or 'Snap & Log' to start.",
                            fontSize = 13.sp,
                            color = CalTrackTextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                items(meals, key = { it.id }) { meal ->
                    MealItemRow(
                        meal = meal,
                        onDeleteClick = { viewModel.deleteMeal(it) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showQuickAddDialog) {
        QuickAddMealDialog(
            onDismiss = { showQuickAddDialog = false },
            onSaveMeal = { title, mealType, cals, p, c, f ->
                viewModel.addMeal(
                    title = title,
                    mealType = mealType,
                    calories = cals,
                    protein = p,
                    carbs = c,
                    fat = f,
                    onSuccess = { showQuickAddDialog = false }
                )
            }
        )
    }

    if (showLogWeightDialog) {
        LogWeightDialog(
            heightCm = profile.heightCm,
            currentWeightKg = latestWeight?.weightKg,
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
