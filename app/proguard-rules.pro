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

# Sačuvati AppConstants (MATERIAL_DENSITIES mapa i LayoutTokens)
# Density mapa se koristi runtime za lookup po string ključu.
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
