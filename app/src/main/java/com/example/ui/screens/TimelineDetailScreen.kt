package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionItem
import com.example.data.model.BinnacleEntry
import com.example.data.model.Collaborator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineDetailScreen(
    collaborator: Collaborator,
    entries: List<BinnacleEntry>,
    actions: List<ActionItem>,
    onBack: () -> Unit,
    onAddEntry: () -> Unit,
    onAddActionForEntry: (BinnacleEntry) -> Unit,
    onUpdateActionStatus: (ActionItem, String) -> Unit,
    onUpdateEntryStatus: (BinnacleEntry, String) -> Unit
) {
    val collabEntries = entries.filter { it.collaboratorId == collaborator.id }
    
    val totalIncidents = collabEntries.count { it.type == "INCIDENT" }
    val totalGoods = collabEntries.count { it.type == "GOOD" }
    val totalBads = collabEntries.count { it.type == "BAD" }
    val pendingActions = actions.filter { it.collaboratorId == collaborator.id && it.status != "DONE" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Línea de Tiempo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("timeline_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    Button(
                        onClick = onAddEntry,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Registrar Evento")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Collaborator Hero Header
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color(collaborator.avatarColor), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = collaborator.name.firstOrNull()?.toString()?.uppercase() ?: "?",
                                color = Color.White,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(text = collaborator.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(text = collaborator.role, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = collaborator.team, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(text = collaborator.email, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Brief Metric counts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CollabStatsCount(label = "⚠️ Incidentes", count = totalIncidents, color = Color(0xFFEF4444))
                        CollabStatsCount(label = "🌟 Logros", count = totalGoods, color = Color(0xFF10B981))
                        CollabStatsCount(label = "🛑 Desvíos", count = totalBads, color = Color(0xFFF59E0B))
                        CollabStatsCount(label = "📋 Tareas Abiertas", count = pendingActions.size, color = Color(0xFF6366F1))
                    }
                }
            }

            // Timeline Items List
            if (collabEntries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.HourglassEmpty, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Bitácora vacía para este colaborador.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        TextButton(onClick = onAddEntry) {
                            Text("Registrar el primer evento ahora")
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("timeline_list"),
                    verticalArrangement = Arrangement.spacedBy(0.dp) // Row overlays vertical line
                ) {
                    items(collabEntries) { entry ->
                        TimelineRowItem(
                            entry = entry,
                            actions = actions.filter { it.entryId == entry.id },
                            onAddAction = { onAddActionForEntry(entry) },
                            onUpdateActionStatus = onUpdateActionStatus,
                            onUpdateEntryStatus = { status -> onUpdateEntryStatus(entry, status) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TimelineRowItem(
    entry: BinnacleEntry,
    actions: List<ActionItem>,
    onAddAction: () -> Unit,
    onUpdateActionStatus: (ActionItem, String) -> Unit,
    onUpdateEntryStatus: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("timeline_item_${entry.id}"),
        verticalAlignment = Alignment.Top
    ) {
        // Vertical Line Column with Node Icon
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(48.dp)
                .align(Alignment.Top)
        ) {
            val (nodeColor, nodeIcon) = when (entry.type) {
                "INCIDENT" -> Color(0xFFEF4444) to Icons.Default.Warning
                "GOOD" -> Color(0xFF10B981) to Icons.Default.Stars
                "BAD" -> Color(0xFFF59E0B) to Icons.Default.OfflineBolt
                else -> MaterialTheme.colorScheme.primary to Icons.Default.Description
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Pulse node Circle
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(nodeColor.copy(alpha = 0.15f), CircleShape)
                    .background(nodeColor, CircleShape)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = nodeIcon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }

            // Timeline continuous line drawn down
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .height(160.dp) // static or wrapping height to sustain vertical flow
                    .background(Color.LightGray.copy(alpha = 0.5f))
            )
        }

        // Details Card column
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 10.dp)
        ) {
            val entryBgColor = when (entry.type) {
                "INCIDENT" -> Color(0xFFFFF2F1)
                "GOOD" -> Color(0xFFF0FDF4)
                "BAD" -> Color(0xFFFFFBEB)
                else -> MaterialTheme.colorScheme.surface
            }

            val titleColor = when (entry.type) {
                "INCIDENT" -> Color(0xFFDC2626)
                "GOOD" -> Color(0xFF15803D)
                "BAD" -> Color(0xFFB45309)
                else -> MaterialTheme.colorScheme.onSurface
            }

            Card(
                colors = CardDefaults.cardColors(containerColor = entryBgColor),
                modifier = Modifier.fillMaxWidth(),
                border = AssistChipDefaults.assistChipBorder(enabled = true)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Header title & timestamp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = when (entry.type) {
                                        "INCIDENT" -> "Incidente"
                                        "GOOD" -> "Reconocimiento"
                                        "BAD" -> "Fuga / Desvío"
                                        else -> "Observación"
                                    }.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = titleColor,
                                    letterSpacing = 1.sp
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                if (entry.severity != "NONE") {
                                    val sevColor = when (entry.severity) {
                                        "CRITICAL" -> Color(0xFFB91C1C)
                                        "HIGH" -> Color(0xFFDC2626)
                                        "MEDIUM" -> Color(0xFFD97706)
                                        else -> Color(0xFF4B5563)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(sevColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = entry.severity,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = sevColor
                                        )
                                    }
                                }
                            }

                            Text(
                                text = entry.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = titleColor
                            )
                        }

                        val dateFormatted = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(entry.timestamp))
                        Text(
                            text = dateFormatted,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }

                    // Content text
                    Text(
                        text = entry.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Category and Tags
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Category, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Categoría: ${entry.category}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        // State trigger dropdown / options
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Estado: ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            val statusColors = when (entry.status) {
                                "CLOSED" -> Color(0xFF10B981)
                                "ONGOING" -> Color(0xFF3B82F6)
                                else -> Color(0xFF6B7280)
                            }
                            AssistChip(
                                onClick = {
                                    // Cycles state for MVP demo quickness: OPEN -> ONGOING -> CLOSED -> OPEN
                                    val nextStatus = when (entry.status) {
                                        "OPEN" -> "ONGOING"
                                        "ONGOING" -> "CLOSED"
                                        else -> "OPEN"
                                    }
                                    onUpdateEntryStatus(nextStatus)
                                },
                                label = { Text(entry.status, fontSize = 9.sp, fontWeight = FontWeight.Bold) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = statusColors.copy(alpha = 0.15f),
                                    labelColor = statusColors
                                ),
                                border = null,
                                modifier = Modifier.height(26.dp)
                            )
                        }
                    }

                    // Raw Tags
                    if (entry.tags.isNotBlank()) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            entry.tags.split(",").forEach { t ->
                                val cleanTag = t.trim()
                                if (cleanTag.isNotEmpty()) {
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "#$cleanTag", fontSize = 8.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }

                    // Associated Work tracking checklist (1-to-many relationship)
                    if (actions.isNotEmpty()) {
                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                        Text(
                            text = "Acciones de Seguimiento relacionadas:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        actions.forEach { act ->
                            val isDone = act.status == "DONE"
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    IconButton(
                                        onClick = {
                                            val nextActStatus = if (isDone) "PENDING" else "DONE"
                                            onUpdateActionStatus(act, nextActStatus)
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                                            contentDescription = null,
                                            tint = if (isDone) Color(0xFF10B981) else Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = act.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                                        color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = when (act.status) {
                                                    "BLOCKED" -> Color(0xFFEF4444).copy(alpha = 0.15f)
                                                    "IN_PROGRESS" -> Color(0xFF3B82F6).copy(alpha = 0.15f)
                                                    "DONE" -> Color(0xFF10B981).copy(alpha = 0.15f)
                                                    else -> Color.Gray.copy(alpha = 0.15f)
                                                },
                                                shape = RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = act.status,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (act.status) {
                                                "BLOCKED" -> Color(0xFFEF4444)
                                                "IN_PROGRESS" -> Color(0xFF3B82F6)
                                                "DONE" -> Color(0xFF10B981)
                                                else -> Color.Gray
                                            }
                                        )
                                    }

                                    // Cycle action priority / status option
                                    IconButton(
                                        onClick = {
                                            // Quick slide state
                                            val nextStatus = when (act.status) {
                                                "PENDING" -> "IN_PROGRESS"
                                                "IN_PROGRESS" -> "BLOCKED"
                                                "BLOCKED" -> "DONE"
                                                else -> "PENDING"
                                            }
                                            onUpdateActionStatus(act, nextStatus)
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Cached, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }

                    // Quick Actions Footer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onAddAction,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Añadir Acción Seguimiento", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CollabStatsCount(label: String, count: Int, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "$count", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
        Text(text = label, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
