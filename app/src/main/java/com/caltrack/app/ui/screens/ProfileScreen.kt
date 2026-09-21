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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.caltrack.app.ui.components.BmiGaugeCard
import com.caltrack.app.ui.components.CalTrackTopBar
import com.caltrack.app.ui.theme.CalTrackBg
import com.caltrack.app.ui.theme.CalTrackBorder
import com.caltrack.app.ui.theme.CalTrackGreen
import com.caltrack.app.ui.theme.CalTrackGreenBg
import com.caltrack.app.ui.theme.CalTrackGreenDark
import com.caltrack.app.ui.theme.CalTrackSurface
import com.caltrack.app.ui.theme.CalTrackTextPrimary
import com.caltrack.app.ui.theme.CalTrackTextSecondary
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    viewModel: CalTrackViewModel
) {
    val profile by viewModel.userProfile.collectAsState()
    val latestWeight by viewModel.latestWeight.collectAsState()

    var heightText by remember(profile.heightCm) {
        mutableStateOf(profile.heightCm.toInt().toString())
    }
    var ageText by remember(profile.age) {
        mutableStateOf(profile.age?.toString() ?: "")
    }
    var selectedSex by remember(profile.biologicalSex) {
        mutableStateOf(profile.biologicalSex)
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val sexOptions = listOf("Male", "Female", "Prefer not")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CalTrackBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            CalTrackTopBar(screenTitle = "Profile")

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // BMI Gauge Card
                item {
                    BmiGaugeCard(
                        heightCm = profile.heightCm,
                        currentWeightKg = latestWeight?.weightKg
                    )
                }

                // Physical Attributes Form Card
                item {
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
                                Text(
                                    text = "Physical Attributes",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CalTrackTextPrimary
                                )
                                // Metric label indicator (metric only per reduced scope)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CalTrackGreenBg)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Metric (cm, kg)",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CalTrackGreenDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Height Input
                            Text(
                                text = "Height * (Required for BMI)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = CalTrackTextSecondary
                            )
                            OutlinedTextField(
                                value = heightText,
                                onValueChange = {
                                    heightText = it.filter { char -> char.isDigit() }
                                    errorMessage = null
                                },
                                suffix = { Text("cm") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 10.dp)
                            )

                            // Latest Weight Display / Input
                            Text(
                                text = "Latest Weight (Auto-syncs from weight entries)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = CalTrackTextSecondary
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 10.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFFF8FAFC))
                                    .border(1.dp, CalTrackBorder, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 14.dp, vertical = 12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${latestWeight?.weightKg ?: 74.2f} kg",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CalTrackTextPrimary
                                    )
                                    Text(
                                        text = latestWeight?.timeFormatted ?: "Today",
                                        fontSize = 11.sp,
                                        color = CalTrackTextSecondary
                                    )
                                }
                            }

                            // Age Input
                            Text(
                                text = "Age (Optional)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = CalTrackTextSecondary
                            )
                            OutlinedTextField(
                                value = ageText,
                                onValueChange = {
                                    ageText = it.filter { char -> char.isDigit() }
                                    errorMessage = null
                                },
                                suffix = { Text("years") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 12.dp)
                            )

                            // Biological Sex Selection
                            Text(
                                text = "Biological Sex (Optional - Calibrates BMI metrics)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = CalTrackTextSecondary
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 6.dp, bottom = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                sexOptions.forEach { sex ->
                                    val isSelected = selectedSex == sex
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(if (isSelected) CalTrackGreen else CalTrackGreenBg)
                                            .clickable { selectedSex = sex }
                                            .padding(vertical = 10.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                            }
                                            Text(
                                                text = sex,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = if (isSelected) Color.White else CalTrackGreenDark
                                            )
                                        }
                                    }
                                }
                            }

                            if (errorMessage != null) {
                                Text(
                                    text = errorMessage!!,
                                    color = Color.Red,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            // Save Profile to SQLite Button
                            Button(
                                onClick = {
                                    val h = heightText.toFloatOrNull()
                                    if (h == null || h < 60f || h > 260f) {
                                        errorMessage = "Please enter valid height (60 - 260 cm)"
                                        return@Button
                                    }
                                    val a = ageText.toIntOrNull()

                                    viewModel.updateProfile(
                                        heightCm = h,
                                        age = a,
                                        biologicalSex = selectedSex,
                                        onSuccess = {
                                            errorMessage = null
                                            scope.launch {
                                                snackbarHostState.showSnackbar("Profile saved to SQLite successfully!")
                                            }
                                        },
                                        onError = { errorMessage = it }
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = CalTrackGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Save,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Save Profile to SQLite",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Runtime & Storage Info Card (Offline Core)
                item {
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
                                Text(
                                    text = "Runtime & Storage",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CalTrackTextPrimary
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(CalTrackGreenBg)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "Offline Core",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = CalTrackGreenDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Gemini Vision row
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
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Gemini 2.0 Flash Vision",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CalTrackTextPrimary
                                        )
                                        Text(
                                            text = "API Key Configured via ENV",
                                            fontSize = 11.sp,
                                            color = CalTrackTextSecondary
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CalTrackGreenBg)
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "Ready",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CalTrackGreenDark
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Room SQLite row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Storage,
                                        contentDescription = null,
                                        tint = CalTrackGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Room SQLite Engine",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = CalTrackTextPrimary
                                        )
                                        Text(
                                            text = "caltrack.db • Zero telemetry • Local device boundary",
                                            fontSize = 11.sp,
                                            color = CalTrackTextSecondary
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFF1F5F9))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "v2.6",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CalTrackTextSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }
}
