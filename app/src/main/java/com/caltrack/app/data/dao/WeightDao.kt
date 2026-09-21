package com.caltrack.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.caltrack.app.data.entity.WeightEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weight: WeightEntryEntity): Long

    @Delete
    suspend fun deleteWeight(weight: WeightEntryEntity)

    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC LIMIT 1")
    fun getLatestWeight(): Flow<WeightEntryEntity?>

    @Query("SELECT * FROM weight_entries ORDER BY timestamp ASC")
    fun getAllWeightsAsc(): Flow<List<WeightEntryEntity>>

    @Query("SELECT * FROM weight_entries ORDER BY timestamp DESC")
    fun getAllWeightsDesc(): Flow<List<WeightEntryEntity>>
}
