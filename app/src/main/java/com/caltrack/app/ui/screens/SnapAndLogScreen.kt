package com.caltrack.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
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
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SnapAndLogScreen(
    viewModel: CalTrackViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current

    val isAnalyzing by viewModel.isAnalyzingFood.collectAsState()
    val analysisError by viewModel.foodAnalysisError.collectAsState()
    val configuredApiKey by viewModel.apiKey.collectAsState()

    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    var mealDescription by remember { mutableStateOf("Grilled Salmon & Sweet Potato Bowl") }
    var mealSlot by remember { mutableStateOf("Dinner") }
    var showSlotDropdown by remember { mutableStateOf(false) }
    var calories by remember { mutableStateOf(520) }
    var proteinGrams by remember { mutableStateOf(42) }
    var carbsGrams by remember { mutableStateOf(38) }
    var fatGrams by remember { mutableStateOf(18) }
    var aiConfidence by remember { mutableStateOf(0.95f) }
    var portionGrams by remember { mutableStateOf(280) }
    var aiSummary by remember { mutableStateOf("Atlantic salmon fillet with roasted sweet potato cubes") }
    var aiIngredients by remember { mutableStateOf(listOf("Salmon", "Sweet Potato", "Broccoli")) }

    var logTime by remember {
        mutableStateOf(
            "Today, " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        )
    }

    val mealSlots = listOf("Breakfast", "Lunch", "Dinner", "Snack")

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            capturedImageUri = tempCameraUri
            viewModel.analyzeFoodPhoto(context, tempCameraUri!!) { result ->
                mealDescription = result.dishName
                portionGrams = result.portionGrams
                calories = result.calories
                proteinGrams = result.proteinGrams
                carbsGrams = result.carbsGrams
                fatGrams = result.fatGrams
                aiConfidence = result.confidence
                aiSummary = result.summary
                aiIngredients = result.ingredients
            }
        }
    }

    // Gallery picker launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            capturedImageUri = uri
            viewModel.analyzeFoodPhoto(context, uri) { result ->
                mealDescription = result.dishName
                portionGrams = result.portionGrams
                calories = result.calories
                proteinGrams = result.proteinGrams
                carbsGrams = result.carbsGrams
                fatGrams = result.fatGrams
                aiConfidence = result.confidence
                aiSummary = result.summary
                aiIngredients = result.ingredients
            }
        }
    }

    fun launchCamera() {
        try {
            val cacheDir = File(context.cacheDir, "camera_photos").apply { mkdirs() }
            val tempFile = File.createTempFile("meal_snap_", ".jpg", cacheDir)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                tempFile
            )
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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
            // Gemini AI Status / Confidence Banner
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
                            text = "GEMINI FLASH VISION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5CF6),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isAnalyzing) Color(0xFFEDE9FE) else CalTrackGreenBg)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (isAnalyzing) "Analyzing Food..." else "${(aiConfidence * 100).toInt()}% AI Confidence",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isAnalyzing) Color(0xFF7C3AED) else CalTrackGreenDark
                        )
                    }
                }

                Text(
                    text = mealDescription,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = CalTrackTextPrimary,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Text(
                    text = if (aiSummary.isNotBlank()) aiSummary else "AI Vision estimated nutritional values. You can review and adjust below:",
                    fontSize = 12.sp,
                    color = CalTrackTextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )

                // Detected ingredients tags
                if (aiIngredients.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        aiIngredients.forEach { ingredient ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = ingredient,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = CalTrackTextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // Error Banner (e.g. missing API key or scan error)
            if (analysisError != null) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF2F2))
                            .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = analysisError ?: "Error analyzing photo",
                                        fontSize = 12.sp,
                                        color = Color(0xFFB91C1C),
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.clearFoodAnalysisError() },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = Color(0xFFDC2626),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            if (configuredApiKey.isBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = onNavigateToProfile,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier.align(Alignment.End)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Settings,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Set Free API Key in Profile", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
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
                        // Visual Dish Card (Real Photo or Fallback)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (capturedImageUri != null) {
                                AsyncImage(
                                    model = capturedImageUri,
                                    contentDescription = "Food Photo",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Restaurant,
                                        contentDescription = null,
                                        tint = CalTrackGreen,
                                        modifier = Modifier.size(52.dp)
                                    )
                                    Text(
                                        text = mealDescription,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CalTrackTextPrimary,
                                        modifier = Modifier.padding(top = 6.dp)
                                    )
                                    Text(
                                        text = "Snap a photo of your meal to calculate macros automatically",
                                        fontSize = 11.sp,
                                        color = CalTrackTextSecondary,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }

                            // Portion tag overlay
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 10.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.Black.copy(alpha = 0.70f))
                                    .padding(horizontal = 12.dp, vertical = 5.dp)
                            ) {
                                Text(
                                    text = if (isAnalyzing) "Analyzing visual features..." else "Estimated Portion • ${portionGrams}g",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }

                            // Scanning overlay animation
                            AnimatedVisibility(
                                visible = isAnalyzing,
                                enter = fadeIn(),
                                exit = fadeOut(),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.45f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        CircularProgressIndicator(
                                            color = Color.White,
                                            strokeWidth = 3.dp,
                                            modifier = Modifier.size(36.dp)
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = "Gemini Vision is analyzing...",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }

                        if (isAnalyzing) {
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp),
                                color = Color(0xFF8B5CF6),
                                trackColor = Color(0xFFEDE9FE)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Camera and Gallery Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Camera Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(CalTrackGreenBg)
                                    .clickable { launchCamera() }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Take Photo",
                                        tint = CalTrackGreenDark,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (capturedImageUri != null) "Retake Photo" else "Take Photo",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = CalTrackGreenDark
                                    )
                                }
                            }

                            // Gallery Button
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFF1F5F9))
                                    .clickable {
                                        galleryLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = "Choose from Gallery",
                                        tint = CalTrackTextSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Upload Gallery",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
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

            // Total Energy Stepper Card
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
                            Box(
                                modifier = Modifier
                                    .weight(pPct.toFloat().coerceAtLeast(1f))
                                    .height(6.dp)
                                    .background(MacroProteinBlue)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(cPct.toFloat().coerceAtLeast(1f))
                                    .height(6.dp)
                                    .background(MacroCarbsAmber)
                            )
                            Box(
                                modifier = Modifier
                                    .weight(fPct.toFloat().coerceAtLeast(1f))
                                    .height(6.dp)
                                    .background(MacroFatRed)
                            )
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
                        text = "Data is saved locally to Room SQLite table 'food_entries' with image attachment.",
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
                                imageUri = capturedImageUri?.toString(),
                                aiConfidence = aiConfidence,
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
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = CalTrackTextPrimary
                )
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
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF1F5F9))
                        .clickable { onIncrement() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp)
                    )
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
