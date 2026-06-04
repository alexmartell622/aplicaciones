package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.data.ReadingEntity
import com.example.data.RoundEntity
import com.example.data.RoundWithReadings
import kotlinx.coroutines.flow.Flow

@Dao
interface SupplyDao {

    @Transaction
    @Query("SELECT * FROM rounds ORDER BY timestamp DESC")
    fun getAllRoundsWithReadings(): Flow<List<RoundWithReadings>>

    @Transaction
    @Query("SELECT * FROM rounds ORDER BY timestamp DESC LIMIT 1")
    fun getLatestRoundWithReadings(): Flow<RoundWithReadings?>

    @Query("SELECT * FROM rounds WHERE id = :roundId")
    suspend fun getRoundById(roundId: Long): RoundEntity?

    @Query("SELECT * FROM readings WHERE roundId = :roundId")
    suspend fun getReadingsForRound(roundId: Long): List<ReadingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: RoundEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadings(readings: List<ReadingEntity>)

    @Transaction
    suspend fun insertRoundWithReadings(round: RoundEntity, readings: List<ReadingEntity>): Long {
        val roundId = insertRound(round)
        val readingsWithId = readings.map { it.copy(roundId = roundId) }
        insertReadings(readingsWithId)
        return roundId
    }

    @Query("DELETE FROM rounds WHERE id = :roundId")
    suspend fun deleteRoundById(roundId: Long)
}
