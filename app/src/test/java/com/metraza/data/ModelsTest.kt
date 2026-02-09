package com.metraza.data

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ModelsTest {

    // ========== Material.fromDisplayName testovi ==========

    @Test
    fun fromDisplayName_validName_returnsMaterial() {
        val result = Material.fromDisplayName("Cu")
        assertThat(result).isEqualTo(Material.Cu)
    }

    @Test
    fun fromDisplayName_allMaterials_found() {
        Material.entries.forEach { material ->
            val result = Material.fromDisplayName(material.name)
            assertThat(result).isNotNull()
            assertThat(result).isEqualTo(material)
        }
    }

    @Test
    fun fromDisplayName_caseInsensitive_returnsMaterial() {
        val cu = Material.fromDisplayName("cu")
        assertThat(cu).isEqualTo(Material.Cu)

        val cuzn10 = Material.fromDisplayName("CUZN10")
        assertThat(cuzn10).isEqualTo(Material.CuZn10)
    }

    @Test
    fun fromDisplayName_invalidName_returnsNull() {
        val result = Material.fromDisplayName("InvalidMat")
        assertThat(result).isNull()
    }

    // ========== Material density testovi (TEST-03 requirement) ==========

    @Test
    fun material_Cu_density_is8960() {
        assertThat(Material.Cu.density).isEqualTo(8960)
    }

    @Test
    fun material_CuZn10_density_is8800() {
        assertThat(Material.CuZn10.density).isEqualTo(8800)
    }

    @Test
    fun material_CuZn15_density_is8750() {
        assertThat(Material.CuZn15.density).isEqualTo(8750)
    }

    @Test
    fun material_CuZn20_density_is8670() {
        assertThat(Material.CuZn20.density).isEqualTo(8670)
    }

    @Test
    fun material_CuZn30_density_is8530() {
        assertThat(Material.CuZn30.density).isEqualTo(8530)
    }

    @Test
    fun material_CuZn37_density_is8285() {
        assertThat(Material.CuZn37.density).isEqualTo(8285)
    }

    // ========== Material entries testovi ==========

    @Test
    fun material_hasSixEntries() {
        assertThat(Material.entries.size).isEqualTo(6)
    }

    // ========== Material displayName testovi ==========

    @Test
    fun material_displayName_matchesEnumName() {
        Material.entries.forEach { material ->
            assertThat(material.displayName).isEqualTo(material.name)
        }
    }

    // ========== Data class testovi ==========

    @Test
    fun rollResult_defaultOuterDiameter_isNull() {
        val result = RollResult(10.0, 5.0)
        assertThat(result.outerDiameterMm).isNull()
    }

    @Test
    fun inputState_defaultValues_areEmpty() {
        val state = InputState()
        assertThat(state.selectedMaterial).isNull()
        assertThat(state.totalWeight).isEmpty()
        assertThat(state.thickness).isEmpty()
        assertThat(state.cutWidth).isEmpty()
        assertThat(state.rolls).isEmpty()
        assertThat(state.innerDiameter).isEmpty()
    }

    @Test
    fun fieldValidation_valid_hasNoErrorMessage() {
        val validation = FieldValidation(true)
        assertThat(validation.errorMessage).isEmpty()
    }

    @Test
    fun fieldValidation_invalid_hasErrorMessage() {
        val validation = FieldValidation(false, "error")
        assertThat(validation.errorMessage).isEqualTo("error")
    }
}
