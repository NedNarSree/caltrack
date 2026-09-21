package com.caltrack.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.caltrack.app.ui.CalTrackViewModel
import com.caltrack.app.ui.theme.CalTrackGreen
import com.caltrack.app.ui.theme.CalTrackGreenDark
import com.caltrack.app.ui.theme.CalTrackTextPrimary
import com.caltrack.app.ui.theme.CalTrackTextSecondary

@Composable
fun LogWeightDialog(
    heightCm: Float,
    currentWeightKg: Float?,
    onDismiss: () -> Unit,
    onSaveWeight: (Float) -> Unit
) {
    var weightText by remember {
        mutableStateOf(currentWeightKg?.toString() ?: "74.2")
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val enteredWeight = weightText.toFloatOrNull()
    val previewBmi = if (enteredWeight != null && heightCm > 0f) {
        CalTrackViewModel.calculateBmi(enteredWeight, heightCm)
    } else null

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Log Body Weight",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = CalTrackTextPrimary
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Track your weight locally. Metric units (kg) only.",
                    fontSize = 12.sp,
                    color = CalTrackTextSecondary
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = weightText,
                    onValueChange = {
                        weightText = it
                        errorMessage = null
                    },
                    label = { Text("Weight (kg)") },
                    placeholder = { Text("e.g. 74.2") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (previewBmi != null) {
                    val (category, _) = CalTrackViewModel.getBmiCategory(previewBmi)
                    Text(
                        text = "Calculated BMI: $previewBmi • $category",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CalTrackGreenDark,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightText.toFloatOrNull()
                    if (w == null || w <= 20f || w >= 350f) {
                        errorMessage = "Please enter a valid weight (20 - 350 kg)"
                        return@Button
                    }
                    onSaveWeight(w)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CalTrackGreen)
            ) {
                Text("Save Weight", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = CalTrackTextSecondary)
            }
        }
    )
}
