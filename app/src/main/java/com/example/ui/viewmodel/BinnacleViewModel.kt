package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.BinnacleRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BinnacleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BinnacleRepository

    // Base Database flows
    val collaborators: StateFlow<List<Collaborator>>
    val allEntries: StateFlow<List<BinnacleEntry>>
    val allActions: StateFlow<List<ActionItem>>
    val allAuditLogs: StateFlow<List<AuditLog>>

    // Filter controls
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedTeam = MutableStateFlow("Todos")
    val selectedTeam = _selectedTeam.asStateFlow()

    private val _selectedCategory = MutableStateFlow("Todas")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _selectedType = MutableStateFlow("Todos")
    val selectedType = _selectedType.asStateFlow()

    private val _selectedSeverity = MutableStateFlow("Todas")
    val selectedSeverity = _selectedSeverity.asStateFlow()

    private val _selectedCollabId = MutableStateFlow<Int?>(null)
    val selectedCollabId = _selectedCollabId.asStateFlow()

    // Export status and logs
    private val _exportLog = MutableStateFlow<String?>(null)
    val exportLog = _exportLog.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = BinnacleRepository(database.binnacleDao())

        // Start checking seed data
        viewModelScope.launch {
            repository.checkAndSeedDatabase()
        }

        collaborators = repository.allCollaborators.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

        allEntries = repository.allEntries.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

        allActions = repository.allActions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

        allAuditLogs = repository.allAuditLogs.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )
    }

    // Helper class for filters combo
    data class FilterState(
        val query: String,
        val team: String,
        val category: String,
        val type: String,
        val severity: String,
        val collabId: Int?
    )

    private val filterState: Flow<FilterState> = combine(
        _searchQuery,
        _selectedTeam,
        _selectedCategory,
        _selectedType,
        _selectedSeverity,
        _selectedCollabId
    ) { array: Array<Any?> ->
        FilterState(
            query = array[0] as String,
            team = array[1] as String,
            category = array[2] as String,
            type = array[3] as String,
            severity = array[4] as String,
            collabId = array[5] as? Int
        )
    }

    // Reactive Combined Lists
    val filteredEntries: StateFlow<List<BinnacleEntry>> = combine(
        allEntries,
        collaborators,
        filterState
    ) { entries, collabs, filters ->
        entries.filter { entry ->
            val collab = collabs.find { it.id == entry.collaboratorId }
            
            val matchQuery = filters.query.isEmpty() || 
                    entry.title.contains(filters.query, ignoreCase = true) ||
                    entry.description.contains(filters.query, ignoreCase = true) ||
                    entry.category.contains(filters.query, ignoreCase = true) ||
                    entry.tags.contains(filters.query, ignoreCase = true) ||
                    (collab?.name?.contains(filters.query, ignoreCase = true) == true)

            val matchTeam = filters.team == "Todos" || (collab?.team ?: "") == filters.team
            val matchCategory = filters.category == "Todas" || entry.category == filters.category
            val matchType = filters.type == "Todos" || entry.type == filters.type
            val matchSeverity = filters.severity == "Todas" || entry.severity == filters.severity
            val matchCollab = filters.collabId == null || entry.collaboratorId == filters.collabId

            matchQuery && matchTeam && matchCategory && matchType && matchSeverity && matchCollab
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val filteredActions: StateFlow<List<ActionItem>> = combine(
        allActions,
        collaborators,
        _searchQuery,
        _selectedTeam,
        _selectedCollabId
    ) { actions, collabs, query, team, collabId ->
        actions.filter { action ->
            val collab = collabs.find { it.id == action.collaboratorId }

            val matchQuery = query.isEmpty() ||
                    action.title.contains(query, ignoreCase = true) ||
                    action.description.contains(query, ignoreCase = true) ||
                    action.responsibleName.contains(query, ignoreCase = true) ||
                    (collab?.name?.contains(query, ignoreCase = true) == true)

            val matchTeam = team == "Todos" || (collab?.team ?: "") == team
            val matchCollab = collabId == null || action.collaboratorId == collabId

            matchQuery && matchTeam && matchCollab
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Update Filter commands
    fun setSearchQuery(query: String) { _searchQuery.value = query }
    fun setSelectedTeam(team: String) { _selectedTeam.value = team }
    fun setSelectedCategory(cat: String) { _selectedCategory.value = cat }
    fun setSelectedType(type: String) { _selectedType.value = type }
    fun setSelectedSeverity(sev: String) { _selectedSeverity.value = sev }
    fun selectCollaborator(collabId: Int?) { _selectedCollabId.value = collabId }

    // Actions
    fun createEntry(
        collaboratorId: Int,
        type: String,
        title: String,
        description: String,
        severity: String,
        category: String,
        tags: String,
        creatorEmail: String = "valmung622@gmail.com"
    ) {
        viewModelScope.launch {
            val entry = BinnacleEntry(
                collaboratorId = collaboratorId,
                type = type,
                title = title,
                description = description,
                severity = severity,
                category = category,
                tags = tags,
                creatorEmail = creatorEmail
            )
            repository.insertEntry(entry, creatorEmail)
        }
    }

    fun updateEntryStatus(entryId: Int, newStatus: String, actorEmail: String = "valmung622@gmail.com") {
        viewModelScope.launch {
            val existing = repository.getEntryById(entryId)
            if (existing != null) {
                val updated = existing.copy(status = newStatus)
                repository.updateEntry(updated, actorEmail)
            }
        }
    }

    fun createActionItem(
        entryId: Int,
        collaboratorId: Int,
        title: String,
        description: String,
        responsibleName: String,
        dueDate: Long,
        priority: String,
        actorEmail: String = "valmung622@gmail.com"
    ) {
        viewModelScope.launch {
            val action = ActionItem(
                entryId = entryId,
                collaboratorId = collaboratorId,
                title = title,
                description = description,
                responsibleName = responsibleName,
                dueDate = dueDate,
                status = "PENDING",
                priority = priority
            )
            repository.insertAction(action, actorEmail)

            // Auto-advance entry status to ONGOING if it was OPEN or DRAFT
            val entry = repository.getEntryById(entryId)
            if (entry != null && (entry.status == "OPEN" || entry.status == "DRAFT")) {
                repository.updateEntry(entry.copy(status = "ONGOING"), actorEmail)
            }
        }
    }

    fun updateActionStatus(actionId: Int, newStatus: String, actorEmail: String = "valmung622@gmail.com") {
        viewModelScope.launch {
            val existing = repository.getActionById(actionId)
            if (existing != null) {
                val updated = existing.copy(status = newStatus)
                repository.updateAction(updated, actorEmail)

                // If done, check if all actions for this entry are done, if so we don't force closed but we can log it.
            }
        }
    }

    fun createCollaborator(name: String, role: String, team: String, email: String, avatarColor: Int, actorEmail: String = "valmung622@gmail.com") {
        viewModelScope.launch {
            val collab = Collaborator(
                name = name,
                role = role,
                team = team,
                email = email,
                avatarColor = avatarColor
            )
            repository.insertCollaborator(collab, actorEmail)
        }
    }

    fun exportReportToText(): String {
        val collabs = collaborators.value
        val entries = filteredEntries.value
        val actions = filteredActions.value

        val sb = StringBuilder()
        sb.append("=== REPORTE DE BITÁCORA DEPARTAMENTAL ===\n")
        sb.append("Generado el: ${java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date())}\n")
        sb.append("Registrador: valmung622@gmail.com\n")
        sb.append("Filtros Activos: Equipo=${selectedTeam.value}, Categoría=${selectedCategory.value}, Tipo=${selectedType.value}, Severidad=${selectedSeverity.value}\n")
        sb.append("=========================================\n\n")

        sb.append("--- ROSTER DE COLABORADORES ACTIVOS ---\n")
        collabs.forEach {
            sb.append("- ${it.name} | ${it.role} | ${it.team} | ${it.email}\n")
        }
        sb.append("\n")

        sb.append("--- BITÁCORA DE REGISTROS (${entries.size}) ---\n")
        entries.forEach { entry ->
            val collab = collabs.find { it.id == entry.collaboratorId }
            val formattedDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date(entry.timestamp))
            sb.append("[${entry.type}] ${entry.title} - ${formattedDate}\n")
            sb.append("  Colaborador: ${collab?.name ?: "N/D"} (${collab?.role ?: "N/D"})\n")
            sb.append("  Detalle: ${entry.description}\n")
            sb.append("  Severidad: ${entry.severity} | Categoría: ${entry.category} | Estado: ${entry.status}\n")
            sb.append("  Etiquetas: ${entry.tags}\n")
            
            // Subactions
            val entryActions = actions.filter { it.entryId == entry.id }
            if (entryActions.isNotEmpty()) {
                sb.append("  Flujos de Seguimiento (${entryActions.size}):\n")
                entryActions.forEach { act ->
                    val dueDateString = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(act.dueDate))
                    sb.append("    * [${act.status}] Asignado a: ${act.responsibleName} - ${act.title} (Límite: ${dueDateString}, Prioridad: ${act.priority})\n")
                }
            }
            sb.append("\n")
        }

        sb.append("--- HISTORIAL DE AUDITORÍA RECIENTE ---\n")
        allAuditLogs.value.take(20).forEach { log ->
            val logTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(log.timestamp))
            sb.append("[${logTime}] [${log.actionType}] por ${log.userEmail}: ${log.details}\n")
        }

        val report = sb.toString()
        _exportLog.value = "Reporte compilado con éxito: ${entries.size} registros procesados."
        return report
    }

    fun clearExportLog() {
        _exportLog.value = null
    }

    // Reset database to initial seed
    fun resetToSeeds() {
        viewModelScope.launch {
            // Delete all and re-pop
            // In a production app, we would make detailed repository clears.
            val database = AppDatabase.getDatabase(getApplication())
            database.clearAllTables()
            repository.checkAndSeedDatabase()
        }
    }
}
