package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionItem
import com.example.data.model.Collaborator
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun KanbanScreen(
    collaborators: List<Collaborator>,
    actions: List<ActionItem>,
    onUpdateActionStatus: (ActionItem, String) -> Unit
) {
    val kanbanStates = listOf(
        "PENDING" to "Para Hacer 📋",
        "IN_PROGRESS" to "En Progreso ⚡",
        "BLOCKED" to "Bloqueada 🛑",
        "DONE" to "Completada ✅"
    )

    val boardScrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title block
        Column {
            Text(
                text = "Tablero Kanban de Acciones",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Seguimiento y SLA de compromisos y tareas correctivas del departamento",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Horizontal board flow container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .horizontalScroll(boardScrollState)
                .testTag("kanban_board_container"),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            kanbanStates.forEach { (stateKey, columnTitle) ->
                val columnActions = actions.filter { it.status == stateKey }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight(),
                    border = AssistChipDefaults.assistChipBorder(enabled = true)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxSize()
                    ) {
                        // Column title with total count
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = columnTitle,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Badge(
                                containerColor = when (stateKey) {
                                    "PENDING" -> MaterialTheme.colorScheme.outline
                                    "IN_PROGRESS" -> MaterialTheme.colorScheme.primary
                                    "BLOCKED" -> Color(0xFFEF4444)
                                    else -> Color(0xFF10B981)
                                }
                            ) {
                                Text(
                                    text = "${columnActions.size}",
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (columnActions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Sin acciones en esta etapa",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        } else {
                            LazyColumn(
                                verticalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f)
                                    .testTag("kanban_column_$stateKey")
                            ) {
                                items(columnActions, key = { it.id }) { actionItem ->
                                    val assignee = collaborators.find { it.id == actionItem.collaboratorId }
                                    KanbanCard(
                                        action = actionItem,
                                        assignee = assignee,
                                        onMoveLeft = {
                                            val prevStatus = when (stateKey) {
                                                "IN_PROGRESS" -> "PENDING"
                                                "BLOCKED" -> "IN_PROGRESS"
                                                "DONE" -> "BLOCKED"
                                                else -> null
                                            }
                                            if (prevStatus != null) onUpdateActionStatus(actionItem, prevStatus)
                                        },
                                        onMoveRight = {
                                            val nextStatus = when (stateKey) {
                                                "PENDING" -> "IN_PROGRESS"
                                                "IN_PROGRESS" -> "BLOCKED"
                                                "BLOCKED" -> "DONE"
                                                else -> "DONE"
                                            }
                                            onUpdateActionStatus(actionItem, nextStatus)
                                        }
                                    )
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
fun KanbanCard(
    action: ActionItem,
    assignee: Collaborator?,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit
) {
    val isOverdue = action.status != "DONE" && action.dueDate < System.currentTimeMillis()
    val dateString = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(action.dueDate))

    val prioColor = when (action.priority) {
        "CRITICAL" -> Color(0xFFEF4444)
        "HIGH" -> Color(0xFFF59E0B)
        "MEDIUM" -> Color(0xFF3B82F6)
        else -> Color(0xFF10B981)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("kanban_card_${action.id}"),
        border = AssistChipDefaults.assistChipBorder(enabled = true)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Task priority & overdue warning
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(prioColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "PRIO: ${action.priority}",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = prioColor
                    )
                }

                if (isOverdue) {
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFEE2E2), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "VENCIDO ⚠️",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB91C1C)
                        )
                    }
                }
            }

            // Task content
            Text(
                text = action.title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = action.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Assignee & Dates details
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Resp: ${action.responsibleName}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                if (assignee != null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Group, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Cód: ${assignee.name}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(12.dp), tint = if (isOverdue) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Vence: $dateString",
                        fontSize = 10.sp,
                        fontWeight = if (isOverdue) FontWeight.Bold else FontWeight.Normal,
                        color = if (isOverdue) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            // Move actions footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val hasLeft = action.status != "PENDING"
                IconButton(
                    onClick = onMoveLeft,
                    enabled = hasLeft,
                    modifier = Modifier.size(32.dp).testTag("kanban_move_left_${action.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Mover izquierda",
                        tint = if (hasLeft) MaterialTheme.colorScheme.primary else Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                val hasRight = action.status != "DONE"
                IconButton(
                    onClick = onMoveRight,
                    enabled = hasRight,
                    modifier = Modifier.size(32.dp).testTag("kanban_move_right_${action.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Mover derecha",
                        tint = if (hasRight) MaterialTheme.colorScheme.primary else Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
