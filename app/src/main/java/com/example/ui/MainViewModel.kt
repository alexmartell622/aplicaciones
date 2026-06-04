package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ReadingEntity
import com.example.data.RoundEntity
import com.example.data.RoundWithReadings
import com.example.data.SupplyConfig
import com.example.data.database.AppDatabase
import com.example.data.repository.SupplyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SupplyRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SupplyRepository(database.supplyDao())
    }

    // Expose all rounds and modern status logs
    val allRounds: StateFlow<List<RoundWithReadings>> = repository.allRounds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val latestRound: StateFlow<RoundWithReadings?> = repository.latestRound
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    // Form states
    private val _technicianName = MutableStateFlow("")
    val technicianName = _technicianName.asStateFlow()

    private val _shift = MutableStateFlow("Diurno") // Diurno, Nocturno, Mixto
    val shift = _shift.asStateFlow()

    private val _steamGenState = MutableStateFlow("En espera") // Operando, En espera, Fuera de servicio
    val steamGenState = _steamGenState.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes = _notes.asStateFlow()

    // Key -> Recorded numerical/textual representation
    private val _readingsMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val readingsMap = _readingsMap.asStateFlow()

    // Key -> Manual checkbox selection for "Needs Replenishment"
    private val _replenishCheckMap = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val replenishCheckMap = _replenishCheckMap.asStateFlow()

    // Action events / UI triggers
    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _saveError = MutableStateFlow<String?>(null)
    val saveError = _saveError.asStateFlow()

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess = _saveSuccess.asStateFlow()

    // Selected historic round for details dialog
    private val _selectedRoundDetail = MutableStateFlow<RoundWithReadings?>(null)
    val selectedRoundDetail = _selectedRoundDetail.asStateFlow()

    fun selectHistoryRound(round: RoundWithReadings?) {
        _selectedRoundDetail.value = round
    }

    fun onTechnicianNameChange(name: String) {
        _technicianName.value = name
    }

    fun onShiftChange(newShift: String) {
        _shift.value = newShift
    }

    fun onSteamStateChange(state: String) {
        _steamGenState.value = state
    }

    fun onReadingValueChange(key: String, value: String) {
        val updated = _readingsMap.value.toMutableMap()
        updated[key] = value
        _readingsMap.value = updated
    }

    fun onReplenishCheckChange(key: String, checked: Boolean) {
        val updated = _replenishCheckMap.value.toMutableMap()
        updated[key] = checked
        _replenishCheckMap.value = updated
    }

    fun onNotesChange(newNotes: String) {
        _notes.value = newNotes
    }

    fun resetForm() {
        _technicianName.value = ""
        _shift.value = "Diurno"
        _steamGenState.value = "En espera"
        _notes.value = ""
        _readingsMap.value = emptyMap()
        _replenishCheckMap.value = emptyMap()
        _saveSuccess.value = false
        _saveError.value = null
    }

    fun saveActiveRound(onComplete: () -> Unit) {
        val techName = _technicianName.value.trim()
        if (techName.isEmpty()) {
            _saveError.value = "Por favor ingrese el nombre del técnico de turno."
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            _saveError.value = null
            try {
                val round = RoundEntity(
                    technicianName = techName,
                    shift = _shift.value,
                    steamGenState = _steamGenState.value,
                    notes = _notes.value
                )

                val readings = SupplyConfig.definitions.map { def ->
                    val rawStr = _readingsMap.value[def.key] ?: ""
                    val parsedVal = rawStr.toDoubleOrNull()

                    // Calculate auto warning
                    val isAutoAlerted = def.checkAlert(parsedVal)
                    val isManualChecked = _replenishCheckMap.value[def.key] ?: false
                    // Alerted if auto limits matched or technician manually flagged it
                    val finalAlert = isAutoAlerted || isManualChecked

                    ReadingEntity(
                        roundId = 0, // Will be set in transaction
                        supplyKey = def.key,
                        recordedValue = parsedVal,
                        recordedText = rawStr.ifEmpty { null },
                        isAlerted = finalAlert,
                        isRepSupplyChecked = isManualChecked
                    )
                }

                repository.insertRoundWithReadings(round, readings)
                _saveSuccess.value = true
                resetForm()
                onComplete()
            } catch (e: Exception) {
                _saveError.value = "Error al guardar el registro: ${e.message}"
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun deleteRound(roundId: Long) {
        viewModelScope.launch {
            repository.deleteRound(roundId)
        }
    }

    // Helper to format timestamps gracefully
    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    // Generates a beautiful markdown-friendly text summary tailored for Teams chat
    fun generateTeamsMessage(roundWithReadings: RoundWithReadings): String {
        val round = roundWithReadings.round
        val readings = roundWithReadings.readings
        val dateString = formatDate(round.timestamp)

        val sb = StringBuilder()
        sb.append("📢 **REGISTRO DIARIO DE SUMINISTROS CRÍTICOS** 📢\n")
        sb.append("**Fecha:** $dateString\n")
        sb.append("**Técnico:** ${round.technicianName}\n")
        sb.append("**Turno:** ${round.shift}\n")
        sb.append("**Generación de Vapor:** ${round.steamGenState}\n")
        if (round.notes.isNotEmpty()) {
            sb.append("**Notas:** ${round.notes}\n")
        }
        sb.append("\n**Suministro - Valor Real - Estatus**\n")
        sb.append("-----------------------------\n")

        SupplyConfig.definitions.forEach { def ->
            val reading = readings.find { it.supplyKey == def.key }
            val valueStr = reading?.recordedText ?: "N/R"
            val statusEmoji = if (reading?.isAlerted == true) "❌ ALERTA (Abastecer)" else "🟢 OK"
            sb.append("- **${def.name}**: $valueStr ${def.unit} | (Cap: ${def.capacity}) -> $statusEmoji\n")
        }

        return sb.toString()
    }
}
