package com.example.data.repository

import com.example.data.local.BinnacleDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.io.Serializable
import java.util.Calendar

class BinnacleRepository(private val binnacleDao: BinnacleDao) {

    val allCollaborators: Flow<List<Collaborator>> = binnacleDao.getAllCollaborators()
    val allEntries: Flow<List<BinnacleEntry>> = binnacleDao.getAllEntries()
    val allActions: Flow<List<ActionItem>> = binnacleDao.getAllActions()
    val allAuditLogs: Flow<List<AuditLog>> = binnacleDao.getAllAuditLogs()

    fun getEntriesByCollaborator(collabId: Int): Flow<List<BinnacleEntry>> =
        binnacleDao.getEntriesByCollaborator(collabId)

    fun getActionsByEntry(entryId: Int): Flow<List<ActionItem>> =
        binnacleDao.getActionsByEntry(entryId)

    fun getActionsByCollaborator(collabId: Int): Flow<List<ActionItem>> =
        binnacleDao.getActionsByCollaborator(collabId)

    suspend fun getCollaboratorById(id: Int): Collaborator? =
        binnacleDao.getCollaboratorById(id)

    suspend fun getEntryById(id: Int): BinnacleEntry? =
        binnacleDao.getEntryById(id)

    suspend fun getActionById(id: Int): ActionItem? =
        binnacleDao.getActionById(id)

    // Inserts with Automatic Audit Log
    suspend fun insertCollaborator(collaborator: Collaborator, actorEmail: String = "valmung622@gmail.com"): Long {
        val id = binnacleDao.insertCollaborator(collaborator)
        binnacleDao.insertAuditLog(
            AuditLog(
                entityType = "COLLABORATOR",
                entityId = id.toInt(),
                userEmail = actorEmail,
                actionType = "CREATE",
                details = "Colaborador creado: ${collaborator.name} (${collaborator.role})"
            )
        )
        return id
    }

    suspend fun insertEntry(entry: BinnacleEntry, actorEmail: String = "valmung622@gmail.com"): Long {
        val id = binnacleDao.insertEntry(entry)
        binnacleDao.insertAuditLog(
            AuditLog(
                entityType = "ENTRY",
                entityId = id.toInt(),
                userEmail = actorEmail,
                actionType = "CREATE",
                details = "Registro creado tipo [${entry.type}]: ${entry.title}"
            )
        )
        return id
    }

    suspend fun insertAction(action: ActionItem, actorEmail: String = "valmung622@gmail.com"): Long {
        val id = binnacleDao.insertAction(action)
        binnacleDao.insertAuditLog(
            AuditLog(
                entityType = "ACTION",
                entityId = id.toInt(),
                userEmail = actorEmail,
                actionType = "CREATE",
                details = "Acción de seguimiento asignada a ${action.responsibleName}: ${action.title}"
            )
        )
        return id
    }

    // Updates with Automatic Audit Log
    suspend fun updateCollaborator(collaborator: Collaborator, actorEmail: String = "valmung622@gmail.com") {
        binnacleDao.updateCollaborator(collaborator)
        binnacleDao.insertAuditLog(
            AuditLog(
                entityType = "COLLABORATOR",
                entityId = collaborator.id,
                userEmail = actorEmail,
                actionType = "UPDATE",
                details = "Colaborador actualizado: ${collaborator.name}"
            )
        )
    }

    suspend fun updateEntry(entry: BinnacleEntry, actorEmail: String = "valmung622@gmail.com") {
        binnacleDao.updateEntry(entry)
        binnacleDao.insertAuditLog(
            AuditLog(
                entityType = "ENTRY",
                entityId = entry.id,
                userEmail = actorEmail,
                actionType = "UPDATE",
                details = "Registro modificado: ${entry.title} (Estado: ${entry.status})"
            )
        )
    }

    suspend fun updateAction(action: ActionItem, actorEmail: String = "valmung622@gmail.com") {
        binnacleDao.updateAction(action)
        binnacleDao.insertAuditLog(
            AuditLog(
                entityType = "ACTION",
                entityId = action.id,
                userEmail = actorEmail,
                actionType = "UPDATE",
                details = "Acción de seguimiento modificada: ${action.title} (Estado: ${action.status})"
            )
        )
    }

    // Deletes with Automatic Audit Log
    suspend fun deleteCollaborator(id: Int, actorName: String, actorEmail: String = "valmung622@gmail.com") {
        val collab = binnacleDao.getCollaboratorById(id)
        if (collab != null) {
            binnacleDao.deleteCollaboratorById(id)
            binnacleDao.insertAuditLog(
                AuditLog(
                    entityType = "COLLABORATOR",
                    entityId = id,
                    userEmail = actorEmail,
                    actionType = "DELETE",
                    details = "Colaborador eliminado: ${collab.name}"
                )
            )
        }
    }

    suspend fun deleteEntry(id: Int, actorEmail: String = "valmung622@gmail.com") {
        val entry = binnacleDao.getEntryById(id)
        if (entry != null) {
            binnacleDao.deleteEntryById(id)
            binnacleDao.insertAuditLog(
                AuditLog(
                    entityType = "ENTRY",
                    entityId = id,
                    userEmail = actorEmail,
                    actionType = "DELETE",
                    details = "Registro eliminado: ${entry.title}"
                )
            )
        }
    }

    suspend fun deleteAction(id: Int, actorEmail: String = "valmung622@gmail.com") {
        val action = binnacleDao.getActionById(id)
        if (action != null) {
            binnacleDao.deleteActionById(id)
            binnacleDao.insertAuditLog(
                AuditLog(
                    entityType = "ACTION",
                    entityId = id,
                    userEmail = actorEmail,
                    actionType = "DELETE",
                    details = "Acción eliminada: ${action.title}"
                )
            )
        }
    }

    // Seeds initial database values if empty
    suspend fun checkAndSeedDatabase() {
        val currentList = allCollaborators.first()
        if (currentList.isNotEmpty()) {
            return // Already seeded
        }

        // 1. Seed Collaborators
        val collabs = listOf(
            Collaborator(name = "Camila Delgado", role = "DevOps Specialist", team = "Infraestructura Cloud", email = "camila.delgado@empresa.com", avatarColor = 0xFF6366F1.toInt()), // Indigo
            Collaborator(name = "Sebastian Ortega", role = "Frontend Tech Lead", team = "Canales Digitales", email = "sebastian.ortega@empresa.com", avatarColor = 0xFFF59E0B.toInt()), // Amber
            Collaborator(name = "María José Castro", role = "QA Analyst Core", team = "Aseguramiento Calidad", email = "mjcastro@empresa.com", avatarColor = 0xFF10B981.toInt()), // Emerald
            Collaborator(name = "Alejandro Ruiz", role = "Backend Developer Sr", team = "Core Microservices", email = "alejandro.ruiz@empresa.com", avatarColor = 0xFF3B82F6.toInt()), // Blue
            Collaborator(name = "Diana Morales", role = "UX/UI Lead Designer", team = "Experiencia de Usuario", email = "diana.morales@empresa.com", avatarColor = 0xFFEC4899.toInt()), // Pink
            Collaborator(name = "Carlos Meneses", role = "Agile Scrum Master", team = "Facilitación Agilidad", email = "carlos.meneses@empresa.com", avatarColor = 0xFF6B7280.toInt()), // Grey
            Collaborator(name = "Sofia Alvarado", role = "Lead Database Admin", team = "Plataforma de Datos", email = "sofia.alvarado@empresa.com", avatarColor = 0xFF8B5CF6.toInt()) // Purple
        )

        val colIds = mutableListOf<Long>()
        collabs.forEach {
            colIds.add(binnacleDao.insertCollaborator(it))
        }

        // 2. Seed some initial Entries (Incidentes, cosas buenas, cosas malas)
        val dummyEntries = listOf(
            BinnacleEntry(
                collaboratorId = colIds[0].toInt(), // Camila (DevOps)
                type = "INCIDENT",
                title = "Caída de Pipeline de Deployment en Producción",
                description = "El pipeline falló durante el despliegue del release v2.4.1 debido a una desincronización de credenciales en Kubernetes.",
                timestamp = System.currentTimeMillis() - 86400000 * 3, // 3 days ago
                severity = "HIGH",
                status = "ONGOING",
                category = "Técnico",
                tags = "k8s,pipeline,infra",
                creatorEmail = "valmung622@gmail.com"
            ),
            BinnacleEntry(
                collaboratorId = colIds[1].toInt(), // Sebastian (Frontend)
                type = "GOOD",
                title = "Reducción de CLS y LCP en Sitio Web Principal",
                description = "Sebastian lideró la refactorización de carga asíncrona de fuentes y optimización de imágenes disminuyendo el LCP a 1.2s.",
                timestamp = System.currentTimeMillis() - 86400000 * 2, // 2 days ago
                severity = "NONE",
                status = "CLOSED",
                category = "Rendimiento",
                tags = "performance,ux,lcp",
                creatorEmail = "valmung622@gmail.com"
            ),
            BinnacleEntry(
                collaboratorId = colIds[2].toInt(), // Maria Jose (QA)
                type = "BAD",
                title = "Demora en Pruebas de Regresión Core",
                description = "Se detectó cuellos de botella en la suite automatizada debido a que la base de datos de pruebas estuvo inactiva por mantenimiento no reportado.",
                timestamp = System.currentTimeMillis() - 86400000 * 4, // 4 days ago
                severity = "MEDIUM",
                status = "CLOSED",
                category = "Procesos",
                tags = "qa,testing,db",
                creatorEmail = "valmung622@gmail.com"
            ),
            BinnacleEntry(
                collaboratorId = colIds[3].toInt(), // Alejandro (Backend)
                type = "INCIDENT",
                title = "Fuga de Memoria en Microservicio Transaccional",
                description = "Alerta de APM disparada por consumo del 98% de heap. Alejandro identificó que un pool de conexiones HTTP no se estaba cerrando tras las llamadas OAuth.",
                timestamp = System.currentTimeMillis() - 86400000 * 1, // 1 day ago
                severity = "CRITICAL",
                status = "OPEN",
                category = "Técnico",
                tags = "fuga,jvm,transaccional",
                creatorEmail = "valmung622@gmail.com"
            ),
            BinnacleEntry(
                collaboratorId = colIds[4].toInt(), // Diana (Designer)
                type = "GOOD",
                title = "Exito en Taller de Co-creación de Diseño con Operaciones",
                description = "Excelente facilitación e involucramiento entre las distintas áreas de tecnología y operaciones para diseñar los flujos de aprobación de flujos internos.",
                timestamp = System.currentTimeMillis() - 86400000 / 2, // 12 hours ago
                severity = "NONE",
                status = "CLOSED",
                category = "Trabajo en Equipo",
                tags = "taller,ux,colaboracion",
                creatorEmail = "valmung622@gmail.com"
            )
        )

        val entryIds = mutableListOf<Long>()
        dummyEntries.forEach {
            entryIds.add(binnacleDao.insertEntry(it))
        }

        // 3. Seed some follow-up Action Items
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 3)
        val in3Days = calendar.timeInMillis
        calendar.add(Calendar.DAY_OF_YEAR, 4)
        val in7Days = calendar.timeInMillis

        val dummyActions = listOf(
            ActionItem(
                entryId = entryIds[0].toInt(), // Caída de pipeline
                collaboratorId = colIds[0].toInt(), // Camila
                title = "Documentar rotación automática de secrets en Vault",
                description = "Crear el playbook en Confluence y configurar cron-job para rotación mensual de tokens K8s.",
                responsibleName = "Camila Delgado",
                createdAt = System.currentTimeMillis() - 86400000 * 3,
                dueDate = in3Days,
                status = "IN_PROGRESS",
                priority = "HIGH"
            ),
            ActionItem(
                entryId = entryIds[2].toInt(), // Demora en regression
                collaboratorId = colIds[2].toInt(), // Maria Jose
                title = "Sincronizar calendario de ventanas de mantenimiento DB",
                description = "Planificar con el equipo de dba las ventanas de parches fuera del horario laboral de QA.",
                responsibleName = "María José Castro",
                createdAt = System.currentTimeMillis() - 86400000 * 4,
                dueDate = in7Days,
                status = "DONE",
                priority = "LOW"
            ),
            ActionItem(
                entryId = entryIds[3].toInt(), // Memoria backend
                collaboratorId = colIds[3].toInt(), // Alejandro
                title = "Implementar Try-With-Resources para clientes HttpClient",
                description = "Revisar y corregir todos los adaptadores de API de terceros para cerrar sockets activamente.",
                responsibleName = "Alejandro Ruiz",
                createdAt = System.currentTimeMillis() - 86400000 * 1,
                dueDate = in3Days,
                status = "PENDING",
                priority = "CRITICAL"
            ),
            ActionItem(
                entryId = entryIds[3].toInt(), // Memoria backend
                collaboratorId = colIds[6].toInt(), // Sofia Alvarado (DBA helping collab)
                title = "Revisión de pools de conexiones en base de datos PostgreSQL",
                description = "Investigar si hay conexiones zombies abiertas que afecten la latencia de respuesta.",
                responsibleName = "Sofia Alvarado",
                createdAt = System.currentTimeMillis() - 86400000 * 1,
                dueDate = in3Days,
                status = "IN_PROGRESS",
                priority = "MEDIUM"
            )
        )

        dummyActions.forEach {
            binnacleDao.insertAction(it)
        }

        // 4. Seed initial audit log entries
        binnacleDao.insertAuditLog(
            AuditLog(
                entityType = "SYSTEM",
                entityId = 0,
                userEmail = "valmung622@gmail.com",
                actionType = "CREATE",
                details = "Inicialización exitosa de base de datos con maestro de colaboradores y bitácora seed."
            )
        )
    }
}
