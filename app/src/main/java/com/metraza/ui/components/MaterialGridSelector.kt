package com.metraza.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.metraza.data.Material
import com.metraza.ui.theme.BorderColor
import com.metraza.ui.theme.Primary
import com.metraza.ui.theme.SurfaceElevated
import com.metraza.ui.theme.SurfaceLight
import com.metraza.ui.theme.TextPrimary
import com.metraza.ui.theme.TextSecondary

@Composable
fun MaterialGridSelector(
    label: String,
    selectedMaterial: Material?,
    onMaterialSelected: (Material) -> Unit,
    modifier: Modifier = Modifier
) {
    val materials = Material.entries
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
    material: Material,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(10.dp)
    val backgroundColor = if (isSelected) SurfaceElevated else SurfaceLight
    val borderColor = if (isSelected) Primary else BorderColor
    val borderWidth = if (isSelected) 2.dp else 1.dp
    val textColor = if (isSelected) Primary else TextPrimary
    val fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal

    Box(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, shape)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = material.displayName,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = fontWeight,
            color = textColor,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
