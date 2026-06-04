package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.BinnacleEntry
import com.example.data.model.Collaborator
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BinnacleViewModel

enum class MainTab {
    DASHBOARD,
    MATRIX,
    KANBAN,
    LOGS
}

class MainActivity : ComponentActivity() {

    private val viewModel: BinnacleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                // Main orchestrator state
                var currentTab by remember { mutableStateOf(MainTab.DASHBOARD) }
                
                // Track selected collaborator for active timeline depth view
                var timelineCollab by remember { mutableStateOf<Collaborator?>(null) }

                // Dialog showing controls
                var showingCreateEntryDialog by remember { mutableStateOf(false) }
                var entryPreselectedCollabId by remember { mutableStateOf<Int?>(null) }

                var showingCreateActionDialog by remember { mutableStateOf(false) }
                var actionPreselectedEntry by remember { mutableStateOf<BinnacleEntry?>(null) }

                var showingCreateCollabDialog by remember { mutableStateOf(false) }

                // ViewModel Collected State
                val collaborators by viewModel.collaborators.collectAsStateWithLifecycle()
                val filteredEntries by viewModel.filteredEntries.collectAsStateWithLifecycle()
                val allEntries by viewModel.allEntries.collectAsStateWithLifecycle()
                val filteredActions by viewModel.filteredActions.collectAsStateWithLifecycle()
                val allActions by viewModel.allActions.collectAsStateWithLifecycle()
                val auditLogs by viewModel.allAuditLogs.collectAsStateWithLifecycle()

                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val selectedTeam by viewModel.selectedTeam.collectAsStateWithLifecycle()
                val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
                val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()
                val selectedSeverity by viewModel.selectedSeverity.collectAsStateWithLifecycle()
                val exportLogMessage by viewModel.exportLog.collectAsStateWithLifecycle()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        // Dynamic bottom bar using M3 tokens
                        NavigationBar(
                            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                        ) {
                            NavigationBarItem(
                                selected = currentTab == MainTab.DASHBOARD,
                                onClick = { 
                                    currentTab = MainTab.DASHBOARD 
                                },
                                label = { Text("Métricas", style = MaterialTheme.typography.labelSmall) },
                                icon = { Icon(Icons.Default.Analytics, contentDescription = "Métricas") },
                                modifier = Modifier.testTag("nav_dashboard")
                            )
                            NavigationBarItem(
                                selected = currentTab == MainTab.MATRIX,
                                onClick = { 
                                    currentTab = MainTab.MATRIX
                                    // Keep timeline state or reset inside matrix tab? 
                                    // Usually keep it, or let clicking this tab reset timeline
                                    timelineCollab = null
                                },
                                label = { Text("Matriz", style = MaterialTheme.typography.labelSmall) },
                                icon = { Icon(Icons.Default.Dns, contentDescription = "Matriz Roster") },
                                modifier = Modifier.testTag("nav_matrix")
                            )
                            NavigationBarItem(
                                selected = currentTab == MainTab.KANBAN,
                                onClick = { 
                                    currentTab = MainTab.KANBAN 
                                },
                                label = { Text("Kanban", style = MaterialTheme.typography.labelSmall) },
                                icon = { Icon(Icons.Default.Splitscreen, contentDescription = "Compromisos Kanban") },
                                modifier = Modifier.testTag("nav_kanban")
                            )
                            NavigationBarItem(
                                selected = currentTab == MainTab.LOGS,
                                onClick = { 
                                    currentTab = MainTab.LOGS 
                                },
                                label = { Text("Historial", style = MaterialTheme.typography.labelSmall) },
                                icon = { Icon(Icons.Default.ListAlt, contentDescription = "Historial Log") },
                                modifier = Modifier.testTag("nav_logs")
                            )
                        }
                    },
                    floatingActionButton = {
                        // Floating action button for quick creation when not deep in timeline
                        if (currentTab != MainTab.KANBAN) {
                            FloatingActionButton(
                                onClick = {
                                    entryPreselectedCollabId = timelineCollab?.id
                                    showingCreateEntryDialog = true
                                },
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.White,
                                modifier = Modifier.testTag("quick_add_binnacle_fab")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = "Añadir Entrada")
                            }
                        }
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        when (currentTab) {
                            MainTab.DASHBOARD -> {
                                DashboardScreen(
                                    collaborators = collaborators,
                                    entries = allEntries,
                                    actions = allActions,
                                    onNavigateToMatrix = { currentTab = MainTab.MATRIX },
                                    onNavigateToKanban = { currentTab = MainTab.KANBAN }
                                )
                            }
                            
                            MainTab.MATRIX -> {
                                val currentTimelineCollab = timelineCollab
                                if (currentTimelineCollab != null) {
                                    // Slide open depth Timeline detail list
                                    TimelineDetailScreen(
                                        collaborator = currentTimelineCollab,
                                        entries = allEntries,
                                        actions = allActions,
                                        onBack = { timelineCollab = null },
                                        onAddEntry = {
                                            entryPreselectedCollabId = currentTimelineCollab.id
                                            showingCreateEntryDialog = true
                                        },
                                        onAddActionForEntry = { entry ->
                                            actionPreselectedEntry = entry
                                            showingCreateActionDialog = true
                                        },
                                        onUpdateActionStatus = { action, state ->
                                            viewModel.updateActionStatus(action.id, state)
                                        },
                                        onUpdateEntryStatus = { entry, state ->
                                            viewModel.updateEntryStatus(entry.id, state)
                                        }
                                    )
                                } else {
                                    // Default Collaborators Matrix view
                                    MatrixScreen(
                                        collaborators = collaborators,
                                        entries = allEntries,
                                        actions = allActions,
                                        onSelectCollaboratorTimeline = { col -> timelineCollab = col },
                                        onAddNewEntryForCollaborator = { col ->
                                            entryPreselectedCollabId = col.id
                                            showingCreateEntryDialog = true
                                        },
                                        onAddNewCollaborator = {
                                            showingCreateCollabDialog = true
                                        }
                                    )
                                }
                            }

                            MainTab.KANBAN -> {
                                KanbanScreen(
                                    collaborators = collaborators,
                                    actions = allActions,
                                    onUpdateActionStatus = { action, state ->
                                        viewModel.updateActionStatus(action.id, state)
                                    }
                                )
                            }

                            MainTab.LOGS -> {
                                HistoryLogsScreen(
                                    collaborators = collaborators,
                                    entries = filteredEntries,
                                    auditLogs = auditLogs,
                                    searchQuery = searchQuery,
                                    onSearchChange = { viewModel.setSearchQuery(it) },
                                    selectedTeam = selectedTeam,
                                    onTeamChange = { viewModel.setSelectedTeam(it) },
                                    selectedCategory = selectedCategory,
                                    onCategoryChange = { viewModel.setSelectedCategory(it) },
                                    selectedType = selectedType,
                                    onTypeChange = { viewModel.setSelectedType(it) },
                                    selectedSeverity = selectedSeverity,
                                    onSeverityChange = { viewModel.setSelectedSeverity(it) },
                                    onExportReport = { viewModel.exportReportToText() },
                                    exportLogMessage = exportLogMessage,
                                    onClearExportMessage = { viewModel.clearExportLog() },
                                    onResetDatabase = { viewModel.resetToSeeds() }
                                )
                            }
                        }
                    }
                }

                // Render active Dialogs if visible
                if (showingCreateEntryDialog && collaborators.isNotEmpty()) {
                    CreateEntryDialog(
                        collaborators = collaborators,
                        preselectedCollaboratorId = entryPreselectedCollabId,
                        onDismiss = {
                            showingCreateEntryDialog = false
                            entryPreselectedCollabId = null
                        },
                        onSave = { collabId, type, title, desc, sev, cat, tags ->
                            viewModel.createEntry(
                                collaboratorId = collabId,
                                type = type,
                                title = title,
                                description = desc,
                                severity = sev,
                                category = cat,
                                tags = tags
                            )
                        }
                    )
                }

                val activeActionEntry = actionPreselectedEntry
                if (showingCreateActionDialog && activeActionEntry != null) {
                    val activeCollab = collaborators.find { it.id == activeActionEntry.collaboratorId }
                    CreateActionDialog(
                        preselectedEntryId = activeActionEntry.id,
                        preselectedCollaboratorId = activeActionEntry.collaboratorId,
                        preselectedCollaboratorName = activeCollab?.name ?: "Personal",
                        onDismiss = {
                            showingCreateActionDialog = false
                            actionPreselectedEntry = null
                        },
                        onSave = { title, desc, resp, due, prio ->
                            viewModel.createActionItem(
                                entryId = activeActionEntry.id,
                                collaboratorId = activeActionEntry.collaboratorId,
                                title = title,
                                description = desc,
                                responsibleName = resp,
                                dueDate = due,
                                priority = prio
                            )
                        }
                    )
                }

                if (showingCreateCollabDialog) {
                    CreateCollaboratorDialog(
                        onDismiss = { showingCreateCollabDialog = false },
                        onSave = { name, role, team, email, avatarColorInt ->
                            viewModel.createCollaborator(
                                name = name,
                                role = role,
                                team = team,
                                email = email,
                                avatarColor = avatarColorInt
                            )
                        }
                    )
                }
            }
        }
    }
}
