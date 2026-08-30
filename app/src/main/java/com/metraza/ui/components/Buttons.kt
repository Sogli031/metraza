package com.metraza.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.metraza.ui.theme.Background
import com.metraza.ui.theme.BorderColor
import com.metraza.ui.theme.PrimaryGradientEnd
import com.metraza.ui.theme.PrimaryGradientStart
import com.metraza.ui.theme.PrimaryShadow
import com.metraza.ui.theme.SurfaceLight
import com.metraza.ui.theme.TextSecondary

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 54.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 10.dp),
    icon: (@Composable RowScope.() -> Unit)? = null
) {
    val shape = RoundedCornerShape(50)

    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = minHeight)
            .shadow(
                elevation = 10.dp,
                shape = shape,
                ambientColor = PrimaryShadow,
                spotColor = PrimaryShadow
            )
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(PrimaryGradientStart, PrimaryGradientEnd)
                )
            ),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Background
        ),
        contentPadding = contentPadding
    ) {
        if (icon != null) {
            icon()
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    minHeight: Dp = 54.dp,
    contentPadding: PaddingValues = PaddingValues(horizontal = 18.dp, vertical = 10.dp)
) {
    val shape = RoundedCornerShape(50)

    Button(
        onClick = onClick,
        modifier = modifier
            .heightIn(min = minHeight)
            .clip(shape)
            .border(1.dp, BorderColor.copy(alpha = 0.8f), shape),
        shape = shape,
        colors = ButtonDefaults.buttonColors(
            containerColor = SurfaceLight,
            contentColor = TextSecondary
        ),
        contentPadding = contentPadding
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
