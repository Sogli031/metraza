package com.metraza.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.metraza.data.FieldValidation
import com.metraza.ui.theme.BorderColor
import com.metraza.ui.theme.ErrorColor
import com.metraza.ui.theme.Primary
import com.metraza.ui.theme.SurfaceLight
import com.metraza.ui.theme.TextPrimary
import com.metraza.ui.theme.TextSecondary

@Composable
fun LabeledTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    validation: FieldValidation = FieldValidation(true),
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    focusRequester: FocusRequester? = null,
    imeAction: ImeAction = ImeAction.Next,
    onImeAction: () -> Unit = {}
) {
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
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            isError = !validation.isValid,
            supportingText = if (!validation.isValid) {
                {
                    Text(
                        text = validation.errorMessage,
                        color = ErrorColor,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            } else null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = imeAction
            ),
            keyboardActions = KeyboardActions(
                onNext = { onImeAction() },
                onDone = { onImeAction() }
            ),
            colors = fieldColors(isError = !validation.isValid),
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (focusRequester != null) Modifier.focusRequester(focusRequester)
                    else Modifier
                )
        )
    }
}

@Composable
fun fieldColors(isError: Boolean = false): TextFieldColors {
    return OutlinedTextFieldDefaults.colors(
        focusedTextColor = TextPrimary,
        unfocusedTextColor = TextPrimary,
        focusedContainerColor = SurfaceLight,
        unfocusedContainerColor = SurfaceLight,
        focusedBorderColor = if (isError) ErrorColor else Primary,
        unfocusedBorderColor = if (isError) ErrorColor else BorderColor,
        errorBorderColor = ErrorColor,
        cursorColor = Primary,
        focusedPlaceholderColor = TextSecondary,
        unfocusedPlaceholderColor = TextSecondary
    )
}
