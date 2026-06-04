package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "collaborators")
data class Collaborator(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val role: String,
    val team: String,
    val email: String,
    val avatarColor: Int = 0xFF4F46E5.toInt() // Indigo by default
) : Serializable

@Entity(tableName = "binnacle_entries")
data class BinnacleEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val collaboratorId: Int,
    val type: String, // "INCIDENT", "GOOD", "BAD", "OBSERVATION"
    val title: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val severity: String = "NONE", // "NONE", "LOW", "MEDIUM", "HIGH", "CRITICAL"
    val status: String = "OPEN", // "DRAFT", "OPEN", "ONGOING", "CLOSED"
    val category: String, // e.g. "Puntualidad", "Rendimiento", "Trabajo en Equipo", "Falta Técnica"
    val tags: String = "", // comma-separated strings
    val creatorEmail: String = "valmung622@gmail.com"
) : Serializable

@Entity(tableName = "action_items")
data class ActionItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val entryId: Int,
    val collaboratorId: Int,
    val title: String,
    val description: String,
    val responsibleName: String,
    val createdAt: Long = System.currentTimeMillis(),
    val dueDate: Long,
    val status: String = "PENDING", // "PENDING", "IN_PROGRESS", "BLOCKED", "DONE", "CANCELLED"
    val priority: String = "MEDIUM" // "LOW", "MEDIUM", "HIGH", "CRITICAL"
) : Serializable

@Entity(tableName = "audit_logs")
data class AuditLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val entityType: String, // "ENTRY", "ACTION", "COLLABORATOR"
    val entityId: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val userEmail: String,
    val actionType: String, // "CREATE", "UPDATE", "DELETE"
    val details: String
) : Serializable
