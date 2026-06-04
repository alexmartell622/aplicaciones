package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface BinnacleDao {

    // Collaborators
    @Query("SELECT * FROM collaborators ORDER BY name ASC")
    fun getAllCollaborators(): Flow<List<Collaborator>>

    @Query("SELECT * FROM collaborators WHERE id = :id LIMIT 1")
    suspend fun getCollaboratorById(id: Int): Collaborator?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollaborator(collaborator: Collaborator): Long

    @Update
    suspend fun updateCollaborator(collaborator: Collaborator)

    @Query("DELETE FROM collaborators WHERE id = :id")
    suspend fun deleteCollaboratorById(id: Int)

    // Binnacle Entries
    @Query("SELECT * FROM binnacle_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<BinnacleEntry>>

    @Query("SELECT * FROM binnacle_entries WHERE collaboratorId = :collabId ORDER BY timestamp DESC")
    fun getEntriesByCollaborator(collabId: Int): Flow<List<BinnacleEntry>>

    @Query("SELECT * FROM binnacle_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: Int): BinnacleEntry?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: BinnacleEntry): Long

    @Update
    suspend fun updateEntry(entry: BinnacleEntry)

    @Query("DELETE FROM binnacle_entries WHERE id = :id")
    suspend fun deleteEntryById(id: Int)

    // Action Items
    @Query("SELECT * FROM action_items ORDER BY dueDate ASC")
    fun getAllActions(): Flow<List<ActionItem>>

    @Query("SELECT * FROM action_items WHERE entryId = :entryId ORDER BY dueDate ASC")
    fun getActionsByEntry(entryId: Int): Flow<List<ActionItem>>

    @Query("SELECT * FROM action_items WHERE collaboratorId = :collabId ORDER BY dueDate ASC")
    fun getActionsByCollaborator(collabId: Int): Flow<List<ActionItem>>

    @Query("SELECT * FROM action_items WHERE id = :id LIMIT 1")
    suspend fun getActionById(id: Int): ActionItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAction(action: ActionItem): Long

    @Update
    suspend fun updateAction(action: ActionItem)

    @Query("DELETE FROM action_items WHERE id = :id")
    suspend fun deleteActionById(id: Int)

    // Audit Logs
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditLog(log: AuditLog): Long
}
