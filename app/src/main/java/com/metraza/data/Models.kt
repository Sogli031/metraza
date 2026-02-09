package com.metraza.data

enum class Material(val density: Int) {
    Cu(8960),
    CuZn10(8800),
    CuZn15(8750),
    CuZn20(8670),
    CuZn30(8530),
    CuZn37(8285);

    val displayName: String
        get() = name

    companion object {
        fun fromDisplayName(name: String): Material? {
            return entries.find { it.name.equals(name, ignoreCase = true) }
        }
    }
}

data class RollResult(
    val lengthM: Double,
    val weightKg: Double,
    val outerDiameterMm: Double? = null
)

data class CalculationResult(
    val rolls: Int,
    val base: RollResult,
    val plusVariant: RollResult,
    val minusVariant: RollResult
)

enum class MessageType {
    NONE,
    SUCCESS,
    ERROR
}

data class MessageState(
    val text: String,
    val type: MessageType
)

data class InputState(
    val selectedMaterial: Material? = null,
    val totalWeight: String = "",
    val thickness: String = "",
    val cutWidth: String = "",
    val rolls: String = "",
    val innerDiameter: String = ""
)

data class FieldValidation(
    val isValid: Boolean,
    val errorMessage: String = ""
)
