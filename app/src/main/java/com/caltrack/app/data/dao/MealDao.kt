package com.caltrack.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caltrack.app.data.entity.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("SELECT * FROM food_entries WHERE dateString = :date ORDER BY timestamp DESC")
    fun getMealsForDate(date: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM food_entries ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<MealEntity>>

    @Query("SELECT COUNT(*) FROM food_entries")
    fun getTotalMealsCount(): Flow<Int>

    @Query("DELETE FROM food_entries")
    suspend fun deleteAllMeals()
}
