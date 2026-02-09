---
phase: 02-critical-calculation-fixes
plan: 01
subsystem: calculation-core
tags: [bugfix, type-safety, security, correctness]
dependency_graph:
  requires: []
  provides:
    - Material enum sa ispravnim gustinama za svih 6 legura
    - Type-safe material handling bez string manipulation
    - ProGuard zaštita za Material enum
  affects:
    - data/Calculator.kt - koristi Material enum umesto String
    - ui/components/MaterialGridSelector.kt - emituje Material enum
    - ui/screens/CalculatorScreen.kt - koristi Material? state
    - ui/theme/AppConstants.kt - više nema MATERIAL_DENSITIES mapu
tech_stack:
  added:
    - Material enum class (Kotlin 2.0+ entries API)
  patterns:
    - Type-safe enum umesto String konstanti
    - Derived state pattern za rememberSaveable (String → Material? konverzija)
    - Companion object za enum lookup
key_files:
  created: []
  modified:
    - app/src/main/java/com/metraza/data/Models.kt - dodati Material enum
    - app/src/main/java/com/metraza/data/Calculator.kt - koristiti Material tip
    - app/src/main/java/com/metraza/ui/components/MaterialGridSelector.kt - Material enum UI
    - app/src/main/java/com/metraza/ui/screens/CalculatorScreen.kt - Material? state
    - app/src/main/java/com/metraza/ui/theme/AppConstants.kt - ukloniti String konstante
    - app/proguard-rules.pro - keep rules za Material enum
decisions:
  - decision: "Material enum umesto String konstanti"
    rationale: "Eliminiše string manipulation bugs, type-safe, kompajler garantuje ispravnost"
    alternatives: ["Sealed class", "Zadržati String sa validacijom"]
    chosen: "Enum - najjednostavnije za ovaj use case"
  - decision: "rememberSaveable čuva String, derived state je Material?"
    rationale: "Enum nije Parcelable po default-u, Bundle zahteva serializable tipove"
    alternatives: ["Implementirati Parcelable za Material", "Koristiti remember umesto rememberSaveable"]
    chosen: "String + derived state - minimalna promena, siguran pattern"
  - decision: "Koristiti entries umesto values()"
    rationale: "entries je Kotlin 2.0+ API, values() je deprecated"
    alternatives: ["Zadržati values()"]
    chosen: "entries - moderniji API, budućnost Kotlin-a"
metrics:
  duration_minutes: 3
  tasks_completed: 2
  files_modified: 6
  commits: 2
  completed_date: 2026-02-09
---

# Phase 2 Plan 1: Material Enum & Density Corrections Summary

Kreiran Material enum sa ispravnim gustinama (CuZn15: 8750, CuZn20: 8670, CuZn30: 8530), eliminisana string manipulation za materijale, dodata ProGuard zaštita, konvertovan ceo pipeline na type-safe enum.

## Problem

Tri materijala su imala netačne gustine u MATERIAL_DENSITIES mapi:
- CuZn15: 8630 kg/m³ umesto 8750 (razlika: 120 kg/m³)
- CuZn20: 8530 kg/m³ umesto 8670 (razlika: 140 kg/m³)
- CuZn30: 8403 kg/m³ umesto 8530 (razlika: 127 kg/m³)

Ove greške direktno utiču na proračun dužine i težine, što dovodi do netačnih specifikacija za klijente. String-based material handling je bio ranjiv na injection i type errors.

## Solution

### Task 1: Material Enum & Calculator Update

Kreiran `Material` enum class u Models.kt sa:
- 6 vrednosti (Cu, CuZn10, CuZn15, CuZn20, CuZn30, CuZn37)
- `density: Int` property sa ISPRAVNIM gustinama
- `displayName: String` computed property
- `fromDisplayName(name: String): Material?` companion funkcija
- Koristi `entries` API umesto deprecated `values()`

Ažuriran Calculator.kt:
- `parseMaterial()` sada vraća `Material` tip
- `getDensity()` prima `Material` i vraća `material.density` direktno
- `computeAllResults()` i `calculateRollMetrics()` primaju `Material` parametar
- Uklonjeni import-i za MATERIAL_DENSITIES, MATERIAL_PLACEHOLDER, java.util.Locale

**Commit:** bf5ee72 - feat(02-01): kreirati Material enum sa ispravnim gustinama

### Task 2: UI Conversion & ProGuard Protection

Konvertovani UI komponenti:

**MaterialGridSelector.kt:**
- Prima `selectedMaterial: Material?` umesto `String`
- Emituje `Material` preko `onMaterialSelected: (Material) -> Unit`
- Koristi `Material.entries` umesto MATERIAL_DISPLAY_NAMES
- MaterialChip prikazuje `material.displayName`

**CalculatorScreen.kt:**
- `selectedMaterialName: String` (rememberSaveable)
- `selectedMaterial: Material?` (derived state iz selectedMaterialName)
- `onMaterialSelected` čuva `material.name` u selectedMaterialName
- `doCalculate` koristi `selectedMaterial ?: throw` validaciju
- Uklonjeni import-i za MATERIAL_PLACEHOLDER i parseMaterial

**AppConstants.kt:**
- Obrisana MATERIAL_DENSITIES mapa (12 linija)
- Obrisana MATERIAL_DISPLAY_NAMES lista (6 vrednosti)
- Obrisana MATERIAL_PLACEHOLDER konstanta

**proguard-rules.pro:**
- Dodati `-keep enum com.metraza.data.Material` blok
- Dodati `-keepclassmembers` za values() i valueOf()
- Ažuriran komentar za AppConstantsKt (bez MATERIAL_DENSITIES reference)

**Commit:** 228bc8c - feat(02-01): konvertovati UI komponente na Material enum i ažurirati ProGuard

## Verification Results

All success criteria met:

### Density Correctness
- ✓ CuZn15 gustina je 8750 (bilo 8630)
- ✓ CuZn20 gustina je 8670 (bilo 8530)
- ✓ CuZn30 gustina je 8530 (bilo 8403)
- ✓ Stare vrednosti (8630, 8403) ne postoje nigde u kodu

### Type Safety
- ✓ Material je enum class sa density property u Models.kt
- ✓ Calculator.kt ne koristi MATERIAL_DENSITIES mapu
- ✓ getDensity() vraća material.density direktno
- ✓ MaterialGridSelector prima i emituje Material enum
- ✓ CalculatorScreen koristi Material? tip za state

### Code Cleanup
- ✓ MATERIAL_DENSITIES mapa ne postoji
- ✓ MATERIAL_DISPLAY_NAMES lista ne postoji
- ✓ MATERIAL_PLACEHOLDER konstanta ne postoji
- ✓ Nema String manipulation za materijale nigde u kodu

### Security & Reliability
- ✓ ProGuard keep rules za Material enum postoje
- ✓ calculateOuterDiameter() je nepromenjena (Double precision adekvatna)
- ✓ Nema mogućnosti za string injection - UI prosleđuje enum vrednosti

## Deviations from Plan

None - plan executed exactly as written.

## Technical Debt & Future Work

None introduced. This change:
- Eliminates string manipulation bugs
- Reduces code complexity (Map lookup → property access)
- Makes material handling compile-time safe
- Improves performance (no runtime Map lookup)

## Impact Assessment

**Correctness:** Critical bug fixed. All materials now have correct densities.

**Type Safety:** Material handling is now compile-time safe. Impossible to pass invalid material.

**Security:** String injection attack surface eliminated. UI can only emit valid Material enum values.

**Performance:** Slight improvement - direct property access instead of Map lookup with string normalization.

**Maintainability:** Adding new material requires single change in enum (density automatically available everywhere).

## Self-Check: PASSED

### Created Files
All expected files confirmed to exist.

### Commits
- FOUND: bf5ee72 (Task 1 - Material enum & Calculator update)
- FOUND: 228bc8c (Task 2 - UI conversion & ProGuard protection)

### Code Verification
- VERIFIED: Material enum exists with 6 values and correct densities
- VERIFIED: Calculator.kt uses Material type throughout
- VERIFIED: UI components use Material enum
- VERIFIED: No String-based material code remains
- VERIFIED: ProGuard keep rules protect Material enum
- VERIFIED: Old incorrect densities (8630, 8403) removed from codebase
