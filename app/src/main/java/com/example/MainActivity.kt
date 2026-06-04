package com.example

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.ReadingEntity
import com.example.data.RoundEntity
import com.example.data.RoundWithReadings
import com.example.data.SupplyConfig
import com.example.data.SupplyDefinition
import com.example.ui.MainViewModel
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppScreen()
            }
        }
    }
}

// Bottom Tab Definitions
enum class AppTab(val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    DASHBOARD("BI en Vivo", Icons.Default.Home),
    NEW_ROUND("Nueva Ronda", Icons.Default.Create),
    HISTORY("Historial", Icons.Default.List)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MainViewModel = viewModel()) {
    var selectedTab by remember { mutableStateOf(AppTab.DASHBOARD) }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Observe DB lists
    val latestRound by viewModel.latestRound.collectAsStateWithLifecycle()
    val allRounds by viewModel.allRounds.collectAsStateWithLifecycle()

    val saveSuccess by viewModel.saveSuccess.collectAsStateWithLifecycle()
    val saveError by viewModel.saveError.collectAsStateWithLifecycle()

    // Show save events
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) {
            Toast.makeText(context, "¡Ronda guardada correctamente!", Toast.LENGTH_SHORT).show()
            selectedTab = AppTab.DASHBOARD // Direct them to see the dashboard refresh
        }
    }

    LaunchedEffect(saveError) {
        saveError?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Brush.linearGradient(listOf(Color(0xFF144D94), Color(0xFF009688)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "V",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Column {
                            Text(
                                "VIJOSA",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF144D94),
                                letterSpacing = 2.sp
                            )
                            Text(
                                "Insumos Críticos Farma",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            latestRound?.let {
                                val textReport = viewModel.generateTeamsMessage(it)
                                clipboardManager.setText(AnnotatedString(textReport))
                                Toast.makeText(context, "Reporte copiado para pegar en Teams", Toast.LENGTH_SHORT).show()

                                // Open share intent as fallback
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "Registro Suministros Críticos")
                                    putExtra(Intent.EXTRA_TEXT, textReport)
                                }
                                try {
                                    context.startActivity(Intent.createChooser(shareIntent, "Compartir reporte en Teams"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Sugerencia: Pegue el reporte (ya copiado) manualmente en Teams.", Toast.LENGTH_LONG).show()
                                }
                            } ?: run {
                                Toast.makeText(context, "No hay ningún reporte registrado aún", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.testTag("share_top_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Compartir reporte a Teams",
                            tint = Color(0xFF144D94)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF144D94)
                ),
                modifier = Modifier.testTag("main_app_bar")
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp,
                windowInsets = WindowInsets.navigationBars,
                modifier = Modifier
                    .testTag("bottom_nav")
                    .height(68.dp)
            ) {
                AppTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.title,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        label = {
                            Text(
                                text = tab.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF144D94),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            selectedTextColor = Color(0xFF144D94),
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            indicatorColor = Color(0xFFE8F5E9)
                        ),
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedTab) {
                AppTab.DASHBOARD -> DashboardPage(latestRound, viewModel)
                AppTab.NEW_ROUND -> NewRoundPage(viewModel)
                AppTab.HISTORY -> HistoryPage(allRounds, viewModel)
            }
        }
    }
}

// ----------------------------------------------------------------------------
// DASHBOARD VIEW (LIVE BI STATUS)
// ----------------------------------------------------------------------------
@Composable
fun DashboardPage(latestRound: RoundWithReadings?, viewModel: MainViewModel) {
    var selectedCategoryFilter by remember { mutableStateOf("Todos") }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    if (latestRound == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "Sin registros",
                tint = Color(0xFF144D94).copy(alpha = 0.4f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No hay registros disponibles",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF144D94)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Realice la primera ronda de turnos con el botón 'Nueva Ronda' para inicializar el Dashboard BI en vivo.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )
        }
    } else {
        val totalItems = SupplyConfig.definitions.size
        val alertsCount = latestRound.readings.count { it.isAlerted }
        val okCount = totalItems - alertsCount
        val progressPct = (okCount.toFloat() / totalItems.toFloat())

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Live Status Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFF144D94).copy(alpha = 0.12f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Estatus de Suministros",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF144D94)
                                )
                                Text(
                                    text = "Último turno: ${viewModel.formatDate(latestRound.round.timestamp)}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // Colored indicator dot
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (alertsCount > 0) Color(0xFFFFEBEE) else Color(0xFFE8F5E9))
                                    .padding(vertical = 4.dp, horizontal = 12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (alertsCount > 0) Color(0xFFD32F2F) else Color(0xFF2E7D32))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (alertsCount > 0) "$alertsCount Alertas" else "Todo OK",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 12.sp,
                                        color = if (alertsCount > 0) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Progress stats
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Disponibilidad en Planta: ${okCount}/${totalItems} Suministros OK",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${(progressPct * 100).toInt()}%",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF144D94)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Custom Progress bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color(0xFFEEEEEE))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progressPct)
                                    .fillMaxHeight()
                                    .clip(RoundedCornerShape(5.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                if (progressPct < 0.8f) Color(0xFFD32F2F) else Color(0xFF00796B),
                                                if (progressPct < 0.8f) Color(0xFFF57C00) else Color(0xFF009688)
                                            )
                                        )
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Metadata line
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Técnico: ${latestRound.round.technicianName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Grupo Turno: ${latestRound.round.shift}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Vapor: ${latestRound.round.steamGenState}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00796B)
                            )
                        }

                        if (latestRound.round.notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Notas de ronda: \"${latestRound.round.notes}\"",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = androidx.compose.ui.text.TextStyle(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            )
                        }
                    }
                }
            }

            // Teams sync callout helper
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
                    border = BorderStroke(1.dp, Color(0xFF3F51B5).copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Teams",
                            tint = Color(0xFF3F51B5),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enviar Reporte a Teams",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A237E)
                            )
                            Text(
                                text = "Copie y envíe esta ronda de insumos directo a su canal de planta en MS Teams.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val textReport = viewModel.generateTeamsMessage(latestRound)
                                clipboardManager.setText(AnnotatedString(textReport))
                                Toast.makeText(context, "¡Reporte copiado con éxito!", Toast.LENGTH_SHORT).show()

                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, textReport)
                                }
                                try {
                                    context.startActivity(Intent.createChooser(shareIntent, "Enviar a Teams"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Sugerencia: Pegue el reporte (ya copiado) manualmente en Teams.", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("teams_share_inner_btn")
                        ) {
                            Text("Compartir", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }

            // Filter category pills
            item {
                Text(
                    text = "Ver por Área de Proceso:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == "Todos",
                            onClick = { selectedCategoryFilter = "Todos" },
                            label = { Text("Todos") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF144D94),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                    items(SupplyConfig.categories) { category ->
                        FilterChip(
                            selected = selectedCategoryFilter == category,
                            onClick = { selectedCategoryFilter = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF144D94),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // Grouped items sorted
            val filteredDefinitions = if (selectedCategoryFilter == "Todos") {
                SupplyConfig.definitions
            } else {
                SupplyConfig.definitions.filter { it.category == selectedCategoryFilter }
            }

            items(filteredDefinitions) { definition ->
                val reading = latestRound.readings.find { it.supplyKey == definition.key }
                val valueStr = reading?.recordedText ?: "---"
                val isAlert = reading?.isAlerted ?: false

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(
                        width = if (isAlert) 1.5.dp else 1.dp,
                        color = if (isAlert) Color(0xFFD32F2F).copy(alpha = 0.5f) else Color(0xFFEEEEEE)
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isAlert) Color(0xFFFFEBEE) else Color(0xFFE8F5E9)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isAlert) Icons.Default.Warning else Icons.Default.Done,
                                        contentDescription = "Status",
                                        tint = if (isAlert) Color(0xFFD32F2F) else Color(0xFF2E7D32),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = definition.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color(0xFF144D94),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = definition.category,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Badge Status
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isAlert) Color(0xFFFFEBEE) else Color(0xFFE8F5E9))
                                    .padding(vertical = 4.dp, horizontal = 10.dp)
                            ) {
                                Text(
                                    text = if (isAlert) "ABA (Abastecer)" else "OK",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isAlert) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "VALOR REGISTRADO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "$valueStr ${definition.unit}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isAlert) Color(0xFFD32F2F) else Color(0xFF121212)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "REGLA DE CONVERGENCIA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = definition.reference,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Max: ${definition.capacity}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// NEW ROUND RECORD TAB (TECHNICIAN FORM)
// ----------------------------------------------------------------------------
@Composable
fun NewRoundPage(viewModel: MainViewModel) {
    val techName by viewModel.technicianName.collectAsState()
    val activeShift by viewModel.shift.collectAsState()
    val steamGenState by viewModel.steamGenState.collectAsState()
    val notes by viewModel.notes.collectAsState()

    val readingsMap by viewModel.readingsMap.collectAsState()
    val replenishCheckMap by viewModel.replenishCheckMap.collectAsState()

    val isSaving by viewModel.isSaving.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    val shifts = listOf("Diurno", "Nocturno", "Mixto")
    val steamStates = listOf("Operando", "En espera", "Fuera de servicio")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("new_round_form")
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Technician header setup card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ronda de Turno Farmacéutico",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF144D94)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = techName,
                        onValueChange = { viewModel.onTechnicianNameChange(it) },
                        label = { Text("Técnico Responsable") },
                        placeholder = { Text("Ej. N. ESQUIVEL") },
                        leadingIcon = { Icon(Icons.Default.Face, contentDescription = "Tec") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("tech_name_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF144D94),
                            focusedLabelColor = Color(0xFF144D94)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Shift selection
                    Text(
                        text = "Turno:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        modifier = Modifier.selectableGroup(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        shifts.forEach { shift ->
                            Row(
                                modifier = Modifier
                                    .selectable(
                                        selected = activeShift == shift,
                                        onClick = { viewModel.onShiftChange(shift) },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = activeShift == shift,
                                    onClick = { viewModel.onShiftChange(shift) },
                                    modifier = Modifier.testTag("shift_radio_$shift")
                                )
                                Text(
                                    text = shift,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Steam state
                    Text(
                        text = "Estado de Generación de Vapor:",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Row(
                        modifier = Modifier.selectableGroup(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        steamStates.forEach { state ->
                            Row(
                                modifier = Modifier
                                    .selectable(
                                        selected = steamGenState == state,
                                        onClick = { viewModel.onSteamStateChange(state) },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = steamGenState == state,
                                    onClick = { viewModel.onSteamStateChange(state) },
                                    modifier = Modifier.testTag("steam_state_$state")
                                )
                                Text(
                                    text = state,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(start = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section Inputs Group
        items(SupplyConfig.definitions) { definition ->
            val value = readingsMap[definition.key] ?: ""
            val isManualChecked = replenishCheckMap[definition.key] ?: false

            // Compute warning live
            val doubleVal = value.toDoubleOrNull()
            val hasWebAlert = definition.checkAlert(doubleVal)
            val isAlertActive = hasWebAlert || isManualChecked

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (isAlertActive) Color(0xFFFFF3E0) else Color.White
                ),
                border = BorderStroke(
                    width = if (isAlertActive) 1.5.dp else 1.dp,
                    color = if (isAlertActive) Color(0xFFF57C00).copy(alpha = 0.5f) else Color(0xFFEEEEEE)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = definition.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF144D94)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Cap: ${definition.capacity} | Ref: ${definition.reference}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = value,
                            onValueChange = { viewModel.onReadingValueChange(definition.key, it) },
                            placeholder = { Text(definition.reference, fontSize = 11.sp) },
                            singleLine = true,
                            trailingIcon = {
                                Text(
                                    definition.unit,
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onSearch = { keyboardController?.hide() }
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_${definition.key}"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF144D94),
                                focusedLabelColor = Color(0xFF144D94)
                            )
                        )

                        // Manual Checkbox "Abastecer"
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isManualChecked) Color(0xFFFF9800).copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { viewModel.onReplenishCheckChange(definition.key, !isManualChecked) }
                                .padding(6.dp)
                        ) {
                            Text("Abastecer?", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isManualChecked) Color(0xFFFF9800) else Color(0xFFEEEEEE)),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isManualChecked) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Si",
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (hasWebAlert) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚠️ Se requiere reabastecimiento automático (≤ Límite Ref)",
                            fontSize = 11.sp,
                            color = Color(0xFFD32F2F),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Notes and Save action card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { viewModel.onNotesChange(it) },
                        label = { Text("Comentarios y Novedades del Turno") },
                        placeholder = { Text("Ej: Caldera operando sin fugas. Oxígeno reabastecido.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(84.dp)
                            .testTag("comments_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF144D94),
                            focusedLabelColor = Color(0xFF144D94)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            keyboardController?.hide()
                            viewModel.saveActiveRound {}
                        },
                        enabled = !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("submit_round_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF144D94)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (isSaving) "Salvando Registro..." else "Guardar Ronda de Turno",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// HISTORY VIEW (PAST ROUNDS LOG)
// ----------------------------------------------------------------------------
@Composable
fun HistoryPage(allRounds: List<RoundWithReadings>, viewModel: MainViewModel) {
    val selectedDetail by viewModel.selectedRoundDetail.collectAsState()
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    if (allRounds.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "No logs",
                tint = Color(0xFF144D94).copy(alpha = 0.4f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Historial vacío",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF144D94)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Complete las rondas de control diarias para ver un historial de nivelación de equipos aquí.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Historial de Rondas Realizadas",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF144D94),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }

            items(allRounds) { roundWithReadings ->
                val round = roundWithReadings.round
                val alertCount = roundWithReadings.readings.count { it.isAlerted }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectHistoryRound(roundWithReadings) }
                        .testTag("history_card_${round.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFEEEEEE)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = viewModel.formatDate(round.timestamp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Color(0xFF144D94)
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (alertCount > 0) Color(0xFFFFEBEE) else Color(0xFFE8F5E9))
                                    .padding(vertical = 2.dp, horizontal = 8.dp)
                            ) {
                                Text(
                                    text = if (alertCount > 0) "$alertCount Alertas" else "Todo OK",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (alertCount > 0) Color(0xFFD32F2F) else Color(0xFF2E7D32)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("TÉCNICO", fontSize = 10.sp, color = Color.Gray)
                                Text(round.technicianName, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text("TURNO", fontSize = 10.sp, color = Color.Gray)
                                Text(round.shift, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column(modifier = Modifier.weight(1.2f)) {
                                Text("GENERACIÓN VAPOR", fontSize = 10.sp, color = Color.Gray)
                                Text(round.steamGenState, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00796B))
                            }
                        }

                        if (round.notes.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = Color(0xFFF5F5F5))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Nota: \"${round.notes}\"",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    val textReport = viewModel.generateTeamsMessage(roundWithReadings)
                                    clipboardManager.setText(AnnotatedString(textReport))
                                    Toast.makeText(context, "Listo. Copiado a portapapeles", Toast.LENGTH_SHORT).show()

                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_TEXT, textReport)
                                    }
                                    try {
                                        context.startActivity(Intent.createChooser(shareIntent, "Compartir reporte en Teams"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Sugerencia: Pegue el reporte (ya copiado) manualmente en Teams.", Toast.LENGTH_LONG).show()
                                    }
                                },
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = "S", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Compartir Teams", fontSize = 11.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            TextButton(
                                onClick = { viewModel.deleteRound(round.id) },
                                colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFD32F2F)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                modifier = Modifier.testTag("delete_round_btn_${round.id}")
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "D", modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Eliminar", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Detail Pop-up overlay dialogue box
    selectedDetail?.let { currentDetail ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { viewModel.selectHistoryRound(null) }
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.85f)
                    .clickable(enabled = false) {}, // absorb clicks to prevent closes
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Detalle de Ronda",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF144D94)
                        )
                        IconButton(onClick = { viewModel.selectHistoryRound(null) }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }

                    HorizontalDivider()

                    Spacer(modifier = Modifier.height(12.dp))

                    // Meta specs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("FECHA / HORA", fontSize = 10.sp, color = Color.Gray)
                            Text(viewModel.formatDate(currentDetail.round.timestamp), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("REGISTRADO POR", fontSize = 10.sp, color = Color.Gray)
                            Text(currentDetail.round.technicianName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("GRUPO TURNO", fontSize = 10.sp, color = Color.Gray)
                            Text(currentDetail.round.shift, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("VAPOR INDUSTRIAL", fontSize = 10.sp, color = Color.Gray)
                            Text(currentDetail.round.steamGenState, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00796B))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(SupplyConfig.definitions) { def ->
                            val reading = currentDetail.readings.find { it.supplyKey == def.key }
                            val valStr = reading?.recordedText ?: "---"
                            val isAlert = reading?.isAlerted ?: false

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isAlert) Color(0xFFFFEBEE) else Color(0xFFF9FBFD))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text(def.name, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF144D94))
                                    Text("Ref: ${def.reference}", fontSize = 10.sp, color = Color.Gray)
                                }
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$valStr ${def.unit}",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = if (isAlert) Color(0xFFD32F2F) else Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isAlert) Color(0xFFD32F2F) else Color(0xFF2E7D32))
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                val textReport = viewModel.generateTeamsMessage(currentDetail)
                                clipboardManager.setText(AnnotatedString(textReport))
                                Toast.makeText(context, "Copiado para Teams", Toast.LENGTH_SHORT).show()

                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, textReport)
                                }
                                try {
                                    context.startActivity(Intent.createChooser(shareIntent, "Enviar a Teams"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Sugerencia: Pegue el reporte (ya copiado) manualmente en Teams.", Toast.LENGTH_LONG).show()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "T", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Teams", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = { viewModel.selectHistoryRound(null) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF144D94)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cerrar", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
