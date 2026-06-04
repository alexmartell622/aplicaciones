package com.example.data.repository

import com.example.data.ReadingEntity
import com.example.data.RoundEntity
import com.example.data.RoundWithReadings
import com.example.data.database.SupplyDao
import kotlinx.coroutines.flow.Flow

class SupplyRepository(private val supplyDao: SupplyDao) {

    val allRounds: Flow<List<RoundWithReadings>> = supplyDao.getAllRoundsWithReadings()

    val latestRound: Flow<RoundWithReadings?> = supplyDao.getLatestRoundWithReadings()

    suspend fun insertRoundWithReadings(round: RoundEntity, readings: List<ReadingEntity>): Long {
        return supplyDao.insertRoundWithReadings(round, readings)
    }

    suspend fun deleteRound(roundId: Long) {
        supplyDao.deleteRoundById(roundId)
    }
}
