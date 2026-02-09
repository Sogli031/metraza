# Metraza - Refactoring & Bug Fixes

## What This Is

Industrijski kalkulator za proračun dužine i težine metalnih traka/vrpci raspoređenih po rolnama. Koristi se u proizvodnji traka od bakra i legura (CuZn10-CuZn37) za izračunavanje specifikacija za kupce. Aplikacija pruža tačne proračune sa ispravnim gustinama materijala, real-time validacijom, i funkcijama za reset i deljenje rezultata.

## Core Value

Tačni proračuni težine i dužine metalnih traka sa ispravnim gustinama materijala - klijenti moraju dobiti precizne specifikacije za proizvodnju.

## Requirements

### Validated

**v1.0 - Tačnost Proračuna:**
- ✓ CALC-01: Popravljene gustine materijala (CuZn15: 8750, CuZn20: 8670, CuZn30: 8530 kg/m³) — v1.0
- ✓ CALC-02: Outer diameter precision koristi volume conservation formulu — v1.0
- ✓ CALC-03: Material kao enum umesto string (type-safe, eliminisane string transformacije) — v1.0

**v1.0 - Validacija i UX:**
- ✓ VALID-01: Pre-flight validacija polja (disabled CALCULATE dugme dok forma nije validna) — v1.0
- ✓ VALID-02: Error states na input poljima (blur-based validation, error messages) — v1.0
- ✓ VALID-03: Message state lifecycle (pravilno clear-ovanje) — v1.0

**v1.0 - Bezbednost:**
- ✓ SEC-01: ProGuard keep rules za Calculator, Models, Validation, AppConstants — v1.0
- ✓ SEC-02: Input sanitization (Material enum eliminiše string manipulation) — v1.0

**v1.0 - Arhitektura:**
- ✓ ARCH-01: InputState data class izdvojena iz CalculatorScreen — v1.0
- ✓ ARCH-02: doCalculate() kao pure function (prima InputState, vraća CalculationResult) — v1.0
- ✓ ARCH-03: Layout tokens centralizovani u AppConstants (eliminiši magic numbers) — v1.0

**v1.0 - Performance:**
- ✓ PERF-01: Binary search za font sizing (O(log n) umesto O(n)) — v1.0
- ✓ PERF-02: LazyColumn za rezultate (skalira do 100+ rolls) — v1.0
- ✓ PERF-03: @Immutable annotations za RowData (reduce recompositions) — v1.0
- ✓ PERF-04: snapshotFlow za scroll-to-results (reaguje na actual UI state) — v1.0

**v1.0 - Nove Funkcije:**
- ✓ FEAT-01: Reset button (clear sva polja i rezultate) — v1.0
- ✓ FEAT-02: Share/Podeli results (Intent.createChooser sa formatted text) — v1.0

**v1.0 - Testovi:**
- ✓ TEST-01: 84 unit tests za Calculator, Validation, Models (JUnit 4, Truth) — v1.0
- ✓ TEST-02: 11 integration tests za CalculatorScreen UI flow (Compose testing) — v1.0
- ✓ TEST-03: Material density verification tests (svih 6 materijala sa tačnim gustinama) — v1.0
- ✓ TEST-04: Integration tests pokrivaju keyboard visibility i material selection — v1.0 (adjusted scope)

**v1.0 - Dependencies:**
- ✓ DEP-01: Compose BOM ažuriran na 2025.06.00 — v1.0
- ✓ DEP-02: Kotlin 2.0.21 zadržan (najnoviji stable) — v1.0
- ✓ DEP-03: AGP ažuriran na 8.9.0, Gradle na 8.11.1 — v1.0

### Active

(None - v1.0 milestone complete. Use /gsd:new-milestone za sledeću verziju.)

### Out of Scope

- **Debug signing key promenu** — ostaje debug key po zahtevu korisnika
- **Calculation history/persistence** — fokus na bagove i arhitekturu prvo
- **Dark/Light mode toggle** — dark only prema specifikaciji
- **Multi-screen navigation** — single screen calculator po dizajnu
- **Real-time collaboration** — nije potrebno za industrijsku upotrebu
- **Cloud sync** — lokalni kalkulator je dovoljan

## Context

**Shipped v1.0 (2026-02-09):**
- 2,606 LOC Kotlin (production + tests)
- 95 testova: 84 unit (Calculator, Validation, Models) + 11 integration (CalculatorScreen UI)
- Tech stack: Jetpack Compose (Material 3), Compose BOM 2025.06.00, Kotlin 2.0.21, AGP 8.9.0
- 8 plans izvršeno kroz 5 phases (~2.4 sata execution time)
- 27 commits sa atomic task-level granularity

**Struktura:**
- **Data layer:** Calculator.kt (pure functions), Validation.kt (validation logic), Models.kt (Material enum, data classes)
- **UI layer:** CalculatorScreen.kt (InputState pattern), MaterialGridSelector, LabeledTextField, GlassCard komponente
- **Theme:** AppConstants.kt (centralizovani layout tokens, colors), MetrazaTheme (dark-only Material 3)
- **Tests:** CalculatorTest, ValidationTest, ModelsTest (unit), CalculatorScreenTest (integration)

**Rešeni problemi:**
- ✓ Ispravne gustine za CuZn15/20/30 (8750, 8670, 8530 kg/m³)
- ✓ Type-safe Material enum (eliminisan string manipulation bug risk)
- ✓ Pure function architecture (testable, maintainable)
- ✓ Real-time validation (blur-based error display, disabled button)
- ✓ Performance optimizacije (binary search, LazyColumn, memoization)
- ✓ Reset i Share funkcije (serijski proračuni, deljenje rezultata)

**Poznati issues / Tech debt:**
- Nema: v1.0 milestone je kompletan bez poznatih bug-ova ili tech debt-a
- Production-ready za industrijsku upotrebu

## Constraints

- **Tech stack**: Kotlin + Jetpack Compose (Material 3) - no change allowed
- **SDK levels**: minSdk 24, targetSdk 34, compileSdk 35 - maintain compatibility
- **Theme**: Dark only (warm black palette, not cool blue) - per specification
- **Architecture**: Single screen, no persistence, pure calculator - by design
- **Language**: Srpski (ekavica) sa dijakritikom u svim UI tekstovima
- **Build**: Gradle 8.9, JDK 17 - stable versions
- **No networking**: Offline calculator, no API calls
- **No database**: Stateless, state lost on app close (by design)

## Key Decisions

| Decision | Rationale | Outcome |
|----------|-----------|---------|
| Material kao enum umesto string | Eliminiše string manipulation bugs, type-safe, lakše testiranje | ✓ Good - 16 unit tests za Material, compiler-enforced correctness |
| ProGuard keep rules odmah | Release builds mogu da strip-uju Math funkcije bez rules | ✓ Good - Rules dodate u Phase 1, expanded u Phase 3-4 |
| LazyColumn za rezultate | Skalira do 100+ rolls, sprečava scroll lag | ✓ Good - Renderuje samo vidljive items, smooth scrolling |
| Binary search za font sizing | Reduces O(n) na O(log n), manje measure() poziva | ✓ Good - ~4 iteracije umesto ~16, measurably faster |
| InputState data class pattern | Odvoji state od UI, testable doCalculate(), clear validation | ✓ Good - 25 validation tests, pure function architecture |
| JUnit 4 + Truth za testove | Android standard, brže setup, fluent assertions | ✓ Good - 95 tests written, Truth error messages superior |
| Blur-based validation | Instant feedback za button enable, errors nakon blur | ✓ Good - Ne smeta tokom unosa, jasna indikacija problema |
| snapshotFlow za scroll timing | Reaguje na actual UI state umesto hard-coded delay | ✓ Good - Eliminisan race condition, pouzdanije |
| Intent.createChooser za Share | Android best practice, prikazuje sve apps | ✓ Good - Radi sa email, messaging, notes apps |
| SharedPreferences preskoči | Fokus na bugove i arhitekturu prvo, istorija u v2 | ✓ Good - v1.0 ship bez scope creep |
| Dark-only theme zadrži | Per specifikacija, nema potrebe za light mode | ✓ Good - Consistent sa industry apps |
| Debug key zadrži | Per zahtevu korisnika (ne menjati signing config) | ✓ Good - No issues |

---
*Last updated: 2026-02-09 after v1.0 milestone completion*
