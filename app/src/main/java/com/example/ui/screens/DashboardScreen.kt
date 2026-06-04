package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActionItem
import com.example.data.model.BinnacleEntry
import com.example.data.model.Collaborator
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    collaborators: List<Collaborator>,
    entries: List<BinnacleEntry>,
    actions: List<ActionItem>,
    onNavigateToMatrix: () -> Unit,
    onNavigateToKanban: () -> Unit
) {
    // Stat Computations
    val totalCollaborators = collaborators.size
    val totalEntries = entries.size
    
    val activeIncidents = entries.count { it.type == "INCIDENT" && it.status != "CLOSED" }
    val openActions = actions.count { it.status in listOf("PENDING", "IN_PROGRESS", "BLOCKED") }
    val goodDeeds = entries.count { it.type == "GOOD" }
    
    // SLA overdue (due date < current and status not strictly DONE or CANCELLED)
    val overdueActions = actions.count {
        it.status != "DONE" && it.status != "CANCELLED" && it.dueDate < System.currentTimeMillis()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
    ) {
        // Welcome Header
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Bitácora Operativa",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Control de incidentes, reconocimientos y compromisos del departamento.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.82f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.Dataset,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }

        // 2x2 Metric Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Cosas Buenas (Logros)
                    MetricCard(
                        title = "Reconocimientos",
                        value = "$goodDeeds",
                        subtext = "Cosas buenas registradas",
                        color = Color(0xFF10B981), // Emerald
                        icon = Icons.Default.Stars,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("kpi_good_deeds")
                    )
                    // Incidentes Activos
                    MetricCard(
                        title = "Incidentes Activos",
                        value = "$activeIncidents",
                        subtext = "En curso o abiertos",
                        color = Color(0xFFEF4444), // Red
                        icon = Icons.Default.Warning,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("kpi_active_incidents")
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Acciones en Seguimiento
                    MetricCard(
                        title = "Acciones Abiertas",
                        value = "$openActions",
                        subtext = "Compromisos pendientes",
                        color = Color(0xFF6366F1), // Indigo
                        icon = Icons.Default.Assignment,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("kpi_open_actions")
                    )
                    // Vencidas / Alerta
                    MetricCard(
                        title = "Alerta SLA",
                        value = "$overdueActions",
                        subtext = "Acciones fuera de plazo",
                        color = if (overdueActions > 0) Color(0xFFF59E0B) else Color(0xFF10B981), // Amber/Green
                        icon = Icons.Default.HourglassDisabled,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("kpi_overdue")
                    )
                }
            }
        }

        // Custom Donut Chart for Registry Types
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = AssistChipDefaults.assistChipBorder(enabled = true)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Balance de Clima y Cumplimiento",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Distribución del tipo de inputs registrados en la bitácora",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Donut Graphic
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            val itemsCount = mapOf(
                                "INCIDENT" to entries.count { it.type == "INCIDENT" },
                                "GOOD" to entries.count { it.type == "GOOD" },
                                "BAD" to entries.count { it.type == "BAD" },
                                "OBSERVATION" to entries.count { it.type == "OBSERVATION" }
                            )
                            val total = itemsCount.values.sum().toFloat()

                            Canvas(modifier = Modifier.fillMaxSize()) {
                                if (total == 0f) {
                                    drawCircle(
                                        color = Color.LightGray,
                                        style = Stroke(width = 20.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                } else {
                                    var startAngle = -90f
                                    val colorsMap = mapOf(
                                        "INCIDENT" to Color(0xFFEF4444),
                                        "GOOD" to Color(0xFF10B981),
                                        "BAD" to Color(0xFFF59E0B),
                                        "OBSERVATION" to Color(0xFF8B5CF6)
                                    )

                                    itemsCount.forEach { (type, count) ->
                                        if (count > 0) {
                                            val sweepAngle = (count / total) * 360f
                                            drawArc(
                                                color = colorsMap[type] ?: Color.Gray,
                                                startAngle = startAngle,
                                                sweepAngle = sweepAngle,
                                                useCenter = false,
                                                style = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
                                            )
                                            startAngle += sweepAngle
                                        }
                                    }
                                }
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${total.toInt()}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                                Text(text = "TOTAL", style = MaterialTheme.typography.labelSmall, fontSize = 8.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        // Chart Legend
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            LegendItem(color = Color(0xFF10B981), label = "Cosas Buenas", count = entries.count { it.type == "GOOD" })
                            LegendItem(color = Color(0xFFEF4444), label = "Incidentes", count = entries.count { it.type == "INCIDENT" })
                            LegendItem(color = Color(0xFFF59E0B), label = "Cosas Malas", count = entries.count { it.type == "BAD" })
                            LegendItem(color = Color(0xFF8B5CF6), label = "Observaciones", count = entries.count { it.type == "OBSERVATION" })
                        }
                    }
                }
            }
        }

        // Top Categories (Proportional Canvas Stack Grid)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = AssistChipDefaults.assistChipBorder(enabled = true)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Frecuencia por Categoría de Operación",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val catCounts = entries.groupBy { it.category }
                        .mapValues { it.value.size }
                        .entries
                        .sortedByDescending { it.value }

                    if (catCounts.isEmpty()) {
                        Text(
                            text = "No hay registros cargados aún.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        val maxCount = catCounts.first().value.toFloat()
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            catCounts.take(4).forEach { (category, count) ->
                                val pct = if (maxCount > 0) count / maxCount else 0f
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = category, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold)
                                        Text(text = "$count entries", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                                    }
                                    
                                    // Custom Linear Progress drawn with Canvas
                                    Canvas(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                    ) {
                                        // Background Track
                                        drawRoundRect(
                                            color = Color.LightGray.copy(alpha = 0.3f),
                                            size = size
                                        )
                                        // Active Bar
                                        drawRoundRect(
                                            color = Color(0xFF6366F1), // Indigo primary
                                            size = Size(width = size.width * pct, height = size.height)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Incident Heatmap Grid (Teams vs Severity)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = AssistChipDefaults.assistChipBorder(enabled = true)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Matriz de Distribución de Severidad",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Incidentes cruzados por Área Operativa vs Severidad",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val teams = listOf("Core Microservices", "Canales Digitales", "Aseguramiento Calidad", "Infraestructura Cloud")
                    val severities = listOf("LOW", "MEDIUM", "HIGH", "CRITICAL")

                    // Heatmap Drawing in a grid arrangement
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Header row
                        Row(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Área / Equipo", style = MaterialTheme.typography.labelSmall, modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold)
                            severities.forEach { sev ->
                                Text(
                                    text = sev.take(4),
                                    style = MaterialTheme.typography.labelSmall,
                                    modifier = Modifier.weight(1f),
                                    fontWeight = FontWeight.Bold,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        teams.forEach { t ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = t.take(15) + "..",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1.5f),
                                    maxLines = 1,
                                    fontWeight = FontWeight.Medium
                                )

                                severities.forEach { sev ->
                                    // Calculate incidents matching
                                    val count = entries.count { entry ->
                                        val collab = collaborators.find { it.id == entry.collaboratorId }
                                        collab?.team == t && entry.severity == sev && entry.type == "INCIDENT"
                                    }

                                    val cellColor = when {
                                        count == 0 -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                        count < 2 -> Color(0xFFFCA5A5).copy(alpha = 0.5f) // light red
                                        count < 4 -> Color(0xFFEF4444).copy(alpha = 0.7f) // red
                                        else -> Color(0xFFB91C1C) // dark red
                                    }

                                    val textColor = if (count > 0) Color.Black else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(2.dp)
                                            .background(cellColor, RoundedCornerShape(4.dp))
                                            .aspectRatio(1.8f),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$count",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = if (count > 0) FontWeight.Bold else FontWeight.Normal,
                                            color = textColor
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recent Outstanding Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Acciones Críticas Abiertas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToKanban) {
                    Text("Ver Tablero")
                }
            }
        }

        val criticalActions = actions.filter { it.status != "DONE" && it.status != "CANCELLED" }
            .sortedByDescending { when(it.priority) { "CRITICAL" -> 4; "HIGH" -> 3; "MEDIUM" -> 2; else -> 1 } }
            .take(3)

        if (criticalActions.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                ) {
                    Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                        Text("No hay acciones abiertas pendientes.", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        } else {
            items(criticalActions) { action ->
                val colleague = collaborators.find { it.id == action.collaboratorId }
                val dateString = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(action.dueDate))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = AssistChipDefaults.assistChipBorder(enabled = true)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val prioColor = when (action.priority) {
                            "CRITICAL" -> Color(0xFFEF4444)
                            "HIGH" -> Color(0xFFF59E0B)
                            "MEDIUM" -> Color(0xFF3B82F6)
                            else -> Color(0xFF10B981)
                        }

                        // Priority dot indicator
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(prioColor, RoundedCornerShape(5.dp))
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = action.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Responsable: ${action.responsibleName} | Asignado a: ${colleague?.name ?: "N/D"}",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        SuggestionChip(
                            onClick = {},
                            label = { Text("Hasta $dateString") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    subtext: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(110.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = AssistChipDefaults.assistChipBorder(enabled = true)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 9.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String, count: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(color, RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodySmall)
        }
        Text(
            text = "$count",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
    }
}
