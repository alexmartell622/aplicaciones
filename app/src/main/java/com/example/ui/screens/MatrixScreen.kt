package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionItem
import com.example.data.model.BinnacleEntry
import com.example.data.model.Collaborator

@Composable
fun MatrixScreen(
    collaborators: List<Collaborator>,
    entries: List<BinnacleEntry>,
    actions: List<ActionItem>,
    onSelectCollaboratorTimeline: (Collaborator) -> Unit,
    onAddNewEntryForCollaborator: (Collaborator) -> Unit,
    onAddNewCollaborator: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTeamFilter by remember { mutableStateOf("Todos") }

    val teams = listOf("Todos") + collaborators.map { it.team }.distinct().sorted()

    val filteredCollabs = collaborators.filter { collab ->
        val matchSearch = collab.name.contains(searchQuery, ignoreCase = true) ||
                collab.role.contains(searchQuery, ignoreCase = true) ||
                collab.email.contains(searchQuery, ignoreCase = true)
        
        val matchTeam = selectedTeamFilter == "Todos" || collab.team == selectedTeamFilter

        matchSearch && matchTeam
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper Action and Title Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Matriz de Colaboradores",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Estado y semáforo de clima operacional del personal",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Button(
                onClick = onAddNewCollaborator,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Registrar Roster", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
        }

        // Search and Filters Segment
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar colaborador o rol...") },
            modifier = Modifier.fillMaxWidth().testTag("matrix_search_input"),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            }
        )

        // Tab team filters
        ScrollableTabRow(
            selectedTabIndex = teams.indexOf(selectedTeamFilter).coerceAtLeast(0),
            edgePadding = 0.dp,
            divider = {},
            indicator = {},
            modifier = Modifier.fillMaxWidth().height(40.dp)
        ) {
            teams.forEach { team ->
                val isSelected = selectedTeamFilter == team
                Tab(
                    selected = isSelected,
                    onClick = { selectedTeamFilter = team },
                    text = {
                        Text(
                            text = team,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .background(
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        )
                )
            }
        }

        // Matriz / Grid of Collaborators
        if (filteredCollabs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("No se encontraron colaboradores.", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 300.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(filteredCollabs) { colleague ->
                    // Derive dynamic Semaforo & Stats for this colleague
                    val collabEntries = entries.filter { it.collaboratorId == colleague.id }
                    val collabActions = actions.filter { it.collaboratorId == colleague.id }

                    val activeIncidentsCount = collabEntries.count { it.type == "INCIDENT" && it.status != "CLOSED" }
                    val activeHighIncidents = collabEntries.count { 
                        it.type == "INCIDENT" && it.status != "CLOSED" && (it.severity == "HIGH" || it.severity == "CRITICAL") 
                    }
                    val openActionsCount = collabActions.count { it.status in listOf("PENDING", "IN_PROGRESS", "BLOCKED") }
                    val blockedActionsCount = collabActions.count { it.status == "BLOCKED" }

                    val totalGoodCount = collabEntries.count { it.type == "GOOD" }
                    val totalBadCount = collabEntries.count { it.type == "BAD" }

                    // Color semáforo logic:
                    // RED: high/critical incident OR blocked action item.
                    // YELLOW: other open incident OR other pending actions.
                    // GREEN: everything resolved or 0 incidents/actions.
                    val (trafficLightColor, trafficLightLabel) = when {
                        activeHighIncidents > 0 || blockedActionsCount > 0 -> Color(0xFFEF4444) to "Critico 🔴"
                        activeIncidentsCount > 0 || openActionsCount > 0 -> Color(0xFFF59E0B) to "Monitorear 🟡"
                        else -> Color(0xFF10B981) to "Estable 🟢"
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCollaboratorTimeline(colleague) }
                            .testTag("collaborator_card_${colleague.id}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = AssistChipDefaults.assistChipBorder(enabled = true)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Top Profile Portion
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Circular Initial Avatar
                                    Box(
                                        modifier = Modifier
                                            .size(52.dp)
                                            .background(Color(colleague.avatarColor), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = colleague.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                                            color = Color.White,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column {
                                        Text(
                                            text = colleague.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = colleague.role,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = colleague.team,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.SemiBold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                // Semáforo Status Badge
                                Box(
                                    modifier = Modifier
                                        .background(trafficLightColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = trafficLightLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = trafficLightColor,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Counter Pill Badges
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                MiniCounterBadge(label = "⚠️ Inc", count = collabEntries.count { it.type == "INCIDENT" }, color = Color(0xFFEF4444), isAlert = activeIncidentsCount > 0)
                                MiniCounterBadge(label = "🌟 Good", count = totalGoodCount, color = Color(0xFF10B981), isAlert = false)
                                MiniCounterBadge(label = "🛑 Bad", count = totalBadCount, color = Color(0xFFF59E0B), isAlert = false)
                                MiniCounterBadge(label = "📋 Acc", count = collabActions.size, color = Color(0xFF6366F1), isAlert = openActionsCount > 0)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Quick Action Buttons footer
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // View Timeline button
                                Row(
                                    modifier = Modifier
                                        .clickable { onSelectCollaboratorTimeline(colleague) }
                                        .padding(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Timeline,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Ver Historial Timeline",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // Quick Log button
                                TextButton(
                                    onClick = { onAddNewEntryForCollaborator(colleague) },
                                    modifier = Modifier.height(32.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Agregar Entrada", style = MaterialTheme.typography.labelSmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MiniCounterBadge(
    label: String,
    count: Int,
    color: Color,
    isAlert: Boolean
) {
    Box(
        modifier = Modifier
            .background(
                color = if (isAlert) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 6.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Normal,
                color = if (isAlert) color else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .background(color, RoundedCornerShape(4.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
            ) {
                Text(
                    text = "$count",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}
