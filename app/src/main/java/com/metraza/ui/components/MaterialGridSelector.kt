package com.metraza.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.metraza.ui.theme.BorderColor
import com.metraza.ui.theme.MATERIAL_DISPLAY_NAMES
import com.metraza.ui.theme.Primary
import com.metraza.ui.theme.SurfaceLight
import com.metraza.ui.theme.TextPrimary
import com.metraza.ui.theme.TextSecondary

@Composable
fun MaterialGridSelector(
    label: String,
    selectedMaterial: String,
    onMaterialSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val materials = MATERIAL_DISPLAY_NAMES
    val firstRow = materials.take(3)
    val secondRow = materials.drop(3)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Column(
            modifier = Modifier.selectableGroup(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Prvi red - 3 materijala
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                firstRow.forEach { material ->
                    MaterialChip(
                        material = material,
                        isSelected = material == selectedMaterial,
                        onClick = { onMaterialSelected(material) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Drugi red - 3 materijala
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                secondRow.forEach { material ->
                    MaterialChip(
                        material = material,
                        isSelected = material == selectedMaterial,
                        onClick = { onMaterialSelected(material) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MaterialChip(
    material: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(50)
    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) Primary.copy(alpha = 0.16f) else SurfaceLight,
        label = "chipBackground"
    )
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) Primary else BorderColor.copy(alpha = 0.6f),
        label = "chipBorder"
    )
    val borderWidth = if (isSelected) 1.5.dp else 1.dp
    val textColor = if (isSelected) Primary else TextPrimary
    val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

    Box(
        modifier = modifier
            .heightIn(min = 48.dp)
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .selectable(
                selected = isSelected,
                onClick = onClick,
                role = Role.RadioButton
            )
            .padding(vertical = 13.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = material,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = fontWeight,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
