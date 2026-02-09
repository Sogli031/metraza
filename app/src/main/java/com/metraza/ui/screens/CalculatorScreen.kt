package com.metraza.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.metraza.R
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.TextStyle
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.animation.shrinkVertically
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.take
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.runtime.mutableIntStateOf
import com.metraza.data.CalculationResult
import com.metraza.data.Material
import com.metraza.data.MessageState
import com.metraza.data.MessageType
import com.metraza.data.InputState
import com.metraza.data.FieldValidation
import com.metraza.data.doCalculate
import com.metraza.data.validateTotalWeight
import com.metraza.data.validateThickness
import com.metraza.data.validateCutWidth
import com.metraza.data.validateRolls
import com.metraza.data.validateInnerDiameter
import com.metraza.data.isFormValid
import com.metraza.data.formatNumber
import com.metraza.data.getRollWord
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.focus.onFocusChanged
import com.metraza.ui.components.AccentGlassCard
import com.metraza.ui.components.GlassCard
import com.metraza.ui.components.LabeledTextField
import com.metraza.ui.components.MaterialGridSelector
import com.metraza.ui.components.PrimaryButton
import com.metraza.ui.theme.BackgroundGradientEnd
import com.metraza.ui.theme.BackgroundGradientStart
import com.metraza.ui.theme.ErrorColor
import com.metraza.ui.theme.Primary
import com.metraza.ui.theme.PrimaryDark
import com.metraza.ui.theme.Secondary
import com.metraza.ui.theme.SuccessColor
import com.metraza.ui.theme.TextPrimary
import com.metraza.ui.theme.TextSecondary
import com.metraza.ui.theme.layoutTokens
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalculatorScreen() {
    // Input state - sva polja grupisana
    var selectedMaterialName by rememberSaveable { mutableStateOf("") }
    val selectedMaterial: Material? = selectedMaterialName.takeIf { it.isNotEmpty() }?.let { Material.fromDisplayName(it) }

    var totalWeight by rememberSaveable { mutableStateOf("") }
    var thickness by rememberSaveable { mutableStateOf("") }
    var cutWidth by rememberSaveable { mutableStateOf("") }
    var rolls by rememberSaveable { mutableStateOf("") }
    var innerDiameter by rememberSaveable { mutableStateOf("") }

    // Derived InputState za doCalculate pure function
    val inputState = InputState(
        selectedMaterial = selectedMaterial,
        totalWeight = totalWeight,
        thickness = thickness,
        cutWidth = cutWidth,
        rolls = rolls,
        innerDiameter = innerDiameter
    )

    var messageState by remember { mutableStateOf(MessageState("", MessageType.NONE)) }

    // Validation - error prikazuje se tek nakon što korisnik napusti polje
    var showWeightError by remember { mutableStateOf(false) }
    var showThicknessError by remember { mutableStateOf(false) }
    var showWidthError by remember { mutableStateOf(false) }
    var showRollsError by remember { mutableStateOf(false) }
    var showDiameterError by remember { mutableStateOf(false) }

    // Validation results (reaktivne - recompute kada se input promeni)
    val weightValidation by remember { derivedStateOf { validateTotalWeight(totalWeight) } }
    val thicknessValidation by remember { derivedStateOf { validateThickness(thickness) } }
    val widthValidation by remember { derivedStateOf { validateCutWidth(cutWidth) } }
    val rollsValidation by remember { derivedStateOf { validateRolls(rolls) } }
    val diameterValidation by remember { derivedStateOf { validateInnerDiameter(innerDiameter) } }

    // Form validity za button enable/disable (real-time, bez blur)
    val formValid by remember { derivedStateOf { isFormValid(inputState) } }

    // Focus requesters za navigaciju između polja
    val focusManager = LocalFocusManager.current
    val weightFocus = remember { FocusRequester() }
    val thicknessFocus = remember { FocusRequester() }
    val widthFocus = remember { FocusRequester() }
    val rollsFocus = remember { FocusRequester() }
    val diameterFocus = remember { FocusRequester() }
    var results by remember { mutableStateOf<List<CalculationResult>>(emptyList()) }
    val layout = layoutTokens()

    // Scroll state i detekcija tastature
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val isKeyboardVisible = WindowInsets.isImeVisible
    var resultsPosition by remember { mutableIntStateOf(0) }
    var shouldScrollToResults by remember { mutableStateOf(false) }

    // Funkcija za izračunavanje
    val handleCalculate: () -> Unit = {
        focusManager.clearFocus()
        messageState = MessageState("", MessageType.NONE)
        results = emptyList()

        // Aktiviraj sve error prikaze
        showWeightError = true
        showThicknessError = true
        showWidthError = true
        showRollsError = true
        showDiameterError = true

        try {
            val calcResults = doCalculate(inputState)
            if (calcResults.isNotEmpty()) {
                results = calcResults
                messageState = MessageState(
                    "✓ Proračun uspešno završen.",
                    MessageType.SUCCESS
                )
                // Skroluj do rezultata
                shouldScrollToResults = true
            } else {
                messageState = MessageState(
                    "✗ Proverite ulazne parametre.",
                    MessageType.ERROR
                )
            }
        } catch (ex: IllegalArgumentException) {
            messageState = MessageState(
                "✗ ${ex.message}",
                MessageType.ERROR
            )
        }
    }

    val backgroundBrush = Brush.linearGradient(
        colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
    )

    // Kompaktan spacing kada je tastatura otvorena
    val currentSpacing = if (isKeyboardVisible) layout.keyboardSpacing else layout.sectionSpacing

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(
                    horizontal = layout.screenPaddingHorizontal,
                    vertical = if (isKeyboardVisible) layout.keyboardScreenPaddingVertical else layout.screenPaddingVertical
                ),
            verticalArrangement = Arrangement.spacedBy(currentSpacing)
        ) {
            // Header with Logo - sakriven kada je tastatura otvorena
            AnimatedVisibility(
                visible = !isKeyboardVisible,
                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit = fadeOut(tween(150)) + shrinkVertically(tween(150))
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val logoPainter = painterResource(id = R.drawable.logo)
                    val aspectRatio = remember(logoPainter) {
                        val width = logoPainter.intrinsicSize.width
                        val height = logoPainter.intrinsicSize.height
                        if (width.isNaN() || height.isNaN() || height == 0f) 3f else width / height
                    }
                    Image(
                        painter = logoPainter,
                        contentDescription = "Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(aspectRatio)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Metraža",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Kalkulator dužine i težine trake",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Input Card
            GlassCard(cornerRadius = layout.cardRadius) {
                Column(
                    modifier = Modifier.padding(
                        horizontal = layout.cardPaddingHorizontal,
                        vertical = if (isKeyboardVisible) layout.keyboardCardPaddingVertical else layout.cardPaddingVertical
                    ),
                    verticalArrangement = Arrangement.spacedBy(if (isKeyboardVisible) layout.keyboardFieldSpacing else layout.sectionSpacing)
                ) {
                    Text(
                        text = "PARAMETRI",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    MaterialGridSelector(
                        label = "Materijal",
                        selectedMaterial = selectedMaterial,
                        onMaterialSelected = { material ->
                            selectedMaterialName = material.name
                            weightFocus.requestFocus()
                        }
                    )

                    LabeledTextField(
                        label = "Ukupna težina (kg)",
                        value = totalWeight,
                        onValueChange = { totalWeight = it },
                        placeholder = "npr. 1000",
                        validation = if (showWeightError) weightValidation else FieldValidation(true),
                        keyboardType = KeyboardType.Decimal,
                        focusRequester = weightFocus,
                        imeAction = ImeAction.Next,
                        onImeAction = { thicknessFocus.requestFocus() },
                        modifier = Modifier.onFocusChanged { if (!it.isFocused) showWeightError = true }
                    )

                    LabeledTextField(
                        label = "Debljina (mm)",
                        value = thickness,
                        onValueChange = { thickness = it },
                        placeholder = "npr. 0.5",
                        validation = if (showThicknessError) thicknessValidation else FieldValidation(true),
                        keyboardType = KeyboardType.Decimal,
                        focusRequester = thicknessFocus,
                        imeAction = ImeAction.Next,
                        onImeAction = { widthFocus.requestFocus() },
                        modifier = Modifier.onFocusChanged { if (!it.isFocused) showThicknessError = true }
                    )

                    LabeledTextField(
                        label = "Širina rezanja (mm)",
                        value = cutWidth,
                        onValueChange = { cutWidth = it },
                        placeholder = "npr. 100",
                        validation = if (showWidthError) widthValidation else FieldValidation(true),
                        keyboardType = KeyboardType.Decimal,
                        focusRequester = widthFocus,
                        imeAction = ImeAction.Next,
                        onImeAction = { rollsFocus.requestFocus() },
                        modifier = Modifier.onFocusChanged { if (!it.isFocused) showWidthError = true }
                    )

                    LabeledTextField(
                        label = "Broj rezova",
                        value = rolls,
                        onValueChange = { rolls = it },
                        placeholder = "npr. 5",
                        validation = if (showRollsError) rollsValidation else FieldValidation(true),
                        keyboardType = KeyboardType.Number,
                        focusRequester = rollsFocus,
                        imeAction = ImeAction.Next,
                        onImeAction = { diameterFocus.requestFocus() },
                        modifier = Modifier.onFocusChanged { if (!it.isFocused) showRollsError = true }
                    )

                    LabeledTextField(
                        label = "Unutrašnji prečnik (opciono)",
                        value = innerDiameter,
                        onValueChange = { innerDiameter = it },
                        placeholder = "npr. 150",
                        validation = if (showDiameterError) diameterValidation else FieldValidation(true),
                        keyboardType = KeyboardType.Decimal,
                        focusRequester = diameterFocus,
                        imeAction = ImeAction.Done,
                        onImeAction = { handleCalculate() },
                        modifier = Modifier.onFocusChanged { if (!it.isFocused) showDiameterError = true }
                    )
                }
            }

            // Calculate Button
            PrimaryButton(
                text = "IZRAČUNAJ",
                onClick = handleCalculate,
                modifier = Modifier.fillMaxWidth(),
                enabled = formValid,
                minHeight = layout.buttonMinHeight,
                contentPadding = layout.buttonContentPadding
            )

            // Message
            AnimatedVisibility(
                visible = messageState.text.isNotBlank(),
                enter = fadeIn(tween(200)) + expandVertically(tween(200)),
                exit = fadeOut(tween(150))
            ) {
                val messageColor = when (messageState.type) {
                    MessageType.SUCCESS -> SuccessColor
                    MessageType.ERROR -> ErrorColor
                    MessageType.NONE -> TextSecondary
                }
                Text(
                    text = messageState.text,
                    color = messageColor,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Results
            if (results.isNotEmpty()) {
                Box(
                    modifier = Modifier.onGloballyPositioned { coordinates ->
                        resultsPosition = coordinates.positionInParent().y.toInt()
                    }
                ) {
                    ResultsSection(results = results, layout = layout)
                }
            }

            // Scroll to results sa snapshotFlow
            LaunchedEffect(shouldScrollToResults) {
                if (shouldScrollToResults) {
                    snapshotFlow { resultsPosition }
                        .filter { it > 0 }
                        .take(1)
                        .collect { position ->
                            scrollState.animateScrollTo(position)
                            shouldScrollToResults = false
                        }
                }
            }
        }
    }
}

@Composable
private fun ResultsSection(
    results: List<CalculationResult>,
    layout: com.metraza.ui.theme.LayoutTokens
) {
    GlassCard(cornerRadius = layout.cardRadius) {
        Column(
            modifier = Modifier.padding(
                horizontal = layout.cardPaddingHorizontal,
                vertical = layout.cardPaddingVertical
            ),
            verticalArrangement = Arrangement.spacedBy(layout.sectionSpacing)
        ) {
            Text(
                text = "REZULTATI",
                style = MaterialTheme.typography.titleMedium,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            results.forEachIndexed { index, result ->
                ResultCard(result = result, layout = layout)
                if (index < results.lastIndex) {
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun ResultCard(result: CalculationResult, layout: com.metraza.ui.theme.LayoutTokens) {
    val rollWord = getRollWord(result.rolls)

    // Build all row texts to find the longest one
    val rows = listOf(
        RowData("Tačna:", result.base, TextPrimary),
        RowData("+10%:", result.plusVariant, SuccessColor),
        RowData("-10%:", result.minusVariant, ErrorColor)
    )

    AccentGlassCard(
        bgColor = PrimaryDark.copy(alpha = 0.3f),
        cornerRadius = layout.resultCardCornerRadius,
        elevation = layout.resultCardElevation
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = layout.resultCardPaddingHorizontal, vertical = layout.resultCardPaddingVertical),
            verticalArrangement = Arrangement.spacedBy(layout.resultRowSpacing)
        ) {
            Text(
                text = "Za ${result.rolls} $rollWord:",
                style = MaterialTheme.typography.bodyLarge,
                color = Primary,
                fontWeight = FontWeight.SemiBold
            )

            ResultRowsGroup(rows = rows, layout = layout)
        }
    }
}

@Immutable
private data class RowData(
    val label: String,
    val variant: com.metraza.data.RollResult,
    val labelColor: androidx.compose.ui.graphics.Color
)

private fun buildRowText(label: String, variant: com.metraza.data.RollResult): String {
    val lengthText = if (variant.outerDiameterMm != null) {
        "${formatNumber(variant.lengthM)} m (Ø ${variant.outerDiameterMm.roundToInt()} mm)"
    } else {
        "${formatNumber(variant.lengthM)} m"
    }
    val weightText = "${formatNumber(variant.weightKg)} kg"
    return "$label  $lengthText  •  $weightText"
}

@Composable
private fun ResultRowsGroup(rows: List<RowData>, layout: com.metraza.ui.theme.LayoutTokens) {
    val textMeasurer = rememberTextMeasurer()
    val baseStyle = MaterialTheme.typography.bodyMedium

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        val maxWidthPx = constraints.maxWidth.toFloat()

        // Find font size that fits ALL rows using binary search
        val fontSize = remember(rows, maxWidthPx) {
            var minSize = 8f
            var maxSize = 16f
            var optimalSize = minSize

            while (maxSize - minSize > 0.5f) {
                val midSize = (minSize + maxSize) / 2f
                val allFit = rows.all { row ->
                    val fullText = buildRowText(row.label, row.variant)
                    val measuredWidth = textMeasurer.measure(
                        text = fullText,
                        style = TextStyle(fontSize = midSize.sp)
                    ).size.width.toFloat()
                    measuredWidth <= maxWidthPx
                }
                if (allFit) {
                    optimalSize = midSize
                    minSize = midSize
                } else {
                    maxSize = midSize
                }
            }
            optimalSize.sp
        }

        Column(verticalArrangement = Arrangement.spacedBy(layout.resultRowSpacing)) {
            rows.forEach { row ->
                ResultRowContent(
                    label = row.label,
                    variant = row.variant,
                    labelColor = row.labelColor,
                    fontSize = fontSize,
                    baseStyle = baseStyle
                )
            }
        }
    }
}

@Composable
private fun ResultRowContent(
    label: String,
    variant: com.metraza.data.RollResult,
    labelColor: androidx.compose.ui.graphics.Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
    baseStyle: androidx.compose.ui.text.TextStyle
) {
    val lengthText = if (variant.outerDiameterMm != null) {
        "${formatNumber(variant.lengthM)} m (Ø ${variant.outerDiameterMm.roundToInt()} mm)"
    } else {
        "${formatNumber(variant.lengthM)} m"
    }
    val weightText = "${formatNumber(variant.weightKg)} kg"

    val valueText = buildAnnotatedString {
        withStyle(SpanStyle(color = TextPrimary)) {
            append(lengthText)
        }
        withStyle(SpanStyle(color = TextSecondary)) {
            append(" • ")
        }
        withStyle(SpanStyle(color = Secondary, fontWeight = FontWeight.Medium)) {
            append(weightText)
        }
    }

    Column {
        // Glavni red: Label + Dužina (Ø) + Težina
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = baseStyle.copy(fontSize = fontSize),
                color = labelColor,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = valueText,
                style = baseStyle.copy(fontSize = fontSize),
                maxLines = 1
            )
        }

        // Čap red (samo ako ima unutrašnji prečnik)
        if (variant.outerDiameterMm != null) {
            val capCm = variant.outerDiameterMm / 2.0 / 10.0  // poluprečnik u cm
            Text(
                text = "Čap: ${formatNumber(capCm)} cm",
                style = baseStyle.copy(fontSize = fontSize * 0.85f),
                color = Primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 2.dp),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
