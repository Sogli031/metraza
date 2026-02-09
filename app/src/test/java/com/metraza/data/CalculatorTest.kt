package com.metraza.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.Assert.assertThrows

class CalculatorTest {

    // ========== parseDouble testovi ==========

    @Test
    fun parseDouble_validDecimal_returnsCorrectValue() {
        val result = parseDouble("10.5", "Test Field")
        assertThat(result).isEqualTo(10.5)
    }

    @Test
    fun parseDouble_commaDecimalSeparator_convertsToPoint() {
        val result = parseDouble("10,5", "Test Field")
        assertThat(result).isEqualTo(10.5)
    }

    @Test
    fun parseDouble_integerInput_returnsDouble() {
        val result = parseDouble("100", "Test Field")
        assertThat(result).isEqualTo(100.0)
    }

    @Test
    fun parseDouble_emptyString_throwsException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            parseDouble("", "Test Field")
        }
        assertThat(exception.message).contains("obavezno polje")
    }

    @Test
    fun parseDouble_nonNumeric_throwsException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            parseDouble("abc", "Test Field")
        }
        assertThat(exception.message).contains("numerička vrednost")
    }

    @Test
    fun parseDouble_belowMinimum_throwsException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            parseDouble("0.0001", "Test Field")
        }
        assertThat(exception.message).contains("između")
    }

    @Test
    fun parseDouble_aboveMaximum_throwsException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            parseDouble("100001", "Test Field")
        }
        assertThat(exception.message).contains("između")
    }

    @Test
    fun parseDouble_minimumValue_returnsValue() {
        val result = parseDouble("0.001", "Test Field")
        assertThat(result).isEqualTo(0.001)
    }

    @Test
    fun parseDouble_maximumValue_returnsValue() {
        val result = parseDouble("100000", "Test Field")
        assertThat(result).isEqualTo(100000.0)
    }

    @Test
    fun parseDouble_withWhitespace_trimmed() {
        val result = parseDouble(" 10.5 ", "Test Field")
        assertThat(result).isEqualTo(10.5)
    }

    // ========== parseInt testovi ==========

    @Test
    fun parseInt_validInteger_returnsCorrectValue() {
        val result = parseInt("5", "Test Field")
        assertThat(result).isEqualTo(5)
    }

    @Test
    fun parseInt_emptyString_throwsException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            parseInt("", "Test Field")
        }
        assertThat(exception.message).contains("obavezno polje")
    }

    @Test
    fun parseInt_nonInteger_throwsException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            parseInt("5.5", "Test Field")
        }
        assertThat(exception.message).contains("ceo broj")
    }

    @Test
    fun parseInt_zero_throwsException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            parseInt("0", "Test Field")
        }
        assertThat(exception.message).contains("između 1")
    }

    @Test
    fun parseInt_negativeNumber_throwsException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            parseInt("-1", "Test Field")
        }
        assertThat(exception.message).contains("ceo broj")
    }

    @Test
    fun parseInt_maximumValue_returnsValue() {
        val result = parseInt("100000", "Test Field")
        assertThat(result).isEqualTo(100000)
    }

    // ========== parseMaterial testovi ==========

    @Test
    fun parseMaterial_validName_returnsMaterial() {
        val result = parseMaterial("Cu")
        assertThat(result).isEqualTo(Material.Cu)
    }

    @Test
    fun parseMaterial_emptyString_throwsException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            parseMaterial("")
        }
        assertThat(exception.message).contains("odaberite materijal")
    }

    @Test
    fun parseMaterial_invalidName_throwsException() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            parseMaterial("InvalidMat")
        }
        assertThat(exception.message).contains("Nepoznat materijal")
    }

    // ========== getDensity testovi ==========

    @Test
    fun getDensity_allMaterials_returnCorrectDensities() {
        assertThat(getDensity(Material.Cu)).isEqualTo(8960)
        assertThat(getDensity(Material.CuZn10)).isEqualTo(8800)
        assertThat(getDensity(Material.CuZn15)).isEqualTo(8750)
        assertThat(getDensity(Material.CuZn20)).isEqualTo(8670)
        assertThat(getDensity(Material.CuZn30)).isEqualTo(8530)
        assertThat(getDensity(Material.CuZn37)).isEqualTo(8285)
    }

    // ========== computeAllResults testovi ==========

    @Test
    fun computeAllResults_singleRoll_returnsOneResult() {
        val results = computeAllResults(
            totalWeight = 100.0,
            cutWidthMm = 50.0,
            thicknessMm = 0.5,
            maxRolls = 1,
            material = Material.Cu,
            innerDiameterMm = null
        )
        assertThat(results).hasSize(1)
        assertThat(results[0].rolls).isEqualTo(1)
    }

    @Test
    fun computeAllResults_multipleRolls_returnsDescendingOrder() {
        val results = computeAllResults(
            totalWeight = 100.0,
            cutWidthMm = 50.0,
            thicknessMm = 0.5,
            maxRolls = 5,
            material = Material.Cu,
            innerDiameterMm = null
        )
        assertThat(results).hasSize(5)
        assertThat(results[0].rolls).isEqualTo(5)
        assertThat(results[1].rolls).isEqualTo(4)
        assertThat(results[2].rolls).isEqualTo(3)
        assertThat(results[3].rolls).isEqualTo(2)
        assertThat(results[4].rolls).isEqualTo(1)
    }

    @Test
    fun computeAllResults_weightDistribution_isEven() {
        val results = computeAllResults(
            totalWeight = 100.0,
            cutWidthMm = 50.0,
            thicknessMm = 0.5,
            maxRolls = 5,
            material = Material.Cu,
            innerDiameterMm = null
        )
        val fiveRollResult = results.find { it.rolls == 5 }!!
        assertThat(fiveRollResult.base.weightKg).isWithin(0.01).of(20.0)
    }

    @Test
    fun computeAllResults_plusVariant_is10PercentMore() {
        val results = computeAllResults(
            totalWeight = 100.0,
            cutWidthMm = 50.0,
            thicknessMm = 0.5,
            maxRolls = 1,
            material = Material.Cu,
            innerDiameterMm = null
        )
        val result = results[0]
        assertThat(result.plusVariant.weightKg).isWithin(0.001).of(result.base.weightKg * 1.1)
        assertThat(result.plusVariant.lengthM).isWithin(0.001).of(result.base.lengthM * 1.1)
    }

    @Test
    fun computeAllResults_minusVariant_is10PercentLess() {
        val results = computeAllResults(
            totalWeight = 100.0,
            cutWidthMm = 50.0,
            thicknessMm = 0.5,
            maxRolls = 1,
            material = Material.Cu,
            innerDiameterMm = null
        )
        val result = results[0]
        assertThat(result.minusVariant.weightKg).isWithin(0.001).of(result.base.weightKg * 0.9)
        assertThat(result.minusVariant.lengthM).isWithin(0.001).of(result.base.lengthM * 0.9)
    }

    @Test
    fun computeAllResults_withInnerDiameter_hasOuterDiameter() {
        val results = computeAllResults(
            totalWeight = 100.0,
            cutWidthMm = 50.0,
            thicknessMm = 0.5,
            maxRolls = 1,
            material = Material.Cu,
            innerDiameterMm = 100.0
        )
        assertThat(results[0].base.outerDiameterMm).isNotNull()
        assertThat(results[0].plusVariant.outerDiameterMm).isNotNull()
        assertThat(results[0].minusVariant.outerDiameterMm).isNotNull()
    }

    @Test
    fun computeAllResults_withoutInnerDiameter_noOuterDiameter() {
        val results = computeAllResults(
            totalWeight = 100.0,
            cutWidthMm = 50.0,
            thicknessMm = 0.5,
            maxRolls = 1,
            material = Material.Cu,
            innerDiameterMm = null
        )
        assertThat(results[0].base.outerDiameterMm).isNull()
        assertThat(results[0].plusVariant.outerDiameterMm).isNull()
        assertThat(results[0].minusVariant.outerDiameterMm).isNull()
    }

    @Test
    fun computeAllResults_Cu100kg_correctLength() {
        // Manual calculation:
        // 100kg Cu, 50mm width, 0.5mm thickness, 1 roll
        // density = 8960 kg/m3
        // volume = 100/8960 = 0.01116 m3
        // crossSection = 0.05 * 0.0005 = 0.000025 m2
        // length = 0.01116/0.000025 = 446.43 m
        val results = computeAllResults(
            totalWeight = 100.0,
            cutWidthMm = 50.0,
            thicknessMm = 0.5,
            maxRolls = 1,
            material = Material.Cu,
            innerDiameterMm = null
        )
        assertThat(results[0].base.lengthM).isWithin(0.1).of(446.43)
    }

    // ========== calculateOuterDiameter testovi ==========

    @Test
    fun calculateOuterDiameter_basicCase_returnsGreaterThanInner() {
        val outerD = calculateOuterDiameter(
            lengthM = 100.0,
            thicknessMm = 0.5,
            innerDiameterMm = 100.0
        )
        assertThat(outerD).isGreaterThan(100.0)
    }

    @Test
    fun calculateOuterDiameter_knownValues_returnsCorrectResult() {
        // Manual calculation:
        // lengthM=100, thicknessMm=0.5, innerDiameterMm=100
        // lengthMm = 100000
        // area = 100000 * 0.5 = 50000
        // OD = sqrt(100^2 + (4/PI)*50000) = sqrt(10000 + 63661.98) = sqrt(73661.98) = 271.41
        val outerD = calculateOuterDiameter(
            lengthM = 100.0,
            thicknessMm = 0.5,
            innerDiameterMm = 100.0
        )
        assertThat(outerD).isWithin(0.1).of(271.41)
    }

    // ========== getRollWord testovi ==========

    @Test
    fun getRollWord_one_returnsSingular() {
        assertThat(getRollWord(1)).isEqualTo("rez")
    }

    @Test
    fun getRollWord_twoToFour_returnsReza() {
        assertThat(getRollWord(2)).isEqualTo("reza")
        assertThat(getRollWord(3)).isEqualTo("reza")
        assertThat(getRollWord(4)).isEqualTo("reza")
    }

    @Test
    fun getRollWord_fiveAndAbove_returnsRezova() {
        assertThat(getRollWord(5)).isEqualTo("rezova")
        assertThat(getRollWord(10)).isEqualTo("rezova")
        assertThat(getRollWord(100)).isEqualTo("rezova")
    }

    // ========== formatNumber testovi ==========

    @Test
    fun formatNumber_twoDecimalPlaces() {
        val result = formatNumber(10.5)
        assertThat(result).isEqualTo("10.50")
    }

    @Test
    fun formatNumber_roundsCorrectly() {
        val result = formatNumber(10.555)
        // Locale.US rounds to 2 decimal places
        assertThat(result).isAnyOf("10.56", "10.55")
    }

    @Test
    fun formatNumber_wholeNumber() {
        val result = formatNumber(100.0)
        assertThat(result).isEqualTo("100.00")
    }

    // ========== doCalculate testovi ==========

    @Test
    fun doCalculate_validInput_returnsResults() {
        val input = InputState(
            selectedMaterial = Material.Cu,
            totalWeight = "100",
            thickness = "0.5",
            cutWidth = "50",
            rolls = "5",
            innerDiameter = ""
        )
        val results = doCalculate(input)
        assertThat(results).hasSize(5)
        assertThat(results[0].rolls).isEqualTo(5)
    }

    @Test
    fun doCalculate_missingMaterial_throwsException() {
        val input = InputState(
            selectedMaterial = null,
            totalWeight = "100",
            thickness = "0.5",
            cutWidth = "50",
            rolls = "5",
            innerDiameter = ""
        )
        val exception = assertThrows(IllegalArgumentException::class.java) {
            doCalculate(input)
        }
        assertThat(exception.message).contains("odaberite materijal")
    }

    @Test
    fun doCalculate_emptyOptionalDiameter_returnsResultsWithoutDiameter() {
        val input = InputState(
            selectedMaterial = Material.Cu,
            totalWeight = "100",
            thickness = "0.5",
            cutWidth = "50",
            rolls = "1",
            innerDiameter = ""
        )
        val results = doCalculate(input)
        assertThat(results[0].base.outerDiameterMm).isNull()
    }

    // ========== formatResultsForShare testovi ==========

    @Test
    fun formatResultsForShare_containsHeader() {
        val input = InputState(
            selectedMaterial = Material.Cu,
            totalWeight = "100",
            thickness = "0.5",
            cutWidth = "50",
            rolls = "5",
            innerDiameter = ""
        )
        val results = computeAllResults(100.0, 50.0, 0.5, 5, Material.Cu, null)
        val output = formatResultsForShare(results, input)
        assertThat(output).contains("METRAZA - REZULTATI")
    }

    @Test
    fun formatResultsForShare_containsParameters() {
        val input = InputState(
            selectedMaterial = Material.Cu,
            totalWeight = "100",
            thickness = "0.5",
            cutWidth = "50",
            rolls = "5",
            innerDiameter = ""
        )
        val results = computeAllResults(100.0, 50.0, 0.5, 5, Material.Cu, null)
        val output = formatResultsForShare(results, input)
        assertThat(output).contains("Materijal: Cu")
        assertThat(output).contains("Ukupna težina: 100 kg")
        assertThat(output).contains("Debljina: 0.5 mm")
    }

    @Test
    fun formatResultsForShare_containsResults() {
        val input = InputState(
            selectedMaterial = Material.Cu,
            totalWeight = "100",
            thickness = "0.5",
            cutWidth = "50",
            rolls = "5",
            innerDiameter = ""
        )
        val results = computeAllResults(100.0, 50.0, 0.5, 5, Material.Cu, null)
        val output = formatResultsForShare(results, input)
        assertThat(output).contains("Za 5 rezova:")
        assertThat(output).contains("Za 1 rez:")
    }
}
