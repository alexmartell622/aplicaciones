package com.example.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Embedded
import androidx.room.Relation

@Entity(tableName = "rounds")
data class RoundEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val technicianName: String,
    val shift: String, // "Diurno", "Nocturno", "Mixto"
    val steamGenState: String, // "Operando", "En espera", "Fuera de servicio"
    val notes: String = ""
)

@Entity(
    tableName = "readings",
    foreignKeys = [
        ForeignKey(
            entity = RoundEntity::class,
            parentColumns = ["id"],
            childColumns = ["roundId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("roundId")]
)
data class ReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val roundId: Long,
    val supplyKey: String,
    val recordedValue: Double?,
    val recordedText: String?,
    val isAlerted: Boolean,
    val isRepSupplyChecked: Boolean = false // Technician manually selected replenishment needed
)

data class RoundWithReadings(
    @Embedded val round: RoundEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "roundId"
    )
    val readings: List<ReadingEntity>
)
