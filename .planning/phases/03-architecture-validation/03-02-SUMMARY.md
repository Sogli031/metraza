---
phase: 03-architecture-validation
plan: 02
subsystem: ui-layer
tags: [refactoring, validation, state-management, layout-tokens]
dependency_graph:
  requires:
    - 03-01 (InputState, FieldValidation, Validation.kt, doCalculate)
    - 02-01 (Material enum, layout tokens)
  provides:
    - Refaktorisan CalculatorScreen sa InputState integration
    - Blur-based validation UI feedback
    - Disabled button dok forma nije validna
    - Layout tokens umesto magic numbers
  affects:
    - app/src/main/java/com/metraza/ui/screens/CalculatorScreen.kt
    - app/proguard-rules.pro
tech_stack:
  added: []
  patterns:
    - Derived state pattern (InputState computed od rememberSaveable strings)
    - Blur-based validation triggering
    - Real-time form validity checking
key_files:
  created: []
  modified:
    - app/src/main/java/com/metraza/ui/screens/CalculatorScreen.kt: "Refactored sa InputState, validation, disabled button, layout tokens"
    - app/src/main/java/com/metraza/data/Calculator.kt: "Dodati java.util.Locale import (bug fix)"
    - app/proguard-rules.pro: "Keep rules za InputState, FieldValidation, ValidationKt"
decisions:
  - "rememberSaveable čuva pojedinačne String promenljive, InputState je derived - Bundle serialization pattern"
  - "Blur-based error display - error se prikazuje tek nakon što korisnik napusti polje"
  - "formValid je real-time - omogućava instant feedback za button enable/disable"
  - "Layout tokens za sve keyboard spacing vrednosti - eliminise magic numbers"
metrics:
  duration: "~4 min"
  completed_at: "2026-02-09T12:13:17Z"
---

# Phase 3 Plan 2: UI Integration - InputState, Validation i Layout Tokens

**InputState integration sa blur-based validation, disabled button logic i layout tokens umesto magic numbers**

## Tasks Completed

### Task 1: Refaktorisati CalculatorScreen na InputState, validaciju i layout tokens
**Commit:** `13b871a`
**Type:** feat

**Changes:**
- Dodati InputState derived state za grupisanje svih input polja
- Integrisati validation funkcije sa blur-based error prikaz
- IZRAČUNAJ dugme disabled dok forma nije validna
- Zameniti sve keyboard magic numbers sa layout tokens
- ResultCard koristi layout tokens za cornerRadius, elevation, padding
- Popraviti Calculator.kt - dodati import java.util.Locale (bug fix)

**Files modified:**
- `app/src/main/java/com/metraza/ui/screens/CalculatorScreen.kt` (85 insertions, 36 deletions)
- `app/src/main/java/com/metraza/data/Calculator.kt` (1 insertion - import fix)

**Key implementation details:**
- InputState je derived (computed) od rememberSaveable String promenljivih - ne može biti rememberSaveable jer nije Parcelable
- Svaki LabeledTextField dobio `validation` parametar koji se aktivira tek nakon blur-a (onFocusChanged)
- formValid koristi isFormValid(inputState) za real-time button enable/disable
- handleCalculate poziva doCalculate(inputState) pure function umesto inline parsiranja
- Keyboard spacing: layout.keyboardSpacing, layout.keyboardScreenPaddingVertical, layout.keyboardCardPaddingVertical, layout.keyboardFieldSpacing
- Result cards: layout.resultCardCornerRadius, layout.resultCardElevation, layout.resultCardPaddingHorizontal/Vertical, layout.resultRowSpacing

### Task 2: Ažurirati ProGuard rules za nove data klase i Validation
**Commit:** `5c4664f`
**Type:** chore

**Changes:**
- Dodati keep rules za InputState data class
- Dodati keep rules za FieldValidation data class
- Dodati keep rules za ValidationKt funkcije

**Files modified:**
- `app/proguard-rules.pro` (10 insertions)

**Keep rules added:**
```
-keep class com.metraza.data.InputState { *; }
-keep class com.metraza.data.FieldValidation { *; }
-keep class com.metraza.data.ValidationKt { *; }
```

## Deviations from Plan

### Auto-fixed Issues

**1. [Rule 1 - Bug] Missing java.util.Locale import u Calculator.kt**
- **Found during:** Task 1 refactoring
- **Issue:** formatNumber() koristi Locale.US ali import nedostaje - kod se ne bi kompajlirao
- **Fix:** Dodati `import java.util.Locale` u Calculator.kt
- **Files modified:** app/src/main/java/com/metraza/data/Calculator.kt
- **Commit:** 13b871a (uključen u Task 1 commit)

## Verification Results

✅ CalculatorScreen: `val inputState = InputState(...)` postoji
✅ CalculatorScreen: `doCalculate(inputState)` poziv postoji (ne inline parsiranje)
✅ CalculatorScreen: `enabled = formValid` na PrimaryButton
✅ CalculatorScreen: Svih 5 LabeledTextField ima `validation =` parametar
✅ CalculatorScreen: Svih 5 LabeledTextField ima `Modifier.onFocusChanged`
✅ CalculatorScreen: `handleCalculate` aktivira sve `showXxxError = true`
✅ CalculatorScreen: Keyboard magic numbers zamenjeni layout tokens
✅ CalculatorScreen: ResultCard koristi layout tokens za cornerRadius, elevation, padding
✅ proguard-rules.pro: Keep rules za InputState, FieldValidation, ValidationKt
✅ Nema `parseDouble`/`parseInt`/`computeAllResults` import-a u CalculatorScreen

## Success Criteria Met

- [x] InputState data class se koristi u CalculatorScreen kao derived state
- [x] IZRAČUNAJ dugme je disabled dok sva obavezna polja nisu popunjena i validna
- [x] Validacioni errori se pokazuju na blur (kada korisnik napusti polje) - crveni border + error message
- [x] Na submit, svi errori se prikazuju odjednom
- [x] doCalculate() pure function poziv umesto inline parse logike
- [x] Nema hard-coded keyboard/result card .dp vrednosti u CalculatorScreen
- [x] ProGuard rules pokrivaju nove klase
- [x] Message state se clear-uje na početku svake nove kalkulacije

## Key Learnings

1. **Derived state pattern** - InputState se ne čuva direktno u rememberSaveable jer nije Parcelable. Umesto toga, čuvaju se pojedinačni String-ovi, a InputState se recompute-uje kao derived state. Isti pattern kao Material enum u Phase 02.

2. **Blur-based validation UX** - Error se prikazuje tek nakon što korisnik napusti polje (onFocusChanged). Na submit, svi errori se aktiviraju odjednom. Ovo sprečava iritantnu UX gde se error prikazuje dok korisnik još kuca.

3. **Real-time vs blur-based** - formValid je real-time (button se odmah disable-uje), ali error poruke su blur-based. Najbolje od oba pristupa.

4. **Layout token propagacija** - ResultCard i ResultRowsGroup moraju primiti layout parametar kako bi koristili layout tokens. Ovo je jednostavna pattern propagacija kroz Composable hijerarhiju.

5. **Missing imports kao bug** - Kotlin ponekad skriva missing imports dok se ne pokrene build. Dodavanje import-a za java.util.Locale je bio bug fix, ne planirani task.

## Impact

**User experience:**
- Korisnik dobija instant vizuelni feedback za validacione greške
- Dugme je disabled dok forma nije validna - sprečava frustraciju od pokušaja submit-a
- Blur-based error display - manje iritantno nego real-time error poruke

**Code quality:**
- CalculatorScreen više ne sadrži inline parsiranje logiku - to je sada u doCalculate pure function
- InputState grupiše svu input state - lakše je proslediti u funkcije
- Layout tokens eliminišu magic numbers - lakše održavanje i consistency

**Architecture:**
- Validacija je kompletno odvojena od UI logike
- Pure function poziv (doCalculate) umesto inline imperative code
- Sve komponente koriste layout tokens - konzistentno ponašanje keyboard spacing-a

## Self-Check

**Created files exist:**
- N/A - nema novih fajlova

**Modified files exist:**
✅ app/src/main/java/com/metraza/ui/screens/CalculatorScreen.kt
✅ app/src/main/java/com/metraza/data/Calculator.kt
✅ app/proguard-rules.pro

**Commits exist:**
✅ 13b871a: feat(03-02): refaktorisati CalculatorScreen na InputState, validaciju i layout tokens
✅ 5c4664f: chore(03-02): ažurirati ProGuard rules za InputState, FieldValidation i Validation

## Self-Check: PASSED
