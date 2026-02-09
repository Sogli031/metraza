package com.metraza.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ValidationTest {

    // ========== validateTotalWeight testovi ==========

    @Test
    fun validateTotalWeight_validInput_returnsValid() {
        val result = validateTotalWeight("100")
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun validateTotalWeight_commaDecimal_returnsValid() {
        val result = validateTotalWeight("10,5")
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun validateTotalWeight_emptyString_returnsInvalid() {
        val result = validateTotalWeight("")
        assertThat(result.isValid).isFalse()
        assertThat(result.errorMessage).contains("obavezno polje")
    }

    @Test
    fun validateTotalWeight_nonNumeric_returnsInvalid() {
        val result = validateTotalWeight("abc")
        assertThat(result.isValid).isFalse()
        assertThat(result.errorMessage).contains("numerička vrednost")
    }

    @Test
    fun validateTotalWeight_belowMin_returnsInvalid() {
        val result = validateTotalWeight("0.0001")
        assertThat(result.isValid).isFalse()
        assertThat(result.errorMessage).contains("između")
    }

    @Test
    fun validateTotalWeight_aboveMax_returnsInvalid() {
        val result = validateTotalWeight("100001")
        assertThat(result.isValid).isFalse()
        assertThat(result.errorMessage).contains("između")
    }

    @Test
    fun validateTotalWeight_boundaryMin_returnsValid() {
        val result = validateTotalWeight("0.001")
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun validateTotalWeight_boundaryMax_returnsValid() {
        val result = validateTotalWeight("100000")
        assertThat(result.isValid).isTrue()
    }

    // ========== validateThickness testovi ==========

    @Test
    fun validateThickness_validInput_returnsValid() {
        val result = validateThickness("0.5")
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun validateThickness_emptyString_returnsInvalid() {
        val result = validateThickness("")
        assertThat(result.isValid).isFalse()
        assertThat(result.errorMessage).contains("obavezno")
    }

    @Test
    fun validateThickness_nonNumeric_returnsInvalid() {
        val result = validateThickness("abc")
        assertThat(result.isValid).isFalse()
        assertThat(result.errorMessage).contains("numerička vrednost")
    }

    // ========== validateCutWidth testovi ==========

    @Test
    fun validateCutWidth_validInput_returnsValid() {
        val result = validateCutWidth("100")
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun validateCutWidth_emptyString_returnsInvalid() {
        val result = validateCutWidth("")
        assertThat(result.isValid).isFalse()
        assertThat(result.errorMessage).contains("obavezno polje")
    }

    @Test
    fun validateCutWidth_nonNumeric_returnsInvalid() {
        val result = validateCutWidth("abc")
        assertThat(result.isValid).isFalse()
        assertThat(result.errorMessage).contains("numerička vrednost")
    }

    // ========== validateRolls testovi ==========

    @Test
    fun validateRolls_validInteger_returnsValid() {
        val result = validateRolls("5")
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun validateRolls_emptyString_returnsInvalid() {
        val result = validateRolls("")
        assertThat(result.isValid).isFalse()
        assertThat(result.errorMessage).contains("obavezno polje")
    }

    @Test
    fun validateRolls_decimal_returnsInvalid() {
        val result = validateRolls("5.5")
        assertThat(result.isValid).isFalse()
        assertThat(result.errorMessage).contains("ceo broj")
    }

    @Test
    fun validateRolls_zero_returnsInvalid() {
        val result = validateRolls("0")
        assertThat(result.isValid).isFalse()
        assertThat(result.errorMessage).contains("između 1")
    }

    // ========== validateInnerDiameter testovi ==========

    @Test
    fun validateInnerDiameter_emptyString_returnsValid() {
        val result = validateInnerDiameter("")
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun validateInnerDiameter_validInput_returnsValid() {
        val result = validateInnerDiameter("150")
        assertThat(result.isValid).isTrue()
    }

    @Test
    fun validateInnerDiameter_nonNumeric_returnsInvalid() {
        val result = validateInnerDiameter("abc")
        assertThat(result.isValid).isFalse()
        assertThat(result.errorMessage).contains("numerička vrednost")
    }

    // ========== isFormValid testovi ==========

    @Test
    fun isFormValid_allFieldsValid_returnsTrue() {
        val input = InputState(
            selectedMaterial = Material.Cu,
            totalWeight = "100",
            thickness = "0.5",
            cutWidth = "50",
            rolls = "5",
            innerDiameter = ""
        )
        val result = isFormValid(input)
        assertThat(result).isTrue()
    }

    @Test
    fun isFormValid_noMaterial_returnsFalse() {
        val input = InputState(
            selectedMaterial = null,
            totalWeight = "100",
            thickness = "0.5",
            cutWidth = "50",
            rolls = "5",
            innerDiameter = ""
        )
        val result = isFormValid(input)
        assertThat(result).isFalse()
    }

    @Test
    fun isFormValid_emptyWeight_returnsFalse() {
        val input = InputState(
            selectedMaterial = Material.Cu,
            totalWeight = "",
            thickness = "0.5",
            cutWidth = "50",
            rolls = "5",
            innerDiameter = ""
        )
        val result = isFormValid(input)
        assertThat(result).isFalse()
    }

    @Test
    fun isFormValid_emptyOptionalDiameter_returnsTrue() {
        val input = InputState(
            selectedMaterial = Material.Cu,
            totalWeight = "100",
            thickness = "0.5",
            cutWidth = "50",
            rolls = "5",
            innerDiameter = ""
        )
        val result = isFormValid(input)
        assertThat(result).isTrue()
    }
}
