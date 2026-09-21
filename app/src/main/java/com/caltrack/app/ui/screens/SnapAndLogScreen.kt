package com.caltrack.app.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caltrack.app.ui.CalTrackViewModel
import com.caltrack.app.ui.components.CalTrackTopBar
import com.caltrack.app.ui.theme.CalTrackBg
import com.caltrack.app.ui.theme.CalTrackBorder
import com.caltrack.app.ui.theme.CalTrackGreen
import com.caltrack.app.ui.theme.CalTrackGreenBg
import com.caltrack.app.ui.theme.CalTrackGreenDark
import com.caltrack.app.ui.theme.CalTrackSurface
import com.caltrack.app.ui.theme.CalTrackTextPrimary
import com.caltrack.app.ui.theme.CalTrackTextSecondary
import com.caltrack.app.ui.theme.MacroCarbsAmber
import com.caltrack.app.ui.theme.MacroFatRed
import com.caltrack.app.ui.theme.MacroProteinBlue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SnapAndLogScreen(
    viewModel: CalTrackViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    // Dish presets simulating Gemini 2.0 Vision analysis
    val presets = listOf(
        Triple("Atlantic Salmon Bowl", 520, Triple(42, 38, 18)),
        Triple("Grilled Chicken Quinoa Salad", 650, Triple(52, 45, 14)),
        Triple("Avocado Toast & Poached Eggs", 440, Triple(22, 34, 20)),
        Triple("Lean Beef Steak & Sweet Potato", 680, Triple(55, 42, 22))
    )

    var currentPresetIndex by remember { mutableStateOf(0) }
    var mealDescription by remember { mutableStateOf("Grilled Salmon & Sweet Potato Bowl") }
    var mealSlot by remember { mutableStateOf("Dinner") }
    var showSlotDropdown by remember { mutableStateOf(false) }
    var calories by remember { mutableStateOf(520) }
    var proteinGrams by remember { mutableStateOf(42) }
    var carbsGrams by remember { mutableStateOf(38) }
    var fatGrams by remember { mutableStateOf(18) }
    var logTime by remember {
        mutableStateOf(
            "Today, " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        )
    }

    val mealSlots = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    // Recalculate macro percentage breakdown
    val totalMacroGrams = (proteinGrams + carbsGrams + fatGrams).coerceAtLeast(1)
    val pPct = ((proteinGrams.toFloat() / totalMacroGrams) * 100).toInt()
    val cPct = ((carbsGrams.toFloat() / totalMacroGrams) * 100).toInt()
    val fPct = (100 - pPct - cPct).coerceAtLeast(0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CalTrackBg)
    ) {
        CalTrackTopBar(
            screenTitle = "Snap And Log",
            onProfileClick = onNavigateToProfile
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Gemini 2.0 Confidence Banner
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color(0xFF8B5CF6),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "GEMINI 2.0 FLASH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5CF6),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(CalTrackGreenBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "95.6% Confidence",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = CalTrackGreenDark
                        )
                    }
                }

                Text(
                    text = presets[currentPresetIndex].first,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalTrackTextPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = "AI Vision analysis estimated macros. Please review and calibrate below:",
                    fontSize = 12.sp,
                    color = CalTrackTextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Captured & Analyzed Image Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(CalTrackSurface)
                        .border(1.dp, CalTrackBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        // Simulated Dish Visual Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Color(0xFFF1F5F9)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = CalTrackGreen,
                                    modifier = Modifier.size(54.dp)
                                )
                                Text(
                                    text = presets[currentPresetIndex].first,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CalTrackTextPrimary,
                                    modifier = Modifier.padding(top = 6.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .padding(top = 6.dp)
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.Black.copy(alpha = 0.65f))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Captured & Analyzed • 180g",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Retake and Gallery actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CalTrackGreenBg)
                                    .clickable {
                                        // Switch to next preset simulation
                                        currentPresetIndex = (currentPresetIndex + 1) % presets.size
                                        val p = presets[currentPresetIndex]
                                        mealDescription = p.first
                                        calories = p.second
                                        proteinGrams = p.third.first
                                        carbsGrams = p.third.second
                                        fatGrams = p.third.third
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = CalTrackGreenDark,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Retake",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CalTrackGreenDark
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .clickable {
                                        // Switch to next preset simulation
                                        currentPresetIndex = (currentPresetIndex + 1) % presets.size
                                        val p = presets[currentPresetIndex]
                                        mealDescription = p.first
                                        calories = p.second
                                        proteinGrams = p.third.first
                                        carbsGrams = p.third.second
                                        fatGrams = p.third.third
                                    }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = null,
                                        tint = CalTrackTextSecondary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Gallery",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CalTrackTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Meal Description Input
            item {
                Column {
                    Text(
                        text = "Meal Description",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CalTrackTextSecondary,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    OutlinedTextField(
                        value = mealDescription,
                        onValueChange = { mealDescription = it },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                tint = CalTrackTextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Meal Slot Dropdown
                        Box(modifier = Modifier.weight(1f)) {
                            Column {
                                Text(
                                    text = "Meal Slot",
                                    fontSize = 11.sp,
                                    color = CalTrackTextSecondary
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, CalTrackBorder, RoundedCornerShape(8.dp))
                                        .clickable { showSlotDropdown = true }
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = mealSlot, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                        Icon(
                                            imageVector = Icons.Default.KeyboardArrowDown,
                                            contentDescription = null,
                                            tint = CalTrackTextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = showSlotDropdown,
                                    onDismissRequest = { showSlotDropdown = false }
                                ) {
                                    mealSlots.forEach { slot ->
                                        DropdownMenuItem(
                                            text = { Text(slot) },
                                            onClick = {
                                                mealSlot = slot
                                                showSlotDropdown = false
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        // Log Time Field
                        Box(modifier = Modifier.weight(1f)) {
                            Column {
                                Text(
                                    text = "Log Time",
                                    fontSize = 11.sp,
                                    color = CalTrackTextSecondary
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 2.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .border(1.dp, CalTrackBorder, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp, vertical = 10.dp)
                                ) {
                                    Text(
                                        text = logTime,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = CalTrackTextPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Total Energy Stepper Card matching Stitch
            item {
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
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(CalTrackGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Total Energy",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CalTrackTextPrimary
                                )
                            }
                            Text(
                                text = "Gemini Calculated",
                                fontSize = 11.sp,
                                color = CalTrackGreenDark,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Large Calorie Stepper
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                                    .clickable { if (calories > 20) calories -= 10 },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease")
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "$calories",
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CalTrackTextPrimary
                                )
                                Text(
                                    text = "kcal",
                                    fontSize = 12.sp,
                                    color = CalTrackTextSecondary
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFF1F5F9))
                                    .clickable { calories += 10 },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Increase")
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Macro balance label & segmented bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Macro Balance", fontSize = 11.sp, color = CalTrackTextSecondary)
                            Text(
                                text = "$pPct% P • $cPct% C • $fPct% F",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = CalTrackTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        ) {
                            Box(modifier = Modifier.weight(pPct.toFloat().coerceAtLeast(1f)).height(6.dp).background(MacroProteinBlue))
                            Box(modifier = Modifier.weight(cPct.toFloat().coerceAtLeast(1f)).height(6.dp).background(MacroCarbsAmber))
                            Box(modifier = Modifier.weight(fPct.toFloat().coerceAtLeast(1f)).height(6.dp).background(MacroFatRed))
                        }
                    }
                }
            }

            // Macro Adjustment Cards (Protein, Carbs, Fats)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MacroEditCard(
                        title = "Protein",
                        grams = proteinGrams,
                        calories = proteinGrams * 4,
                        color = MacroProteinBlue,
                        modifier = Modifier.weight(1f),
                        onIncrement = { proteinGrams += 1 },
                        onDecrement = { if (proteinGrams > 0) proteinGrams -= 1 }
                    )
                    MacroEditCard(
                        title = "Carbs",
                        grams = carbsGrams,
                        calories = carbsGrams * 4,
                        color = MacroCarbsAmber,
                        modifier = Modifier.weight(1f),
                        onIncrement = { carbsGrams += 1 },
                        onDecrement = { if (carbsGrams > 0) carbsGrams -= 1 }
                    )
                    MacroEditCard(
                        title = "Fats",
                        grams = fatGrams,
                        calories = fatGrams * 9,
                        color = MacroFatRed,
                        modifier = Modifier.weight(1f),
                        onIncrement = { fatGrams += 1 },
                        onDecrement = { if (fatGrams > 0) fatGrams -= 1 }
                    )
                }
            }

            // SQLite Encryption & Offline Notice
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CalTrackTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Offline & Encrypted Storage. Data will be saved locally to SQLite table 'food_entries'",
                        fontSize = 10.sp,
                        color = CalTrackTextSecondary
                    )
                }
            }

            // Action Buttons
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.addMeal(
                                title = mealDescription,
                                mealType = mealSlot,
                                calories = calories,
                                protein = proteinGrams,
                                carbs = carbsGrams,
                                fat = fatGrams,
                                aiConfidence = 0.956f,
                                onSuccess = onNavigateBack
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CalTrackGreen),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save to Local Log",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    TextButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Cancel",
                            fontSize = 14.sp,
                            color = CalTrackTextSecondary
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MacroEditCard(
    title: String,
    grams: Int,
    calories: Int,
    color: Color,
    modifier: Modifier = Modifier,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CalTrackSurface)
            .border(1.dp, CalTrackBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = CalTrackTextPrimary)
            }

            Text(
                text = "${grams}g",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = CalTrackTextPrimary,
                modifier = Modifier.padding(top = 4.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                        .clickable { onDecrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(12.dp))
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                        .clickable { onIncrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(12.dp))
                }
            }

            Text(
                text = "$calories kcal",
                fontSize = 10.sp,
                color = CalTrackTextSecondary,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}
