# Metraza - Refactoring & Bug Fixes

## What This Is

Industrijski kalkulator za proračun dužine i težine metalnih traka/vrpci raspoređenih po rolnama. Koristi se u proizvodnji traka od bakra i legura (CuZn10-CuZn37) za izračunavanje specifikacija za kupce. Trenutno ima kritične bagove u gustinama materijala koji direktno utiču na tačnost komercijlnih porudžbina.

## Core Value

Tačni proračuni težine i dužine metalnih traka sa ispravnim gustinama materijala - klijenti moraju dobiti precizne specifikacije za proizvodnju.

## Requirements

### Validated

(None yet - existing codebase has bugs, no features validated)

### Active

**Kritično - Tačnost Proračuna:**
- [ ] CALC-01: Popravljene gustine materijala (CuZn15: 8750, CuZn20: 8670, CuZn30: 8530 kg/m³)
- [ ] CALC-02: Outer diameter precision loss fix (BigDecimal ili bolja Double aritmetika)
- [ ] CALC-03: Material kao enum umesto string (eliminisati string transformacije)

**Validacija i UX:**
- [ ] VALID-01: Pre-flight validacija polja (disable CALCULATE dugme dok nisu sva polja validna)
- [ ] VALID-02: Error states na input poljima (crveni border, error text)
- [ ] VALID-03: Message state lifecycle fix (clear messageState correctly)

**Bezbednost:**
- [ ] SEC-01: ProGuard keep rules za Calculator i Models klase
- [ ] SEC-02: Input sanitization (Material enum eliminiše string manipulation)

**Arhitektura:**
- [ ] ARCH-01: Ekstrakcija state iz CalculatorScreen (data class za sva polja)
- [ ] ARCH-02: doCalculate() kao pure function (testable)
- [ ] ARCH-03: Sve layout vrednosti centralizovane u AppConstants (ukloniti magic numbers)

**Performance:**
- [ ] PERF-01: Binary search za font sizing (umesto while loop)
- [ ] PERF-02: LazyColumn za rezultate (skalira do 100+ rolls)
- [ ] PERF-03: Memoizacija RowData objekata (reduce recompositions)
- [ ] PERF-04: Scroll-to-results timing fix (LaunchedEffect umesto delay)

**Nove Funkcije:**
- [ ] FEAT-01: Reset/Undo button (clear sva polja i rezultate)
- [ ] FEAT-02: Share/Copy results (Intent ACTION_SEND za formatted text)

**Testovi:**
- [ ] TEST-01: Unit tests za sve Calculator funkcije (parseDouble, parseInt, computeAllResults, calculateOuterDiameter, getDensity, getRollWord)
- [ ] TEST-02: Integration tests za CalculatorScreen (field navigation, validation, calculation flow, results rendering)
- [ ] TEST-03: Material density validation tests (verify all 6 materials ima tačne vrednosti)
- [ ] TEST-04: Layout responsive tests (compact vs regular, keyboard visibility)

**Dependencies:**
- [ ] DEP-01: Update Compose BOM sa 2024.12.01 na najnoviji stable
- [ ] DEP-02: Update Kotlin sa 2.0.21 na najnoviji 2.0.x
- [ ] DEP-03: Update AGP sa 8.7.3 na najnoviji 8.x

### Out of Scope

- **Debug signing key promenu** — ostaje debug key po zahtevu korisnika
- **Calculation history/persistence** — fokus na bagove i arhitekturu prvo
- **Dark/Light mode toggle** — dark only prema specifikaciji
- **Multi-screen navigation** — single screen calculator po dizajnu
- **Real-time collaboration** — nije potrebno za industrijsku upotrebu
- **Cloud sync** — lokalni kalkulator je dovoljan

## Context

**Brownfield projekat:**
- Postojeća kodna baza: CalculatorScreen (~540 linija), Calculator.kt (pure functions), Models.kt
- Codebase map kreiran: `.planning/codebase/` sa ARCHITECTURE, STACK, CONCERNS dokumentima
- Kritični bagovi identifikovani: pogrešne gustine za 3 materijala (CuZn15, CuZn20, CuZn30) - razlika 120-140 kg/m³

**Komercijalni uticaj:**
- Pogrešni proračuni direktno utiču na specifikacije za kupce
- Klijenti dobijaju pogrešne dužine traka i prečnike rolni
- Greške u gustinama su tehnički dug koji mora odmah da se ispravi

**Tehnički kontekst:**
- Monolitna arhitektura: sav state i logika u jednoj Composable funkciji
- Nema testova: ni unit ni integration
- Performance problemi: font sizing sa while loop, Column umesto LazyColumn
- Fragile areas: dual formatting logika, scroll timing sa hard-coded delay

**Korisničke potrebe:**
- Reset funkcija za brze serijske proračune
- Mogućnost deljenja rezultata (email, messaging)
- Jasna validacija (ne čekati CALCULATE klik da bi videli greške)

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
| Material kao enum umesto string | Eliminiše string manipulation bugs, type-safe, lakše testiranje | — Pending |
| ProGuard keep rules odmah | Release builds mogu da strip-uju Math funkcije bez rules | — Pending |
| LazyColumn za rezultate | Skalira do 100+ rolls, sprečava scroll lag | — Pending |
| Binary search za font sizing | Reduces O(n) na O(log n), manje measure() poziva | — Pending |
| SharedPreferences preskoči | Fokus na bugove i arhitekturu prvo, istorija u v2 | — Pending |
| Dark-only theme zadrži | Per specifikacija, nema potrebe za light mode | ✓ Good |
| Debug key zadrži | Per zahtevu korisnika (ne menjati signing config) | ✓ Good |

---
*Last updated: 2026-02-09 after initialization*
