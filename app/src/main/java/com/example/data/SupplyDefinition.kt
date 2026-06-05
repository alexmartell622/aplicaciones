package com.example.data

data class SupplyDefinition(
    val key: String,
    val name: String,
    val category: String,
    val unit: String,
    val capacity: String,
    val reference: String,
    val placeholder: String = "0.0",
    val plant: String,
    val isNumeric: Boolean = true,
    val checkAlert: (Double?) -> Boolean
)

object SupplyConfig {
    // Categories for Merliot
    const val CAT_INSUMOS_M1 = "Insumos M1 (Prod.)"
    const val CAT_TANQUES_M1 = "Tanques/Cisternas M1"
    const val CAT_INSUMOS_M2 = "Insumos M2 (Críticos)"
    const val CAT_AGUAS_CALDERAS_M2 = "Sala de Aguas M2"
    const val CAT_CISTERNAS_M2 = "Cisternas M2"
    const val CAT_GASES_M2 = "Gases Críticos M2"
    const val CAT_SISTEMAS_COMPRESORES = "Monitoreo y Compresores"

    // Categories for Megaplanta
    const val CAT_COMBUSTIBLE = "Combustible"
    const val CAT_AGUA_SUAVE_CALDERAS = "Agua Suave y Calderas"
    const val CAT_NIVEL_CISTERNAS_MEGA = "Nivel Cisternas"
    const val CAT_GASES_CRITICOS = "Gases Críticos"
    const val CAT_GASES_ESPECIALES = "Gases Especiales"

    val categories = listOf(
        CAT_INSUMOS_M1,
        CAT_TANQUES_M1,
        CAT_INSUMOS_M2,
        CAT_AGUAS_CALDERAS_M2,
        CAT_CISTERNAS_M2,
        CAT_GASES_M2,
        CAT_SISTEMAS_COMPRESORES,
        CAT_COMBUSTIBLE,
        CAT_AGUA_SUAVE_CALDERAS,
        CAT_NIVEL_CISTERNAS_MEGA,
        CAT_GASES_CRITICOS,
        CAT_GASES_ESPECIALES
    )

    val definitions = listOf(
        // ==========================================
        // MERLIOT SITES
        // ==========================================
        
        // Category: Insumos M1 (Prod.) (from Image 2)
        SupplyDefinition(
            key = "MER_SAL",
            name = "SAL EN PELLET",
            category = CAT_INSUMOS_M1,
            unit = "bolsas",
            capacity = "60 bolsas",
            reference = "Abastecer si ≤ 20 Bolsas",
            plant = "Merliot",
            checkAlert = { it != null && it <= 20 }
        ),
        SupplyDefinition(
            key = "MER_SODA",
            name = "HIDROXIDO DE SODIO",
            category = CAT_INSUMOS_M1,
            unit = "% / bidones",
            capacity = "8 bidones",
            reference = "Abastecer si ≤ 50%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MER_HIPOCLORITO",
            name = "HIPOCLORITO DE SODIO",
            category = CAT_INSUMOS_M1,
            unit = "% / bidones",
            capacity = "3 bidones",
            reference = "Abastecer si ≤ 50%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MER_ANTIESCALANTE",
            name = "ANTIESCALANTE",
            category = CAT_INSUMOS_M1,
            unit = "% / bidones",
            capacity = "3 bidones",
            reference = "Abastecer si ≤ 50%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MER_ACIDO_CLOHIDRIC",
            name = "ACIDO CLOHIDRICO",
            category = CAT_INSUMOS_M1,
            unit = "% / bidones",
            capacity = "3 bidones",
            reference = "Abastecer si ≤ 50%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MER_PEROXIDO",
            name = "PEROXIDO DE HIDROGENO",
            category = CAT_INSUMOS_M1,
            unit = "% / bidones",
            capacity = "3 bidones",
            reference = "Abastecer si ≤ 50%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MER_ALOX_ULTRA",
            name = "SANITIZANTE PEROXIDO (ALOX ULTRA)",
            category = CAT_INSUMOS_M1,
            unit = "% / bidones",
            capacity = "3 bidones",
            reference = "Abastecer si ≤ 50%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MER_SAN_ALCALINO",
            name = "SANATIZANTE ALCALINO PARA FILTRO",
            category = CAT_INSUMOS_M1,
            unit = "% / bidones",
            capacity = "3 bidones",
            reference = "Abastecer si ≤ 50%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 50 }
        ),

        // Category: Tanques/Cisternas M1 (from Image 2)
        SupplyDefinition(
            key = "MER_CLORO_DE_TANQUES",
            name = "CLORO (Tanques)",
            category = CAT_TANQUES_M1,
            unit = "unidades",
            capacity = "2 unidades",
            reference = "Abastecer si ≤ 0.5 uni",
            plant = "Merliot",
            checkAlert = { it != null && it <= 0.5 }
        ),
        SupplyDefinition(
            key = "MER_AGUA_POTABLE_ED",
            name = "AGUA POTABLE EDIFICIOS",
            category = CAT_TANQUES_M1,
            unit = "% / m³",
            capacity = "100 m³",
            reference = "Abastecer si ≤ 90%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 90 }
        ),
        SupplyDefinition(
            key = "MER_AGUA_DE_PROCESOS",
            name = "AGUA DE PROCESOS",
            category = CAT_TANQUES_M1,
            unit = "% / m³",
            capacity = "100 m³",
            reference = "Abastecer si ≤ 90%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 90 }
        ),
        SupplyDefinition(
            key = "MER_NIVEL_TANQUE_AP",
            name = "NIVEL TANQUE AP",
            category = CAT_TANQUES_M1,
            unit = "% / Lt",
            capacity = "10,000 Lt",
            reference = "Abastecer si ≤ 90%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 90 }
        ),
        SupplyDefinition(
            key = "MER_NIVEL_TANQUE_API",
            name = "NIVEL TANQUE API",
            category = CAT_TANQUES_M1,
            unit = "% / Lt",
            capacity = "5,000 Lt",
            reference = "Abastecer si ≤ 90%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 90 }
        ),

        // Category: Insumos Críticos M2 (from Image 1)
        SupplyDefinition(
            key = "MER_NIVEL_AP",
            name = "NIVEL AP (M2)",
            category = CAT_INSUMOS_M2,
            unit = "L",
            capacity = "13,000 L",
            reference = "Abastecer si ≤ 8,500 L",
            plant = "Merliot",
            checkAlert = { it != null && it <= 8500 }
        ),
        SupplyDefinition(
            key = "MER_NIVEL_API_M2",
            name = "NIVEL API (M2)",
            category = CAT_INSUMOS_M2,
            unit = "L",
            capacity = "2,000 L",
            reference = "Abastecer si ≤ 1,750 L",
            plant = "Merliot",
            checkAlert = { it != null && it <= 1750 }
        ),
        SupplyDefinition(
            key = "MER_DIESEL",
            name = "DIESEL (M2)",
            category = CAT_INSUMOS_M2,
            unit = "%",
            capacity = "2,000 gal",
            reference = "Abastecer si ≤ 50%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MER_GAS_PROPANO",
            name = "GAS PROPANO (M2)",
            category = CAT_INSUMOS_M2,
            unit = "%",
            capacity = "250 gal",
            reference = "Abastecer si ≤ 30%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 30 }
        ),

        // Category: Sala de Aguas M2 (from Image 1)
        SupplyDefinition(
            key = "MER_ES_620",
            name = "Tratamiento ES-620",
            category = CAT_AGUAS_CALDERAS_M2,
            unit = "%",
            capacity = "1 uni",
            reference = "Abastecer si ≤ 25%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 25 }
        ),
        SupplyDefinition(
            key = "MER_ES_501_M2",
            name = "Tratamiento ES-501",
            category = CAT_AGUAS_CALDERAS_M2,
            unit = "%",
            capacity = "1 uni",
            reference = "Abastecer si ≤ 25%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 25 }
        ),
        SupplyDefinition(
            key = "MER_ES_502",
            name = "Tratamiento ES-502",
            category = CAT_AGUAS_CALDERAS_M2,
            unit = "%",
            capacity = "1 uni",
            reference = "Abastecer si ≤ 25%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 25 }
        ),
        SupplyDefinition(
            key = "MER_HTM_X16E",
            name = "Tratamiento HTM-X16E",
            category = CAT_AGUAS_CALDERAS_M2,
            unit = "%",
            capacity = "1 uni",
            reference = "Abastecer si ≤ 25%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 25 }
        ),
        SupplyDefinition(
            key = "MER_ACIDO_CLOHID_M2",
            name = "ACIDO CLOHIDRICO (M2)",
            category = CAT_AGUAS_CALDERAS_M2,
            unit = "%",
            capacity = "10 uni",
            reference = "Abastecer si ≤ 3 uni",
            plant = "Merliot",
            checkAlert = { it != null && it <= 3 }
        ),
        SupplyDefinition(
            key = "MER_SODA_M2",
            name = "SODA CAUSTICA (M2)",
            category = CAT_AGUAS_CALDERAS_M2,
            unit = "%",
            capacity = "6 uni",
            reference = "Abastecer si ≤ 2 uni",
            plant = "Merliot",
            checkAlert = { it != null && it <= 2 }
        ),
        SupplyDefinition(
            key = "MER_CLORO_M2",
            name = "CLORO (M2)",
            category = CAT_AGUAS_CALDERAS_M2,
            unit = "%",
            capacity = "10 uni",
            reference = "Abastecer si ≤ 50%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MER_SAL_M2",
            name = "SAL (M2)",
            category = CAT_AGUAS_CALDERAS_M2,
            unit = "%",
            capacity = "45 uni",
            reference = "Abastecer si ≤ 10 uni",
            plant = "Merliot",
            checkAlert = { it != null && it <= 10 }
        ),

        // Category: Cisternas M2 (from Image 1)
        SupplyDefinition(
            key = "MER_CIST_SALA_1",
            name = "SALA DE AGUAS 1",
            category = CAT_CISTERNAS_M2,
            unit = "%",
            capacity = "100 m3",
            reference = "Abastecer si ≤ 75 m³",
            plant = "Merliot",
            checkAlert = { it != null && it <= 75 }
        ),
        SupplyDefinition(
            key = "MER_CIST_SALA_2",
            name = "SALA DE AGUAS 2",
            category = CAT_CISTERNAS_M2,
            unit = "%",
            capacity = "100 m3",
            reference = "Abastecer si ≤ 75 m³",
            plant = "Merliot",
            checkAlert = { it != null && it <= 75 }
        ),
        SupplyDefinition(
            key = "MER_CIST_CLINICA",
            name = "CLINICA (Cisterna)",
            category = CAT_CISTERNAS_M2,
            unit = "%",
            capacity = "15 m3",
            reference = "Abastecer si ≤ 10 m³",
            plant = "Merliot",
            checkAlert = { it != null && it <= 10 }
        ),

        // Category: Gases Críticos M2 (from Image 1)
        SupplyDefinition(
            key = "MER_OXI_TERMO_1",
            name = "OXIGENO TERMO 1",
            category = CAT_GASES_M2,
            unit = "%",
            capacity = "50 gal",
            reference = "Abastecer si ≤ 10%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 10 }
        ),
        SupplyDefinition(
            key = "MER_OXI_TERMO_2",
            name = "OXIGENO TERMO 2",
            category = CAT_GASES_M2,
            unit = "%",
            capacity = "50 gal",
            reference = "Abastecer si ≤ 10%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 10 }
        ),
        SupplyDefinition(
            key = "MER_NIT_TERMO_1",
            name = "NITROGENO TERMO 1",
            category = CAT_GASES_M2,
            unit = "%",
            capacity = "175 lb",
            reference = "Abastecer si ≤ 20%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 20 }
        ),
        SupplyDefinition(
            key = "MER_NIT_TERMO_2",
            name = "NITROGENO TERMO 2",
            category = CAT_GASES_M2,
            unit = "%",
            capacity = "175 lb",
            reference = "Abastecer si ≤ 20%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 20 }
        ),
        SupplyDefinition(
            key = "MER_NIT_TERMO_3",
            name = "NITROGENO TERMO 3",
            category = CAT_GASES_M2,
            unit = "%",
            capacity = "175 lb",
            reference = "Abastecer si ≤ 20%",
            plant = "Merliot",
            checkAlert = { it != null && it <= 20 }
        ),
        SupplyDefinition(
            key = "MER_CIL_CO2_M2",
            name = "CILINDROS CO2",
            category = CAT_GASES_M2,
            unit = "%",
            capacity = "3 uni",
            reference = "Abastecer si ≤ 1 uni",
            plant = "Merliot",
            checkAlert = { it != null && it <= 1 }
        ),
        SupplyDefinition(
            key = "MER_CIL_N2_M2",
            name = "CILINDROS N2",
            category = CAT_GASES_M2,
            unit = "%",
            capacity = "3 uni",
            reference = "Abastecer si ≤ 1 uni",
            plant = "Merliot",
            checkAlert = { it != null && it <= 1 }
        ),
        SupplyDefinition(
            key = "MER_CIL_AIRE_ZERO_M2",
            name = "CILINDROS AIRE GRADO CERO",
            category = CAT_GASES_M2,
            unit = "%",
            capacity = "2 uni",
            reference = "Abastecer si ≤ 1 uni",
            plant = "Merliot",
            checkAlert = { it != null && it <= 1 }
        ),
        SupplyDefinition(
            key = "MER_CIL_N2O_M2",
            name = "CILINDROS N2O",
            category = CAT_GASES_M2,
            unit = "%",
            capacity = "3 uni",
            reference = "Abastecer si ≤ 1 uni",
            plant = "Merliot",
            checkAlert = { it != null && it <= 1 }
        ),
        SupplyDefinition(
            key = "MER_CIL_ACETILE_M2",
            name = "CILINDROS ACETILENO ABS",
            category = CAT_GASES_M2,
            unit = "%",
            capacity = "3 uni",
            reference = "Abastecer si ≤ 1 uni",
            plant = "Merliot",
            checkAlert = { it != null && it <= 1 }
        ),
        SupplyDefinition(
            key = "MER_PRESION_NITRO_M2",
            name = "PRESION NITROGENO",
            category = CAT_GASES_M2,
            unit = "psi",
            capacity = "60-80 psi",
            reference = "Fuera de 60-80 psi",
            plant = "Merliot",
            checkAlert = { it != null && (it < 60 || it > 80) }
        ),
        SupplyDefinition(
            key = "MER_PRESION_OXI_M2",
            name = "PRESION OXIGENO",
            category = CAT_GASES_M2,
            unit = "psi",
            capacity = "100-110 psi",
            reference = "Fuera de 100-110 psi",
            plant = "Merliot",
            checkAlert = { it != null && (it < 100 || it > 110) }
        ),

        // Category: Monitoreo y Compresores (from Image 5)
        SupplyDefinition(
            key = "MER_COMP_NIVEL_AP",
            name = "Nivel Tanque de AP",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "L",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_NIVEL_API",
            name = "Nivel Tanque de API",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "L",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_ESTADO_GEN",
            name = "Estado de generación de vapor puro",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "Texto",
            capacity = "Opciones: En espera / Producción",
            reference = "Lectura",
            placeholder = "Ej: En espera",
            plant = "Merliot",
            isNumeric = false,
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_NIT_STORE",
            name = "Nitrógeno (% almacenamiento)",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "%",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_NIT_PRESS",
            name = "Nitrógeno (Presión de dist.)",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "psi",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_OXI_STORE",
            name = "Oxigeno (% almacenamiento)",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "%",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_OXI_PRESS",
            name = "Oxigeno (Presión de dist.)",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "psi",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_LOAD_1",
            name = "Carga compresor 1",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "%",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_PRESS_1",
            name = "Presión compresor 1",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "psi",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_LOAD_2",
            name = "Carga compresor 2",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "%",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_PRESS_2",
            name = "Presión compresor 2",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "psi",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_PRESS_SX75",
            name = "Presión compresor SX 7.5 Estado",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "psi",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_PRESS_SK15",
            name = "Presión compresor SK 15 Estado",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "psi",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_PRESS_MANUFILL",
            name = "Presión manufill kaeser",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "psi",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_PRESS_VAP_IND",
            name = "Presión vapor industrial",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "bar",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_REG_LPG_PES02",
            name = "Regulación LPG PES02",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "mbar",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "MER_COMP_REG_LPG_PES03",
            name = "Regulación LPG PES03",
            category = CAT_SISTEMAS_COMPRESORES,
            unit = "mbar",
            capacity = "Registral",
            reference = "Lectura",
            plant = "Merliot",
            checkAlert = { false }
        ),

        // ==========================================
        // MEGAPLANTA SITES
        // ==========================================

        // Category: Combustible (Megaplanta)
        SupplyDefinition(
            key = "MEGA_DIESEL_PRINCIPAL",
            name = "DIESEL TANQUE PRINCIPAL",
            category = CAT_COMBUSTIBLE,
            unit = "% / gal",
            capacity = "2,000 gal",
            reference = "Abastecer si ≤ 50%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MEGA_DIESEL_INCENDIOS",
            name = "DIESEL TANQUE CONTRA INCENDIOS",
            category = CAT_COMBUSTIBLE,
            unit = "% / gal",
            capacity = "120 gal",
            reference = "Abastecer si ≤ 75%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 75 }
        ),
        SupplyDefinition(
            key = "MEGA_DIESEL_CALDERA",
            name = "DIESEL TANQUE CALDERA",
            category = CAT_COMBUSTIBLE,
            unit = "% / gal",
            capacity = "180 gal",
            reference = "Abastecer si ≤ 50%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MEGA_LPG_11000",
            name = "LPG (Tanque de 11k L)",
            category = CAT_COMBUSTIBLE,
            unit = "%",
            capacity = "90%",
            reference = "Abastecer si ≤ 50%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 50 }
        ),

        // Category: Agua Suave y Calderas (Megaplanta)
        SupplyDefinition(
            key = "MEGA_ES_500",
            name = "ES - 500 (Secuestrante Oxígeno)",
            category = CAT_AGUA_SUAVE_CALDERAS,
            unit = "% / uni",
            capacity = "2 uni",
            reference = "Abastecer si ≤ 25%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 25 }
        ),
        SupplyDefinition(
            key = "MEGA_CAP_375",
            name = "CAP 375 (Triamina Carbonico)",
            category = CAT_AGUA_SUAVE_CALDERAS,
            unit = "% / uni",
            capacity = "2 uni",
            reference = "Abastecer si ≤ 25%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 25 }
        ),
        SupplyDefinition(
            key = "MEGA_ES_610",
            name = "ES - 610 (Floculante Lodos)",
            category = CAT_AGUA_SUAVE_CALDERAS,
            unit = "% / uni",
            capacity = "2 uni",
            reference = "Abastecer si ≤ 25%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 25 }
        ),
        SupplyDefinition(
            key = "MEGA_ES_501",
            name = "ES - 501 (Antiincrustante Caldera)",
            category = CAT_AGUA_SUAVE_CALDERAS,
            unit = "% / uni",
            capacity = "2 uni",
            reference = "Abastecer si ≤ 25%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 25 }
        ),
        SupplyDefinition(
            key = "MEGA_SAL_MEZANINE",
            name = "SAL (Mezanine sala tratamiento)",
            category = CAT_AGUA_SUAVE_CALDERAS,
            unit = "uni",
            capacity = "10 uni",
            reference = "Abastecer si ≤ 5 uni",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 5 }
        ),

        // Category: Nivel Cisternas (Megaplanta)
        SupplyDefinition(
            key = "MEGA_HIPOCLORITO_CIST",
            name = "HIPOCLORITO DE SODIO (Cisterna)",
            category = CAT_NIVEL_CISTERNAS_MEGA,
            unit = "% / uni",
            capacity = "2 uni",
            reference = "Abastecer si ≤ 50%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MEGA_TANQUE_CLOR_POT",
            name = "TANQUE CLORACIÓN (Potable)",
            category = CAT_NIVEL_CISTERNAS_MEGA,
            unit = "%",
            capacity = "100%",
            reference = "Abastecer si ≤ 50%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MEGA_TANQUE_CLOR_PROC",
            name = "TANQUE CLORACIÓN (Procesos)",
            category = CAT_NIVEL_CISTERNAS_MEGA,
            unit = "%",
            capacity = "100%",
            reference = "Abastecer si ≤ 50%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MEGA_AGUA_POTABLE_ED",
            name = "AGUA POTABLE EDIFICIOS (Cist)",
            category = CAT_NIVEL_CISTERNAS_MEGA,
            unit = "% / m³",
            capacity = "410 m³",
            reference = "Abastecer si ≤ 90%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 90 }
        ),
        SupplyDefinition(
            key = "MEGA_AGUA_PROCESOS_CIST",
            name = "AGUA DE PROCESOS (Cist)",
            category = CAT_NIVEL_CISTERNAS_MEGA,
            unit = "% / m³",
            capacity = "410 m³",
            reference = "Abastecer si ≤ 90%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 90 }
        ),
        SupplyDefinition(
            key = "MEGA_SISTEMA_CONTRA_INC",
            name = "SISTEMA CONTRA INCENDIOS",
            category = CAT_NIVEL_CISTERNAS_MEGA,
            unit = "% / m³",
            capacity = "410 m³",
            reference = "Abastecer si ≤ 90%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 90 }
        ),
        SupplyDefinition(
            key = "MEGA_SISTEMA_RIEGO",
            name = "SISTEMA DE RIEGO",
            category = CAT_NIVEL_CISTERNAS_MEGA,
            unit = "% / m³",
            capacity = "410 m³",
            reference = "Abastecer si ≤ 90%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 90 }
        ),
        SupplyDefinition(
            key = "MEGA_TANQUE_RESID_PROC",
            name = "TANQUES CAPTACION AGUAS RES.",
            category = CAT_NIVEL_CISTERNAS_MEGA,
            unit = "L",
            capacity = "10,000 L",
            reference = "Sin Alerta (n/a)",
            plant = "Megaplanta",
            checkAlert = { false }
        ),

        // Category: Gases Críticos (Megaplanta)
        SupplyDefinition(
            key = "MEGA_OXI_BULK",
            name = "OXIGENO TANQUE BULK",
            category = CAT_GASES_CRITICOS,
            unit = "% / L",
            capacity = "1000 L",
            reference = "Abastecer si ≤ 50%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MEGA_NIT_BULK",
            name = "NITROGENO TANQUE BULK",
            category = CAT_GASES_CRITICOS,
            unit = "% / L",
            capacity = "1500 L",
            reference = "Abastecer si ≤ 50%",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "MEGA_CIL_OXI_O2",
            name = "CILINDROS OXIGENO O2",
            category = CAT_GASES_CRITICOS,
            unit = "uni",
            capacity = "6 uni",
            reference = "Abastecer si ≤ 3 uni",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 3 }
        ),
        SupplyDefinition(
            key = "MEGA_CIL_NIT_N2",
            name = "CILINDROS NITROGENO N2",
            category = CAT_GASES_CRITICOS,
            unit = "uni",
            capacity = "6 uni",
            reference = "Abastecer si ≤ 3 uni",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 3 }
        ),
        SupplyDefinition(
            key = "MEGA_CIL_CO2",
            name = "CILINDROS CO2",
            category = CAT_GASES_CRITICOS,
            unit = "uni",
            capacity = "6 uni",
            reference = "Abastecer si ≤ 3 uni",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 3 }
        ),
        SupplyDefinition(
            key = "MEGA_PRESION_NITROGENO",
            name = "PRESION NITROGENO",
            category = CAT_GASES_CRITICOS,
            unit = "psi",
            capacity = "50-100psi",
            reference = "Fuera de 50-100 psi",
            plant = "Megaplanta",
            checkAlert = { it != null && (it < 50 || it > 100) }
        ),
        SupplyDefinition(
            key = "MEGA_PRESION_OXIGENO",
            name = "PRESION OXIGENO",
            category = CAT_GASES_CRITICOS,
            unit = "psi",
            capacity = "50-100psi",
            reference = "Fuera de 50-100 psi",
            plant = "Megaplanta",
            checkAlert = { it != null && (it < 50 || it > 100) }
        ),
        SupplyDefinition(
            key = "MEGA_PRESION_CO2_CRIT",
            name = "PRESION DE CO2",
            category = CAT_GASES_CRITICOS,
            unit = "psi",
            capacity = "90-100psi",
            reference = "Fuera de 90-100 psi",
            plant = "Megaplanta",
            checkAlert = { it != null && (it < 90 || it > 100) }
        ),

        // Category: Gases Especiales (Megaplanta)
        SupplyDefinition(
            key = "MEGA_CIL_HELIO",
            name = "CILINDROS HELIO HE",
            category = CAT_GASES_ESPECIALES,
            unit = "uni",
            capacity = "2 uni",
            reference = "Abastecer si ≤ 1 uni",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 1 }
        ),
        SupplyDefinition(
            key = "MEGA_CIL_HIDROGENO",
            name = "CILINDROS HIDROGENO H",
            category = CAT_GASES_ESPECIALES,
            unit = "% / uni",
            capacity = "1 uni",
            reference = "Abastecer si ≤ 50% (0.5 uni)",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 0.5 }
        ),
        SupplyDefinition(
            key = "MEGA_CIL_ACETILE_ABS",
            name = "CILINDROS ACETILENO ABS",
            category = CAT_GASES_ESPECIALES,
            unit = "uni",
            capacity = "4 uni",
            reference = "Abastecer si ≤ 2 uni",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 2 }
        ),
        SupplyDefinition(
            key = "MEGA_CIL_NIT_N2_SPEC",
            name = "CILINDROS NITROGENO N2 (Esp)",
            category = CAT_GASES_ESPECIALES,
            unit = "uni",
            capacity = "2 uni",
            reference = "Abastecer si ≤ 1 uni",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 1 }
        ),
        SupplyDefinition(
            key = "MEGA_CIL_N2O",
            name = "CILINDROS OXIDO NITROSO N2O",
            category = CAT_GASES_ESPECIALES,
            unit = "uni",
            capacity = "4 uni",
            reference = "Abastecer si ≤ 2 uni",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 2 }
        ),
        SupplyDefinition(
            key = "MEGA_CIL_AIRE_ZERO",
            name = "CILINDROS AIRE GRADO CERO",
            category = CAT_GASES_ESPECIALES,
            unit = "uni",
            capacity = "2 uni",
            reference = "Abastecer si ≤ 1 uni",
            plant = "Megaplanta",
            checkAlert = { it != null && it <= 1 }
        )
    )
}
