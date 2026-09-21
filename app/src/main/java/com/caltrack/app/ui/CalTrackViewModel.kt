package com.caltrack.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.caltrack.app.data.dao.MealDao
import com.caltrack.app.data.dao.ProfileDao
import com.caltrack.app.data.dao.WeightDao
import com.caltrack.app.data.entity.MealEntity
import com.caltrack.app.data.entity.UserProfileEntity
import com.caltrack.app.data.entity.WeightEntryEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DailyHistoryItem(
    val dateString: String,
    val displayDate: String, // e.g. "Yesterday • Oct 23"
    val weightKg: Float?,
    val bmi: Float?,
    val totalCalories: Int,
    val totalProtein: Int,
    val totalCarbs: Int,
    val totalFat: Int,
    val meals: List<MealEntity>
)

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class CalTrackViewModel(
    private val mealDao: MealDao,
    private val weightDao: WeightDao,
    private val profileDao: ProfileDao
) : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val displayDateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

    private val _selectedDateCalendar = MutableStateFlow(Calendar.getInstance())
    val selectedDateCalendar = _selectedDateCalendar.asStateFlow()

    private val _selectedDateString = MutableStateFlow(dateFormat.format(Date()))
    val selectedDateString = _selectedDateString.asStateFlow()

    // Stream meals for the currently selected date
    val selectedDateMeals: StateFlow<List<MealEntity>> = _selectedDateString
        .flatMapLatest { date -> mealDao.getMealsForDate(date) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All meals across all dates
    val allMeals: StateFlow<List<MealEntity>> = mealDao.getAllMeals()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalDatabaseEntries: StateFlow<Int> = mealDao.getTotalMealsCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // User Profile
    val userProfile: StateFlow<UserProfileEntity> = profileDao.getProfile()
        .combine(MutableStateFlow(Unit)) { profile, _ ->
            profile ?: UserProfileEntity()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserProfileEntity(heightCm = 178f, age = 29, biologicalSex = "Male")
        )

    // Weight Records
    val latestWeight: StateFlow<WeightEntryEntity?> = weightDao.getLatestWeight()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allWeightsAsc: StateFlow<List<WeightEntryEntity>> = weightDao.getAllWeightsAsc()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allWeightsDesc: StateFlow<List<WeightEntryEntity>> = weightDao.getAllWeightsDesc()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Continuous History List (all logged days, newest first)
    val historyItems: StateFlow<List<DailyHistoryItem>> = combine(
        allMeals,
        allWeightsDesc,
        userProfile
    ) { meals, weights, profile ->
        val dateToMeals = meals.groupBy { it.dateString }
        val dateToWeight = weights.associateBy { it.dateString }

        val allDates = (dateToMeals.keys + dateToWeight.keys)
            .distinct()
            .sortedDescending()

        allDates.map { dateStr ->
            val dayMeals = dateToMeals[dateStr] ?: emptyList()
            val dayWeight = dateToWeight[dateStr]?.weightKg
            val dayBmi = if (dayWeight != null && profile.heightCm > 0) {
                calculateBmi(dayWeight, profile.heightCm)
            } else null

            val totalCals = dayMeals.sumOf { it.calories }
            val totalP = dayMeals.sumOf { it.proteinGrams }
            val totalC = dayMeals.sumOf { it.carbsGrams }
            val totalF = dayMeals.sumOf { it.fatGrams }

            val label = formatHistoryDateLabel(dateStr)

            DailyHistoryItem(
                dateString = dateStr,
                displayDate = label,
                weightKg = dayWeight,
                bmi = dayBmi,
                totalCalories = totalCals,
                totalProtein = totalP,
                totalCarbs = totalC,
                totalFat = totalF,
                meals = dayMeals
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Navigation and Date selection
    fun previousDay() {
        val cal = _selectedDateCalendar.value.clone() as Calendar
        cal.add(Calendar.DAY_OF_YEAR, -1)
        _selectedDateCalendar.value = cal
        _selectedDateString.value = dateFormat.format(cal.time)
    }

    fun nextDay() {
        val cal = _selectedDateCalendar.value.clone() as Calendar
        cal.add(Calendar.DAY_OF_YEAR, 1)
        _selectedDateCalendar.value = cal
        _selectedDateString.value = dateFormat.format(cal.time)
    }

    fun resetToToday() {
        val cal = Calendar.getInstance()
        _selectedDateCalendar.value = cal
        _selectedDateString.value = dateFormat.format(cal.time)
    }

    fun getSelectedDateLabel(): String {
        val selected = _selectedDateString.value
        val today = dateFormat.format(Date())
        val calYest = Calendar.getInstance()
        calYest.add(Calendar.DAY_OF_YEAR, -1)
        val yesterday = dateFormat.format(calYest.time)

        return when (selected) {
            today -> "Today, " + shortDateFormat.format(_selectedDateCalendar.value.time)
            yesterday -> "Yesterday, " + shortDateFormat.format(_selectedDateCalendar.value.time)
            else -> displayDateFormat.format(_selectedDateCalendar.value.time)
        }
    }

    fun getSelectedDateSubLabel(): String {
        val dayOfWeekFormat = SimpleDateFormat("EEEE", Locale.getDefault())
        return dayOfWeekFormat.format(_selectedDateCalendar.value.time) + " • Daily Log"
    }

    // Adding Meal with validation
    fun addMeal(
        title: String,
        mealType: String,
        calories: Int,
        protein: Int,
        carbs: Int,
        fat: Int,
        timeFormatted: String = "",
        imageUri: String? = null,
        aiConfidence: Float? = null,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (title.isBlank()) {
            onError("Please enter a meal description")
            return
        }
        if (calories <= 0) {
            onError("Calories must be greater than 0")
            return
        }
        if (protein < 0 || carbs < 0 || fat < 0) {
            onError("Macros cannot be negative")
            return
        }

        val formattedTime = if (timeFormatted.isBlank()) {
            SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        } else {
            timeFormatted
        }

        viewModelScope.launch {
            mealDao.insertMeal(
                MealEntity(
                    title = title.trim(),
                    mealType = mealType,
                    calories = calories,
                    proteinGrams = protein,
                    carbsGrams = carbs,
                    fatGrams = fat,
                    dateString = _selectedDateString.value,
                    timeFormatted = formattedTime,
                    imageUri = imageUri,
                    aiConfidence = aiConfidence
                )
            )
            onSuccess()
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            mealDao.deleteMeal(meal)
        }
    }

    // Weight logging
    fun logWeight(
        weightKg: Float,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (weightKg <= 20f || weightKg >= 350f) {
            onError("Please enter a realistic weight (20 - 350 kg)")
            return
        }
        val cal = Calendar.getInstance()
        val todayStr = dateFormat.format(cal.time)
        val timeStr = "Today at " + SimpleDateFormat("h:mm a", Locale.getDefault()).format(cal.time)

        viewModelScope.launch {
            weightDao.insertWeight(
                WeightEntryEntity(
                    weightKg = weightKg,
                    dateString = todayStr,
                    timestamp = cal.timeInMillis,
                    timeFormatted = timeStr
                )
            )
            onSuccess()
        }
    }

    // Profile update (Height, Age, Biological Sex)
    fun updateProfile(
        heightCm: Float,
        age: Int?,
        biologicalSex: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (heightCm < 60f || heightCm > 260f) {
            onError("Height must be between 60 cm and 260 cm")
            return
        }
        if (age != null && (age < 10 || age > 120)) {
            onError("Please enter a valid age (10-120)")
            return
        }

        viewModelScope.launch {
            profileDao.insertOrUpdateProfile(
                UserProfileEntity(
                    id = 1,
                    heightCm = heightCm,
                    age = age,
                    biologicalSex = biologicalSex
                )
            )
            onSuccess()
        }
    }

    companion object {
        fun calculateBmi(weightKg: Float, heightCm: Float): Float {
            if (heightCm <= 0f) return 0f
            val heightM = heightCm / 100f
            val bmi = weightKg / (heightM * heightM)
            return (Math.round(bmi * 10f) / 10f)
        }

        fun getBmiCategory(bmi: Float): Pair<String, String> {
            return when {
                bmi < 18.5f -> "Underweight" to "BMI < 18.5"
                bmi < 25.0f -> "Normal Weight" to "18.5 - 24.9"
                bmi < 30.0f -> "Overweight" to "25.0 - 29.9"
                else -> "Obese" to "BMI ≥ 30.0"
            }
        }
    }

    private fun formatHistoryDateLabel(dateStr: String): String {
        return try {
            val d = dateFormat.parse(dateStr) ?: return dateStr
            val calToday = Calendar.getInstance()
            val todayStr = dateFormat.format(calToday.time)
            calToday.add(Calendar.DAY_OF_YEAR, -1)
            val yestStr = dateFormat.format(calToday.time)

            when (dateStr) {
                todayStr -> "Today • " + shortDateFormat.format(d)
                yestStr -> "Yesterday • " + shortDateFormat.format(d)
                else -> {
                    val dayNameFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                    dayNameFormat.format(d) + " • " + shortDateFormat.format(d)
                }
            }
        } catch (e: Exception) {
            dateStr
        }
    }
}

class CalTrackViewModelFactory(
    private val mealDao: MealDao,
    private val weightDao: WeightDao,
    private val profileDao: ProfileDao
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalTrackViewModel::class.java)) {
            return CalTrackViewModel(mealDao, weightDao, profileDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
