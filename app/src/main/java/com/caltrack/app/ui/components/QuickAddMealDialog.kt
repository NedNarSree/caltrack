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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.caltrack.app.ui.theme.CalTrackBorder
import com.caltrack.app.ui.theme.CalTrackGreen
import com.caltrack.app.ui.theme.CalTrackGreenBg
import com.caltrack.app.ui.theme.CalTrackGreenDark
import com.caltrack.app.ui.theme.CalTrackTextPrimary
import com.caltrack.app.ui.theme.CalTrackTextSecondary

@Composable
fun QuickAddMealDialog(
    onDismiss: () -> Unit,
    onSaveMeal: (String, String, Int, Int, Int, Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedMealType by remember { mutableStateOf("Breakfast") }
    var caloriesText by remember { mutableStateOf("") }
    var proteinText by remember { mutableStateOf("0") }
    var carbsText by remember { mutableStateOf("0") }
    var fatText by remember { mutableStateOf("0") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val mealTypes = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Quick Add Food",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = CalTrackTextPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Meal Type selector chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    mealTypes.forEach { type ->
                        val isSelected = selectedMealType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) CalTrackGreen else CalTrackGreenBg)
                                .clickable { selectedMealType = type }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = type,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) Color.White else CalTrackGreenDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        errorMessage = null
                    },
                    label = { Text("Food Description") },
                    placeholder = { Text("e.g. Scrambled eggs on sourdough") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = caloriesText,
                    onValueChange = {
                        caloriesText = it.filter { char -> char.isDigit() }
                        errorMessage = null
                    },
                    label = { Text("Total Calories (kcal)") },
                    placeholder = { Text("e.g. 420") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Macronutrients (Optional):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CalTrackTextSecondary
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = proteinText,
                        onValueChange = { proteinText = it.filter { char -> char.isDigit() } },
                        label = { Text("Protein (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = carbsText,
                        onValueChange = { carbsText = it.filter { char -> char.isDigit() } },
                        label = { Text("Carbs (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = fatText,
                        onValueChange = { fatText = it.filter { char -> char.isDigit() } },
                        label = { Text("Fat (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        errorMessage = "Please enter food name"
                        return@Button
                    }
                    val cals = caloriesText.toIntOrNull()
                    if (cals == null || cals <= 0) {
                        errorMessage = "Please enter valid calories (> 0)"
                        return@Button
                    }
                    val p = proteinText.toIntOrNull() ?: 0
                    val c = carbsText.toIntOrNull() ?: 0
                    val f = fatText.toIntOrNull() ?: 0

                    onSaveMeal(title, selectedMealType, cals, p, c, f)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CalTrackGreen)
            ) {
                Text("Add Food", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CalTrackTextSecondary)
            }
        }
    )
}
