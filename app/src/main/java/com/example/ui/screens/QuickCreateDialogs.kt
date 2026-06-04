package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.Collaborator
import com.example.data.model.BinnacleEntry
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEntryDialog(
    collaborators: List<Collaborator>,
    preselectedCollaboratorId: Int? = null,
    onDismiss: () -> Unit,
    onSave: (collaboratorId: Int, type: String, title: String, description: String, severity: String, category: String, tags: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("INCIDENT") } // "INCIDENT", "GOOD", "BAD", "OBSERVATION"
    var selectedSeverity by remember { mutableStateOf("MEDIUM") } // "NONE", "LOW", "MEDIUM", "HIGH", "CRITICAL"
    var selectedCategory by remember { mutableStateOf("Técnico") }
    var tagsInput by remember { mutableStateOf("") }
    
    var selectedCollaboratorId by remember { 
        mutableStateOf(preselectedCollaboratorId ?: collaborators.firstOrNull()?.id ?: 0) 
    }

    var colExpanded by remember { mutableStateOf(false) }
    var catExpanded by remember { mutableStateOf(false) }

    val categories = listOf("Técnico", "Puntualidad", "Rendimiento", "Trabajo en Equipo", "Procesos", "Seguridad", "Feedback")
    val types = listOf(
        "INCIDENT" to "Incidente ⚠️",
        "GOOD" to "Cosa Buena 🌟",
        "BAD" to "Cosa Mala 🛑",
        "OBSERVATION" to "Observación 📝"
    )
    val severities = listOf("NONE", "LOW", "MEDIUM", "HIGH", "CRITICAL")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("create_entry_dialog"),
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
                    Text(
                        text = "Registrar Bitácora",
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                // Selector Tipo de Registro
                Text("Tipo de entrada", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    types.forEach { (typeKey, typeLabel) ->
                        val isSelected = selectedType == typeKey
                        FilterChip(
                            selected = isSelected,
                            onClick = { 
                                selectedType = typeKey
                                if (typeKey == "GOOD") selectedSeverity = "NONE"
                            },
                            label = { Text(typeLabel, style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.weight(1f, fill = false)
                        )
                    }
                }

                // Collaborator dropdown selection
                Text("Seleccionar Colaborador *", style = MaterialTheme.typography.titleSmall)
                Box(modifier = Modifier.fillMaxWidth()) {
                    val currentCollab = collaborators.find { it.id == selectedCollaboratorId }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .clickable { colExpanded = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp)
                            .testTag("collaborator_dropdown_trigger"),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentCollab?.name ?: "Elegir colaborador...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                    }

                    DropdownMenu(
                        expanded = colExpanded,
                        onDismissRequest = { colExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        collaborators.forEach { colleague ->
                            DropdownMenuItem(
                                text = { Text("${colleague.name} (${colleague.role})") },
                                onClick = {
                                    selectedCollaboratorId = colleague.id
                                    colExpanded = false
                                }
                            )
                        }
                    }
                }

                // Title Input
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Título / Resumen corto *") },
                    placeholder = { Text("Ej: Error de compilación en pipeline") },
                    modifier = Modifier.fillMaxWidth().testTag("entry_title_input"),
                    singleLine = true
                )

                // Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción / Acciones tomadas *") },
                    placeholder = { Text("Describir detalles, impactos o circunstancias del registro...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("entry_desc_input")
                )

                // Category selection dropdown
                Text("Categoría", style = MaterialTheme.typography.titleSmall)
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .clickable { catExpanded = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = selectedCategory, style = MaterialTheme.typography.bodyMedium)
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                    }

                    DropdownMenu(
                        expanded = catExpanded,
                        onDismissRequest = { catExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        categories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category) },
                                onClick = {
                                    selectedCategory = category
                                    catExpanded = false
                                }
                            )
                        }
                    }
                }

                // Severity selector (Not applicable if GOOD)
                if (selectedType != "GOOD") {
                    Text("Incomodidad / Severidad", style = MaterialTheme.typography.titleSmall)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        severities.forEach { sev ->
                            val isSelected = selectedSeverity == sev
                            val chipColors = when (sev) {
                                "CRITICAL" -> AssistChipDefaults.assistChipColors(
                                    containerColor = if (isSelected) Color(0xFFFFDAD6) else Color.Transparent,
                                    labelColor = if (isSelected) Color(0xFF410002) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                "HIGH" -> AssistChipDefaults.assistChipColors(
                                    containerColor = if (isSelected) Color(0xFFFFECE9) else Color.Transparent,
                                    labelColor = if (isSelected) Color(0xFFBA1A1A) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                "MEDIUM" -> AssistChipDefaults.assistChipColors(
                                    containerColor = if (isSelected) Color(0xFFFFF1C5) else Color.Transparent,
                                    labelColor = if (isSelected) Color(0xFF8B6B00) else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                else -> AssistChipDefaults.assistChipColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
                                    labelColor = if (isSelected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            AssistChip(
                                onClick = { selectedSeverity = sev },
                                label = { Text(sev, style = MaterialTheme.typography.bodySmall) },
                                colors = chipColors,
                                border = if (isSelected) null else AssistChipDefaults.assistChipBorder(enabled = true)
                            )
                        }
                    }
                }

                // Tags input
                OutlinedTextField(
                    value = tagsInput,
                    onValueChange = { tagsInput = it },
                    label = { Text("Etiquetas (Separadas por comas)") },
                    placeholder = { Text("k8s, docker, pipeline, api, delay") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Tag, contentDescription = null) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank() && description.isNotBlank() && selectedCollaboratorId != 0) {
                                onSave(
                                    selectedCollaboratorId,
                                    selectedType,
                                    title,
                                    description,
                                    selectedSeverity,
                                    selectedCategory,
                                    tagsInput
                                )
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank() && description.isNotBlank() && selectedCollaboratorId != 0,
                        modifier = Modifier.testTag("save_entry_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Registrar")
                    }
                }
            }
        }
    }
}

@Composable
fun CreateActionDialog(
    preselectedEntryId: Int,
    preselectedCollaboratorId: Int,
    preselectedCollaboratorName: String,
    onDismiss: () -> Unit,
    onSave: (title: String, description: String, responsibleName: String, dueDate: Long, priority: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var responsibleName by remember { mutableStateOf(preselectedCollaboratorName) }
    var priority by remember { mutableStateOf("MEDIUM") } // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    
    // Choose active target date (default to 3 days from now)
    var daysSlider by remember { mutableFloatStateOf(3f) }

    val priorities = listOf("LOW", "MEDIUM", "HIGH", "CRITICAL")

    Dialog(onDismissRequest = onDismiss) {
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
                    Text(text = "Nueva Acción Administrativa", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }

                Text(
                    text = "Asignado a: $preselectedCollaboratorName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Acción de Mejora / Tarea correctiva *") },
                    placeholder = { Text("Ej: Re-entrenar proceso de cifrado") },
                    modifier = Modifier.fillMaxWidth().testTag("action_title_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Detalles del plan / Criterio de aceptación *") },
                    placeholder = { Text("Ej: Crear manual, impartir sesión técnica a la célula de QA y guardar grabación del taller...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp).testTag("action_desc_input")
                )

                OutlinedTextField(
                    value = responsibleName,
                    onValueChange = { responsibleName = it },
                    label = { Text("Nombre del Responsable Ejecución") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                )

                // Priority
                Text("Prioridad", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    priorities.forEach { prio ->
                        val isSelected = priority == prio
                        val chipColors = when (prio) {
                            "CRITICAL" -> FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFDAD6),
                                selectedLabelColor = Color(0xFF410002)
                            )
                            "HIGH" -> FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFECE9),
                                selectedLabelColor = Color(0xFFBA1A1A)
                            )
                            else -> FilterChipDefaults.filterChipColors()
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { priority = prio },
                            label = { Text(prio) },
                            colors = chipColors
                        )
                    }
                }

                // Slider days to due date
                val targetCalendar = Calendar.getInstance()
                targetCalendar.add(Calendar.DAY_OF_YEAR, daysSlider.toInt())
                val dueDateInMillis = targetCalendar.timeInMillis
                val formattedDueDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(targetCalendar.time)

                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Días estimados para resolver", style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = "${daysSlider.toInt()} días ($formattedDueDate)",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = daysSlider,
                        onValueChange = { daysSlider = it },
                        valueRange = 1f..30f,
                        steps = 29
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if (title.isNotBlank() && description.isNotBlank()) {
                                onSave(title, description, responsibleName, dueDateInMillis, priority)
                                onDismiss()
                            }
                        },
                        enabled = title.isNotBlank() && description.isNotBlank(),
                        modifier = Modifier.testTag("save_action_button")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Asignar Acción")
                    }
                }
            }
        }
    }
}

@Composable
fun CreateCollaboratorDialog(
    onDismiss: () -> Unit,
    onSave: (name: String, role: String, team: String, email: String, avatarColor: Int) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var team by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    
    val teams = listOf("Core Microservices", "Canales Digitales", "Aseguramiento Calidad", "Infraestructura Cloud", "Experiencia de Usuario", "Facilitación Agilidad", "Plataforma de Datos", "Célula Seguridad")
    var teamExpanded by remember { mutableStateOf(false) }

    val colors = listOf(
        0xFF6366F1, // Indigo
        0xFFEF4444, // Red
        0xFFF59E0B, // Amber
        0xFF10B981, // Emerald
        0xFF3B82F6, // Blue
        0xFFEC4899, // Pink
        0xFF8B5CF6, // Purple
        0xFF14B8A6  // Teal
    )
    var selectedColorIndex by remember { mutableStateOf(0) }

    Dialog(onDismissRequest = onDismiss) {
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
                    Text(text = "Nuevo Colaborador (R roster)", style = MaterialTheme.typography.titleLarge)
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = null)
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre Completo *") },
                    placeholder = { Text("Ej: Mateo Sánchez") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = role,
                    onValueChange = { role = it },
                    label = { Text("Rol / Cargo *") },
                    placeholder = { Text("Ej: Lead Architect, Product Owner") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Team selector dropdown
                Text("Área / Equipo *", style = MaterialTheme.typography.titleSmall)
                Box(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .clickable { teamExpanded = true }
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (team.isEmpty()) "Seleccionar Equipo..." else team,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = "Expandir")
                    }

                    DropdownMenu(
                        expanded = teamExpanded,
                        onDismissRequest = { teamExpanded = false },
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        teams.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    team = t
                                    teamExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo Institucional *") },
                    placeholder = { Text("mateo.sanchez@empresa.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                )

                // Color selector
                Text("Color de Avatar", style = MaterialTheme.typography.titleSmall)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    colors.forEachIndexed { idx, colorInt ->
                        val isSelected = selectedColorIndex == idx
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(colorInt), RoundedCornerShape(18.dp))
                                .clickable { selectedColorIndex = idx },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Button(
                        onClick = {
                            if (name.isNotBlank() && role.isNotBlank() && team.isNotBlank() && email.isNotBlank()) {
                                onSave(name, role, team, email, colors[selectedColorIndex].toInt())
                                onDismiss()
                            }
                        },
                        enabled = name.isNotBlank() && role.isNotBlank() && team.isNotBlank() && email.isNotBlank()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ingresar")
                    }
                }
            }
        }
    }
}
