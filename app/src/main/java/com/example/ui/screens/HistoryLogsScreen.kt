package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AuditLog
import com.example.data.model.BinnacleEntry
import com.example.data.model.Collaborator
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryLogsScreen(
    collaborators: List<Collaborator>,
    entries: List<BinnacleEntry>,
    auditLogs: List<AuditLog>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedTeam: String,
    onTeamChange: (String) -> Unit,
    selectedCategory: String,
    onCategoryChange: (String) -> Unit,
    selectedType: String,
    onTypeChange: (String) -> Unit,
    selectedSeverity: String,
    onSeverityChange: (String) -> Unit,
    onExportReport: () -> String,
    exportLogMessage: String?,
    onClearExportMessage: () -> Unit,
    onResetDatabase: () -> Unit
) {
    val context = LocalContext.current
    var isAuditCollapsed by remember { mutableStateOf(true) }
    var isExportDialogShowing by remember { mutableStateOf(false) }
    var compiledReportText by remember { mutableStateOf("") }

    val teams = listOf("Todos") + collaborators.map { it.team }.distinct().sorted()
    val categories = listOf("Todas", "Técnico", "Puntualidad", "Rendimiento", "Trabajo en Equipo", "Procesos", "Seguridad")
    val types = listOf("Todos", "INCIDENT", "GOOD", "BAD", "OBSERVATION")
    val severities = listOf("Todas", "NONE", "LOW", "MEDIUM", "HIGH", "CRITICAL")

    var teamExpanded by remember { mutableStateOf(false) }
    var catExpanded by remember { mutableStateOf(false) }
    var typeExpanded by remember { mutableStateOf(false) }
    var sevExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title block
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Bitácora de Eventos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Buscador global, auditoría de cambios y exportación de hojas",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                // Export Icon trigger
                IconButton(
                    onClick = {
                        compiledReportText = onExportReport()
                        isExportDialogShowing = true
                    },
                    modifier = Modifier.testTag("export_report_button")
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = "Exportar Reporte")
                }

                // Reset DB option
                IconButton(onClick = onResetDatabase) {
                    Icon(Icons.Default.SettingsBackupRestore, contentDescription = "Reiniciar Datos")
                }
            }
        }

        // Search text box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Buscar por título, contenido, etiquetas o nombres...") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.FilterList, contentDescription = null) },
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(Icons.Default.Clear, contentDescription = null)
                    }
                }
            }
        )

        // Dropdown filters Row grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Type dropdown
            Box(modifier = Modifier.weight(1f)) {
                Button(
                    onClick = { typeExpanded = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                ) {
                    Text(text = "Tipo: $selectedType", fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                DropdownMenu(expanded = typeExpanded, onDismissRequest = { typeExpanded = false }) {
                    types.forEach { t ->
                        DropdownMenuItem(
                            text = { Text(t, fontSize = 12.sp) },
                            onClick = { onTypeChange(t); typeExpanded = false }
                        )
                    }
                }
            }

            // Severity dropdown
            Box(modifier = Modifier.weight(1f)) {
                Button(
                    onClick = { sevExpanded = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                ) {
                    Text(text = "Prio: $selectedSeverity", fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                DropdownMenu(expanded = sevExpanded, onDismissRequest = { sevExpanded = false }) {
                    severities.forEach { s ->
                        DropdownMenuItem(
                            text = { Text(s, fontSize = 12.sp) },
                            onClick = { onSeverityChange(s); sevExpanded = false }
                        )
                    }
                }
            }

            // Category dropdown
            Box(modifier = Modifier.weight(1.2f)) {
                Button(
                    onClick = { catExpanded = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                ) {
                    Text(text = "Cat: $selectedCategory", fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                DropdownMenu(expanded = catExpanded, onDismissRequest = { catExpanded = false }) {
                    categories.forEach { c ->
                        DropdownMenuItem(
                            text = { Text(c, fontSize = 12.sp) },
                            onClick = { onCategoryChange(c); catExpanded = false }
                        )
                    }
                }
            }

            // Team dropdown
            Box(modifier = Modifier.weight(1.2f)) {
                Button(
                    onClick = { teamExpanded = true },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.fillMaxWidth().height(32.dp)
                ) {
                    Text(text = "Team: $selectedTeam", fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                DropdownMenu(expanded = teamExpanded, onDismissRequest = { teamExpanded = false }) {
                    teams.forEach { teamName ->
                        DropdownMenuItem(
                            text = { Text(teamName, fontSize = 12.sp) },
                            onClick = { onTeamChange(teamName); teamExpanded = false }
                        )
                    }
                }
            }
        }

        Divider(color = MaterialTheme.colorScheme.outlineVariant)

        // Main listings LazyColumn
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("history_logs_list"),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (entries.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.LightGray)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "No hay registros coincidentes con los filtros activos.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(entries) { entry ->
                    val colleague = collaborators.find { it.id == entry.collaboratorId }
                    val entryTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(entry.timestamp))

                    val entryColor = when (entry.type) {
                        "INCIDENT" -> Color(0xFFEF4444)
                        "GOOD" -> Color(0xFF10B981)
                        "BAD" -> Color(0xFFF59E0B)
                        else -> MaterialTheme.colorScheme.secondary
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        modifier = Modifier.fillMaxWidth(),
                        border = AssistChipDefaults.assistChipBorder(enabled = true)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Top Row: Collab profile & event type indicator
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(colleague?.avatarColor ?: 0xFF6B7280.toInt()), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = colleague?.name?.firstOrNull()?.toString()?.uppercase() ?: "?",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = colleague?.name ?: "N/D",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "(${colleague?.team ?: "Sin área"})",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }

                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(entry.type, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = entryColor) },
                                    modifier = Modifier.height(20.dp)
                                )
                            }

                            // Middle title & description
                            Column {
                                Text(
                                    text = entry.title,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = entry.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Footer meta
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .background(entryColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = "SEV: ${entry.severity}", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = entryColor)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(4.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = entry.category, fontSize = 8.sp, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                    }
                                }

                                Text(
                                    text = "$entryTime | Registra: ${entry.creatorEmail}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 8.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Auditable Change trace collapsible drawer (Audit Log)
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isAuditCollapsed = !isAuditCollapsed },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bitácora de Auditoría (RBAC Trail)",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Icon(
                        imageVector = if (isAuditCollapsed) Icons.Default.ExpandMore else Icons.Default.ExpandLess,
                        contentDescription = "Expandir"
                    )
                }

                AnimatedVisibility(visible = !isAuditCollapsed) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .height(150.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        auditLogs.take(30).forEach { log ->
                            val logTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date(log.timestamp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                                    Text(text = "[$logTime] ", fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    Text(text = log.details, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = log.userEmail,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal report copying dialog (Excel export)
    if (isExportDialogShowing) {
        Dialog(onDismissRequest = { isExportDialogShowing = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Exportar Reporte de Bitácora", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { isExportDialogShowing = false }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }

                    Text(
                        text = "El reporte ha sido normalizado y compilado en formato de hoja de cálculo estructurada (CSV) listo para ser importado en Excel.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Text holder display
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(
                            text = compiledReportText,
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 8.sp
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(onClick = { isExportDialogShowing = false }) {
                            Text("Cerrar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Binnacle Excel Report", compiledReportText)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Reporte copiado con éxito", Toast.LENGTH_SHORT).show()
                                isExportDialogShowing = false
                            },
                            modifier = Modifier.testTag("copy_export_button")
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Copiar Datos")
                        }
                    }
                }
            }
        }
    }
}
