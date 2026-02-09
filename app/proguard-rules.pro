# Metraza ProGuard pravila

# Sačuvati Calculator klasu i sve njene public metode
# Calculator.kt sadrži pure funkcije za proračun težine i dužine traka.
# R8 može da ukloni ili preimenuje ove funkcije jer se pozivaju
# iz Compose lambda-a koje R8 ne prati uvek korektno.
-keep class com.metraza.data.CalculatorKt {
    public *;
}

# Sačuvati data klase (RollResult, CalculationResult, MessageState)
# Data klase koriste componentN() i copy() metode koje R8 može da ukloni.
-keep class com.metraza.data.RollResult { *; }
-keep class com.metraza.data.CalculationResult { *; }
-keep class com.metraza.data.MessageState { *; }
-keep class com.metraza.data.MessageType { *; }

# Sačuvati Material enum i sve njene metode i polja
# Material enum sadrži density property i companion object za fromDisplayName().
# R8 može da ukloni values()/valueOf() metode ili obfuskuje enum polja.
-keep enum com.metraza.data.Material {
    public *;
    <fields>;
}
-keepclassmembers enum com.metraza.data.Material {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Sačuvati AppConstants (LayoutTokens)
-keep class com.metraza.ui.theme.AppConstantsKt {
    public *;
}
-keep class com.metraza.ui.theme.LayoutTokens { *; }

# Sačuvati kotlin.math funkcije koje Calculator koristi
-keep class kotlin.math.MathKt { *; }

# Sprečiti uklanjanje java.lang.Math metoda koje se koriste u calculateOuterDiameter()
-keep class java.lang.Math {
    public static double sqrt(double);
}

# =============================
# InputState i FieldValidation - data klase za state management
# Phase 3: Architecture & Validation
# =============================
-keep class com.metraza.data.InputState { *; }
-keep class com.metraza.data.FieldValidation { *; }

# Validation pure funkcije - pozivaju se iz Compose derivedStateOf
-keep class com.metraza.data.ValidationKt { *; }

# =============================
# Phase 4: Performance & Features
# =============================
# formatResultsForShare - poziva se iz Compose lambda u CalculatorScreen
# CalculatorKt keep rule već pokriva sve public funkcije u Calculator.kt,
# što uključuje i formatResultsForShare. Dokumentovano ovde za jasnoću.

# SecondaryButton - disabled state colors, enabled parametar
# Koristi se kao Composable iz UI, ali eksplicitno čuvamo za safety.
-keep class com.metraza.ui.components.ButtonsKt { public *; }
