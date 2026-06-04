package com.example.data

data class SupplyDefinition(
    val key: String,
    val name: String,
    val category: String,
    val unit: String,
    val capacity: String,
    val reference: String,
    val placeholder: String = "0.0",
    val checkAlert: (Double?) -> Boolean
)

object SupplyConfig {
    const val CAT_INSUMOS_BASICOS = "Insumos Básicos"
    const val CAT_SALA_AGUAS_CALDERAS = "Sala de Aguas y Calderas"
    const val CAT_NIVEL_CISTERNAS = "Niveles de Cisternas"
    const val CAT_GASES_CRITICOS = "Gases Críticos"
    const val CAT_VAPOR_COMPRESORES = "Vapor y Compresores"

    val categories = listOf(
        CAT_INSUMOS_BASICOS,
        CAT_SALA_AGUAS_CALDERAS,
        CAT_NIVEL_CISTERNAS,
        CAT_GASES_CRITICOS,
        CAT_VAPOR_COMPRESORES
    )

    val definitions = listOf(
        // INSUMOS BÁSICOS
        SupplyDefinition(
            key = "AP_LEVEL",
            name = "Tanque AP (Agua de Proceso)",
            category = CAT_INSUMOS_BASICOS,
            unit = "L",
            capacity = "13,000 L",
            reference = "Abastecer si ≤ 8,500 L",
            checkAlert = { it != null && it <= 8500 }
        ),
        SupplyDefinition(
            key = "API_LEVEL",
            name = "Tanque API (Agua Inyectables)",
            category = CAT_INSUMOS_BASICOS,
            unit = "L",
            capacity = "2,000 L",
            reference = "Abastecer si ≤ 1,750 L",
            checkAlert = { it != null && it <= 1750 }
        ),
        SupplyDefinition(
            key = "DIESEL",
            name = "Nivel de Diesel",
            category = CAT_INSUMOS_BASICOS,
            unit = "%",
            capacity = "2,000 gal",
            reference = "Abastecer si ≤ 50%",
            checkAlert = { it != null && it <= 50 }
        ),
        SupplyDefinition(
            key = "GAS_PROPANO",
            name = "Gas Propano",
            category = CAT_INSUMOS_BASICOS,
            unit = "%",
            capacity = "250 gal",
            reference = "Abastecer si ≤ 30%",
            checkAlert = { it != null && it <= 30 }
        ),

        // SALA DE AGUAS Y CALDERAS
        SupplyDefinition(
            key = "TRAT_ES_620",
            name = "Tratamiento ES-620",
            category = CAT_SALA_AGUAS_CALDERAS,
            unit = "%",
            capacity = "1 uni",
            reference = "Abastecer si ≤ 25%",
            checkAlert = { it != null && it <= 25 }
        ),
        SupplyDefinition(
            key = "TRAT_ES_501",
            name = "Tratamiento ES-501",
            category = CAT_SALA_AGUAS_CALDERAS,
            unit = "%",
            capacity = "1 uni",
            reference = "Abastecer si ≤ 25%",
            checkAlert = { it != null && it <= 25 }
        ),
        SupplyDefinition(
            key = "TRAT_ES_502",
            name = "Tratamiento ES-502",
            category = CAT_SALA_AGUAS_CALDERAS,
            unit = "%",
            capacity = "1 uni",
            reference = "Abastecer si ≤ 25%",
            checkAlert = { it != null && it <= 25 }
        ),
        SupplyDefinition(
            key = "TRAT_HTM_X16E",
            name = "Tratamiento HTM-X16E",
            category = CAT_SALA_AGUAS_CALDERAS,
            unit = "%",
            capacity = "1 uni",
            reference = "Abastecer si ≤ 25%",
            checkAlert = { it != null && it <= 25 }
        ),
        SupplyDefinition(
            key = "ACIDO_CLOHIDRICO",
            name = "Ácido Clorhídrico",
            category = CAT_SALA_AGUAS_CALDERAS,
            unit = "Barriles",
            capacity = "10 Barriles",
            reference = "Abastecer si ≤ 3",
            checkAlert = { it != null && it <= 3 }
        ),
        SupplyDefinition(
            key = "SODA_CAUSTICA",
            name = "Soda Cáustica",
            category = CAT_SALA_AGUAS_CALDERAS,
            unit = "Barriles",
            capacity = "6 Barriles",
            reference = "Abastecer si ≤ 2",
            checkAlert = { it != null && it <= 2 }
        ),
        SupplyDefinition(
            key = "CLORO",
            name = "Cloro",
            category = CAT_SALA_AGUAS_CALDERAS,
            unit = "Gal",
            capacity = "50 Gal",
            reference = "Abastecer si ≤ 10 Gal",
            checkAlert = { it != null && it <= 10 }
        ),
        SupplyDefinition(
            key = "SAL",
            name = "Sal",
            category = CAT_SALA_AGUAS_CALDERAS,
            unit = "Sacos",
            capacity = "45 sacos",
            reference = "Abastecer si ≤ 10",
            checkAlert = { it != null && it <= 10 }
        ),

        // NIVEL CISTERNAS
        SupplyDefinition(
            key = "CIST_SALA_1",
            name = "Cisterna Sala de Aguas 1",
            category = CAT_NIVEL_CISTERNAS,
            unit = "m³",
            capacity = "100 m³",
            reference = "Abastecer si ≤ 75",
            checkAlert = { it != null && it <= 75 }
        ),
        SupplyDefinition(
            key = "CIST_SALA_2",
            name = "Cisterna Sala de Aguas 2",
            category = CAT_NIVEL_CISTERNAS,
            unit = "m³",
            capacity = "100 m³",
            reference = "Abastecer si ≤ 75",
            checkAlert = { it != null && it <= 75 }
        ),
        SupplyDefinition(
            key = "CIST_CLINICA",
            name = "Cisterna Clínica",
            category = CAT_NIVEL_CISTERNAS,
            unit = "m³",
            capacity = "15 m³",
            reference = "Abastecer si ≤ 10",
            checkAlert = { it != null && it <= 10 }
        ),

        // GASES CRÍTICOS
        SupplyDefinition(
            key = "OXI_TERMO_1",
            name = "Oxígeno Termo 1",
            category = CAT_GASES_CRITICOS,
            unit = "%",
            capacity = "50 gal",
            reference = "Abastecer si ≤ 10%",
            checkAlert = { it != null && it <= 10 }
        ),
        SupplyDefinition(
            key = "OXI_TERMO_2",
            name = "Oxígeno Termo 2",
            category = CAT_GASES_CRITICOS,
            unit = "%",
            capacity = "50 gal",
            reference = "Abastecer si ≤ 10%",
            checkAlert = { it != null && it <= 10 }
        ),
        SupplyDefinition(
            key = "NIT_TERMO_1",
            name = "Nitrógeno Termo 1",
            category = CAT_GASES_CRITICOS,
            unit = "%",
            capacity = "175 lb",
            reference = "Abastecer si ≤ 20%",
            checkAlert = { it != null && it <= 20 }
        ),
        SupplyDefinition(
            key = "NIT_TERMO_2",
            name = "Nitrógeno Termo 2",
            category = CAT_GASES_CRITICOS,
            unit = "%",
            capacity = "175 lb",
            reference = "Abastecer si ≤ 20%",
            checkAlert = { it != null && it <= 20 }
        ),
        SupplyDefinition(
            key = "NIT_TERMO_3",
            name = "Nitrógeno Termo 3",
            category = CAT_GASES_CRITICOS,
            unit = "%",
            capacity = "175 lb",
            reference = "Abastecer si ≤ 20%",
            checkAlert = { it != null && it <= 20 }
        ),
        SupplyDefinition(
            key = "CIL_CO2",
            name = "Cilindros CO2",
            category = CAT_GASES_CRITICOS,
            unit = "uni",
            capacity = "3 uni",
            reference = "Abastecer si ≤ 1",
            checkAlert = { it != null && it <= 1 }
        ),
        SupplyDefinition(
            key = "CIL_N2",
            name = "Cilindros N2",
            category = CAT_GASES_CRITICOS,
            unit = "uni",
            capacity = "3 uni",
            reference = "Abastecer si ≤ 1",
            checkAlert = { it != null && it <= 1 }
        ),
        SupplyDefinition(
            key = "CIL_AIRE_ZERO",
            name = "Cilindro Aire Grado Cero",
            category = CAT_GASES_CRITICOS,
            unit = "uni",
            capacity = "2 uni",
            reference = "Abastecer si ≤ 1",
            checkAlert = { it != null && it <= 1 }
        ),
        SupplyDefinition(
            key = "CIL_N2O",
            name = "Cilindros N2O",
            category = CAT_GASES_CRITICOS,
            unit = "uni",
            capacity = "3 uni",
            reference = "Abastecer si ≤ 1",
            checkAlert = { it != null && it <= 1 }
        ),
        SupplyDefinition(
            key = "CIL_ACETILENO_ABS",
            name = "Cilindro Acetileno ABS",
            category = CAT_GASES_CRITICOS,
            unit = "uni",
            capacity = "3 uni",
            reference = "Abastecer si ≤ 1",
            checkAlert = { it != null && it <= 1 }
        ),
        SupplyDefinition(
            key = "PRESION_NITROGENO",
            name = "Presión de Nitrógeno",
            category = CAT_GASES_CRITICOS,
            unit = "psi",
            capacity = "60-80 psi",
            reference = "Fuera de 60-80 psi",
            checkAlert = { it != null && (it < 60 || it > 80) }
        ),
        SupplyDefinition(
            key = "PRESION_OXIGENO",
            name = "Presión de Oxígeno",
            category = CAT_GASES_CRITICOS,
            unit = "psi",
            capacity = "100-110 psi",
            reference = "Fuera de 100-110 psi",
            checkAlert = { it != null && (it < 100 || it > 110) }
        ),

        // VAPOR Y COMPRESORES (FROM TEAMS REPORT)
        SupplyDefinition(
            key = "CARGA_COMP_1",
            name = "Carga Compresor 1",
            category = CAT_VAPOR_COMPRESORES,
            unit = "%",
            capacity = "100%",
            reference = "Ref: Sin Alerta",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "PRESION_COMP_1",
            name = "Presión Compresor 1",
            category = CAT_VAPOR_COMPRESORES,
            unit = "psi",
            capacity = "135 psi",
            reference = "Fuera de 125-145",
            checkAlert = { it != null && (it < 125 || it > 145) }
        ),
        SupplyDefinition(
            key = "CARGA_COMP_2",
            name = "Carga Compresor 2",
            category = CAT_VAPOR_COMPRESORES,
            unit = "%",
            capacity = "100%",
            reference = "Ref: Sin Alerta",
            checkAlert = { false }
        ),
        SupplyDefinition(
            key = "PRESION_COMP_2",
            name = "Presión Compresor 2",
            category = CAT_VAPOR_COMPRESORES,
            unit = "psi",
            capacity = "135 psi",
            reference = "Fuera de 125-145",
            checkAlert = { it != null && (it < 125 || it > 145) }
        ),
        SupplyDefinition(
            key = "GEN_VAPOR",
            name = "Generación de Vapor",
            category = CAT_VAPOR_COMPRESORES,
            unit = "bar",
            capacity = "3.5 bar",
            reference = "Fuera de 3.0-4.0",
            checkAlert = { it != null && (it < 3.0 || it > 4.0) }
        ),
        SupplyDefinition(
            key = "PRESION_VAPOR_IND",
            name = "Presión Vapor Industrial",
            category = CAT_VAPOR_COMPRESORES,
            unit = "bar",
            capacity = "6.5 bar",
            reference = "Fuera de 6.0-7.5",
            checkAlert = { it != null && (it < 6.0 || it > 7.5) }
        ),
        SupplyDefinition(
            key = "LPG_PES02",
            name = "LPG PES02",
            category = CAT_VAPOR_COMPRESORES,
            unit = "mbar",
            capacity = "90 mbar",
            reference = "Fuera de 80-100",
            checkAlert = { it != null && (it < 80 || it > 100) }
        ),
        SupplyDefinition(
            key = "LPG_PES03",
            name = "LPG PES03",
            category = CAT_VAPOR_COMPRESORES,
            unit = "mbar",
            capacity = "90 mbar",
            reference = "Fuera de 80-100",
            checkAlert = { it != null && (it < 80 || it > 100) }
        )
    )
}
