package com.caltrack.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.caltrack.app.data.dao.MealDao
import com.caltrack.app.data.dao.ProfileDao
import com.caltrack.app.data.dao.WeightDao
import com.caltrack.app.data.entity.MealEntity
import com.caltrack.app.data.entity.UserProfileEntity
import com.caltrack.app.data.entity.WeightEntryEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Database(
    entities = [
        MealEntity::class,
        WeightEntryEntity::class,
        UserProfileEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CalTrackDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao
    abstract fun weightDao(): WeightDao
    abstract fun profileDao(): ProfileDao

    companion object {
        @Volatile
        private var INSTANCE: CalTrackDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): CalTrackDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CalTrackDatabase::class.java,
                    "caltrack.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        prepopulateData(database)
                    }
                }
            }
        }

        private suspend fun prepopulateData(db: CalTrackDatabase) {
            val profileDao = db.profileDao()
            val mealDao = db.mealDao()
            val weightDao = db.weightDao()

            // 1. Initial User Profile
            profileDao.insertOrUpdateProfile(
                UserProfileEntity(
                    id = 1,
                    heightCm = 178f,
                    age = 29,
                    biologicalSex = "Male"
                )
            )

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()

            val todayStr = dateFormat.format(cal.time)

            // 2. Prepopulate Today's Logged Meals (matching Stitch design)
            mealDao.insertMeal(
                MealEntity(
                    title = "Oatmeal with blueberries & chia",
                    mealType = "Breakfast",
                    calories = 380,
                    proteinGrams = 14,
                    carbsGrams = 54,
                    fatGrams = 6,
                    dateString = todayStr,
                    timestamp = cal.timeInMillis - (6 * 3600 * 1000),
                    timeFormatted = "8:20 AM"
                )
            )
            mealDao.insertMeal(
                MealEntity(
                    title = "Grilled chicken quinoa salad",
                    mealType = "Lunch",
                    calories = 680,
                    proteinGrams = 58,
                    carbsGrams = 48,
                    fatGrams = 12,
                    dateString = todayStr,
                    timestamp = cal.timeInMillis - (2 * 3600 * 1000),
                    timeFormatted = "1:15 PM"
                )
            )
            mealDao.insertMeal(
                MealEntity(
                    title = "Greek yogurt & honey",
                    mealType = "Snack",
                    calories = 190,
                    proteinGrams = 18,
                    carbsGrams = 22,
                    fatGrams = 3,
                    dateString = todayStr,
                    timestamp = cal.timeInMillis - (30 * 60 * 1000),
                    timeFormatted = "4:45 PM"
                )
            )

            // 3. Prepopulate Weight Records (matching Stitch line chart & days)
            // Oct 17: 75.4 kg
            cal.add(Calendar.DAY_OF_YEAR, -4)
            val d4Str = dateFormat.format(cal.time)
            weightDao.insertWeight(
                WeightEntryEntity(weightKg = 75.4f, dateString = d4Str, timestamp = cal.timeInMillis)
            )

            // Oct 19: 75.0 kg
            cal.add(Calendar.DAY_OF_YEAR, 2)
            val d2Str = dateFormat.format(cal.time)
            weightDao.insertWeight(
                WeightEntryEntity(weightKg = 75.0f, dateString = d2Str, timestamp = cal.timeInMillis)
            )

            // Oct 21: 74.6 kg
            cal.add(Calendar.DAY_OF_YEAR, 1)
            val d1Str = dateFormat.format(cal.time)
            weightDao.insertWeight(
                WeightEntryEntity(weightKg = 74.6f, dateString = d1Str, timestamp = cal.timeInMillis)
            )

            // Today: 74.2 kg
            cal.time = Date()
            weightDao.insertWeight(
                WeightEntryEntity(
                    weightKg = 74.2f,
                    dateString = todayStr,
                    timestamp = cal.timeInMillis,
                    timeFormatted = "Today at 8:15 AM"
                )
            )

            // 4. Prepopulate Previous Days Meals for History Screen
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yestStr = dateFormat.format(cal.time)
            mealDao.insertMeal(
                MealEntity(
                    title = "Scrambled eggs on sourdough",
                    mealType = "Breakfast",
                    calories = 450,
                    proteinGrams = 26,
                    carbsGrams = 32,
                    fatGrams = 18,
                    dateString = yestStr,
                    timestamp = cal.timeInMillis - 25000000,
                    timeFormatted = "8:30 AM"
                )
            )
            mealDao.insertMeal(
                MealEntity(
                    title = "Salmon brown rice bowl",
                    mealType = "Lunch",
                    calories = 720,
                    proteinGrams = 48,
                    carbsGrams = 68,
                    fatGrams = 24,
                    dateString = yestStr,
                    timestamp = cal.timeInMillis - 15000000,
                    timeFormatted = "1:00 PM"
                )
            )
            mealDao.insertMeal(
                MealEntity(
                    title = "Steak & roasted asparagus",
                    mealType = "Dinner",
                    calories = 880,
                    proteinGrams = 58,
                    carbsGrams = 18,
                    fatGrams = 38,
                    dateString = yestStr,
                    timestamp = cal.timeInMillis - 5000000,
                    timeFormatted = "7:45 PM"
                )
            )

            // Two days ago
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val twoDaysAgoStr = dateFormat.format(cal.time)
            mealDao.insertMeal(
                MealEntity(
                    title = "Protein smoothie bowl",
                    mealType = "Breakfast",
                    calories = 420,
                    proteinGrams = 32,
                    carbsGrams = 52,
                    fatGrams = 8,
                    dateString = twoDaysAgoStr,
                    timestamp = cal.timeInMillis - 25000000,
                    timeFormatted = "9:00 AM"
                )
            )
            mealDao.insertMeal(
                MealEntity(
                    title = "Turkey avocado wrap",
                    mealType = "Lunch",
                    calories = 650,
                    proteinGrams = 42,
                    carbsGrams = 48,
                    fatGrams = 22,
                    dateString = twoDaysAgoStr,
                    timestamp = cal.timeInMillis - 14000000,
                    timeFormatted = "1:30 PM"
                )
            )
            mealDao.insertMeal(
                MealEntity(
                    title = "Pasta with lean beef marinara",
                    mealType = "Dinner",
                    calories = 850,
                    proteinGrams = 46,
                    carbsGrams = 75,
                    fatGrams = 28,
                    dateString = twoDaysAgoStr,
                    timestamp = cal.timeInMillis - 4000000,
                    timeFormatted = "8:00 PM"
                )
            )
        }
    }
}
