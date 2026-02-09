package com.metraza.data

import java.util.Locale

private const val MIN_INPUT = 0.001
private const val MAX_INPUT = 100000.0
private const val VARIANT_INCREASE = 10
private const val VARIANT_DECREASE = 10

fun parseDouble(raw: String, fieldName: String): Double {
    val cleaned = raw.trim().replace(",", ".")
    if (cleaned.isEmpty()) {
        throw IllegalArgumentException("$fieldName je obavezno polje.")
    }

    val value = cleaned.toDoubleOrNull()
        ?: throw IllegalArgumentException("$fieldName mora biti numerička vrednost.")

    if (value < MIN_INPUT || value > MAX_INPUT) {
        throw IllegalArgumentException(
            "$fieldName mora biti između $MIN_INPUT i $MAX_INPUT."
        )
    }
    return value
}

fun parseInt(raw: String, fieldName: String): Int {
    val cleaned = raw.trim()
    if (cleaned.isEmpty()) {
        throw IllegalArgumentException("$fieldName je obavezno polje.")
    }

    val value = cleaned.toIntOrNull()
        ?: throw IllegalArgumentException("$fieldName mora biti ceo broj.")

    if (value < 1 || value > MAX_INPUT.toInt()) {
        throw IllegalArgumentException(
            "$fieldName mora biti između 1 i ${MAX_INPUT.toInt()}."
        )
    }
    return value
}

fun parseMaterial(raw: String): Material {
    val trimmed = raw.trim()
    if (trimmed.isEmpty()) {
        throw IllegalArgumentException("Molimo odaberite materijal iz liste.")
    }
    return Material.fromDisplayName(trimmed)
        ?: throw IllegalArgumentException("Nepoznat materijal. Koristite Cu, CuZn10, CuZn15, CuZn20, CuZn30 ili CuZn37.")
}

fun getDensity(material: Material): Int = material.density

fun computeAllResults(
    totalWeight: Double,
    cutWidthMm: Double,
    thicknessMm: Double,
    maxRolls: Int,
    material: Material,
    innerDiameterMm: Double? = null
): List<CalculationResult> {
    val results = ArrayList<CalculationResult>(maxRolls)
    for (rollCount in maxRolls downTo 1) {
        val (baseLength, baseWeight) = calculateRollMetrics(
            totalWeight,
            cutWidthMm,
            thicknessMm,
            rollCount,
            material
        )
        val increaseFactor = 1 + VARIANT_INCREASE / 100.0
        val decreaseFactor = 1 - VARIANT_DECREASE / 100.0

        val baseDiameter = innerDiameterMm?.let { calculateOuterDiameter(baseLength, thicknessMm, it) }
        
        val plusLength = baseLength * increaseFactor
        val plusDiameter = innerDiameterMm?.let { calculateOuterDiameter(plusLength, thicknessMm, it) }

        val minusLength = baseLength * decreaseFactor
        val minusDiameter = innerDiameterMm?.let { calculateOuterDiameter(minusLength, thicknessMm, it) }

        results.add(
            CalculationResult(
                rolls = rollCount,
                base = RollResult(baseLength, baseWeight, baseDiameter),
                plusVariant = RollResult(baseLength * increaseFactor, baseWeight * increaseFactor, plusDiameter),
                minusVariant = RollResult(baseLength * decreaseFactor, baseWeight * decreaseFactor, minusDiameter)
            )
        )
    }
    return results
}

private fun calculateRollMetrics(
    totalWeight: Double,
    cutWidthMm: Double,
    thicknessMm: Double,
    rollCount: Int,
    material: Material
): Pair<Double, Double> {
    val density = getDensity(material)

    val cutWidthM = cutWidthMm * 0.001
    val thicknessM = thicknessMm * 0.001
    val crossSection = cutWidthM * thicknessM
    if (crossSection < Math.ulp(1.0)) {
        throw IllegalArgumentException("Proizvod širine i debljine je previše mali za precizan proračun.")
    }

    val volume = totalWeight / density
    val totalLength = volume / crossSection

    return Pair(totalLength / rollCount, totalWeight / rollCount)
}

fun getRollWord(count: Int): String {
    return when {
        count == 1 -> "rez"
        count in 2..4 -> "reza"
        else -> "rezova"
    }
}

fun formatNumber(value: Double): String {
    return String.format(Locale.US, "%.2f", value)
}

fun calculateOuterDiameter(lengthM: Double, thicknessMm: Double, innerDiameterMm: Double): Double {
    val lengthMm = lengthM * 1000.0
    val area = lengthMm * thicknessMm
    // Area of annulus = pi * (R^2 - r^2) = pi/4 * (D^2 - d^2)
    // Area of cross section (side view) = L * T
    // Using the volume conservation approach from Precnik app:
    // OD = sqrt(ID^2 + (4/PI) * L_mm * T_mm)
    return kotlin.math.sqrt(
        innerDiameterMm * innerDiameterMm + (4.0 / Math.PI) * area
    )
}

fun doCalculate(input: InputState): List<CalculationResult> {
    val material = input.selectedMaterial
        ?: throw IllegalArgumentException("Molimo odaberite materijal iz liste.")
    val weight = parseDouble(input.totalWeight, "Ukupna težina")
    val thick = parseDouble(input.thickness, "Debljina")
    val width = parseDouble(input.cutWidth, "Širina rezanja")
    val rollCount = parseInt(input.rolls, "Broj rezova")
    val innerD = if (input.innerDiameter.trim().isNotEmpty()) {
        parseDouble(input.innerDiameter, "Unutrašnji prečnik")
    } else null

    return computeAllResults(weight, width, thick, rollCount, material, innerD)
}

fun formatResultsForShare(results: List<CalculationResult>, input: InputState): String {
    val sb = StringBuilder()
    sb.appendLine("=== METRAZA - REZULTATI ===")
    sb.appendLine()

    // Parametri
    sb.appendLine("PARAMETRI:")
    sb.appendLine("Materijal: ${input.selectedMaterial?.displayName ?: "N/A"}")
    sb.appendLine("Ukupna težina: ${input.totalWeight} kg")
    sb.appendLine("Debljina: ${input.thickness} mm")
    sb.appendLine("Širina rezanja: ${input.cutWidth} mm")
    sb.appendLine("Broj rezova: ${input.rolls}")
    if (input.innerDiameter.trim().isNotBlank()) {
        sb.appendLine("Unutrašnji prečnik: ${input.innerDiameter} mm")
    }
    sb.appendLine()

    // Rezultati
    sb.appendLine("REZULTATI:")
    results.forEach { result ->
        val rollWord = getRollWord(result.rolls)
        sb.appendLine()
        sb.appendLine("Za ${result.rolls} $rollWord:")

        // Base variant
        sb.append("  Tačna: ${formatNumber(result.base.lengthM)} m")
        sb.append(" * ${formatNumber(result.base.weightKg)} kg")
        if (result.base.outerDiameterMm != null) {
            sb.append(" (O ${result.base.outerDiameterMm.roundToInt()} mm)")
        }
        sb.appendLine()

        // +10% variant
        sb.append("  +10%: ${formatNumber(result.plusVariant.lengthM)} m")
        sb.append(" * ${formatNumber(result.plusVariant.weightKg)} kg")
        if (result.plusVariant.outerDiameterMm != null) {
            sb.append(" (O ${result.plusVariant.outerDiameterMm.roundToInt()} mm)")
        }
        sb.appendLine()

        // -10% variant
        sb.append("  -10%: ${formatNumber(result.minusVariant.lengthM)} m")
        sb.append(" * ${formatNumber(result.minusVariant.weightKg)} kg")
        if (result.minusVariant.outerDiameterMm != null) {
            sb.append(" (O ${result.minusVariant.outerDiameterMm.roundToInt()} mm)")
        }
        sb.appendLine()
    }

    return sb.toString()
}
