package com.example.data.local

import android.content.Context
import androidx.room.*
import com.example.data.model.*

@Database(
    entities = [
        Collaborator::class,
        BinnacleEntry::class,
        ActionItem::class,
        AuditLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun binnacleDao(): BinnacleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "binnacle_database"
                )
                .fallbackToDestructiveMigration() // Simple strategy for MVP development
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
