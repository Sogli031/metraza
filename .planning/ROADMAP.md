# Roadmap: Metraza Refactoring

## Overview

Refaktoring industrijskog kalkulatora za metalne trake - ispravljanje kritičnih bagova u gustinama materijala, modernizacija arhitekture, optimizacija performansi, i dodavanje kompletne test pokrivenosti. Faze su strukturirane da prvo reše komercijalno-kritične probleme (pogrešni proračuni), zatim poboljšaju održivost koda (arhitektura), i na kraju dodaju funkcije koje olakšavaju rad (reset/share) uz potpunu test pokrivenost.

## Phases

**Phase Numbering:**
- Integer phases (1, 2, 3): Planned milestone work
- Decimal phases (2.1, 2.2): Urgent insertions (marked with INSERTED)

Decimal phases appear between their surrounding integers in numeric order.

- [x] **Phase 1: Foundation & Security** - Dependency updates i ProGuard rules (2026-02-09)
- [x] **Phase 2: Critical Calculation Fixes** - Ispravke gustina, preciznosti, Material enum (2026-02-09)
- [x] **Phase 3: Architecture & Validation** - State refaktoring i input validacija (2026-02-09)
- [x] **Phase 4: Performance & Features** - Optimizacije i nove funkcije (Reset/Share) (2026-02-09)
- [ ] **Phase 5: Test Coverage** - Kompletna test pokrivenost (unit + integration)

## Phase Details

### Phase 1: Foundation & Security
**Goal**: Sigurna build infrastruktura sa najnovijim dependency verzijama
**Depends on**: Nothing (first phase)
**Requirements**: DEP-01, DEP-02, DEP-03, SEC-01
**Success Criteria** (what must be TRUE):
  1. Compose BOM, Kotlin, i AGP su na najnovijim stable verzijama
  2. Release build se uspešno kreira bez ProGuard warnings
  3. Calculator i Models klase su zaštićene keep rules pravilima
  4. Build process radi bez grešaka sa novim dependency verzijama
**Plans**: 1 plan

Plans:
- [ ] 01-01-PLAN.md -- Azuriranje dependency verzija (Gradle, AGP, Kotlin, Compose BOM, AndroidX) i ProGuard keep rules

### Phase 2: Critical Calculation Fixes
**Goal**: Tačni proračuni za sve materijale sa type-safe Material enum
**Depends on**: Phase 1
**Requirements**: CALC-01, CALC-02, CALC-03, SEC-02
**Success Criteria** (what must be TRUE):
  1. CuZn15, CuZn20, CuZn30 materijali koriste ispravne gustine (8750, 8670, 8530 kg/m³)
  2. Outer diameter proračun je precizan za trake dužine 10,000+ metara bez precision loss
  3. Material je Enum type (ne String), eliminiše string manipulation bugs
  4. Proračuni za sve kombinacije materijala/težina/dimenzija vraćaju tačne rezultate
**Plans**: 1 plan

Plans:
- [x] 02-01-PLAN.md -- Material enum sa ispravnim gustinama, Calculator konverzija na enum, UI ažuriranje, uklanjanje String-based koda

### Phase 3: Architecture & Validation
**Goal**: Čist state management i real-time input validacija
**Depends on**: Phase 2
**Requirements**: ARCH-01, ARCH-02, ARCH-03, VALID-01, VALID-02, VALID-03
**Success Criteria** (what must be TRUE):
  1. Sav input state je izdvojen u data class (odvoji state od UI)
  2. doCalculate() je pure function koja prima state i vraća result (testable)
  3. CALCULATE dugme je disabled dok sva input polja nisu validna
  4. Invalidna polja pokazuju crveni border sa error message ispod
  5. Message state se pravilno clear-uje na novu kalkulaciju
  6. Nema magic numbers - sve layout vrednosti su centralizovane u AppConstants
**Plans**: 2 plans

Plans:
- [x] 03-01-PLAN.md -- Data layer fondacija: InputState/FieldValidation modeli, Validation.kt pure functions, doCalculate pure function, LabeledTextField error support, PrimaryButton enabled support, layout tokens
- [x] 03-02-PLAN.md -- CalculatorScreen refaktorisanje: InputState integracija, blur-based validacija, disabled button, magic number zamena, ProGuard rules

### Phase 4: Performance & Features
**Goal**: Brza UI sa reset i share funkcijama za serijske proračune
**Depends on**: Phase 3
**Requirements**: PERF-01, PERF-02, PERF-03, PERF-04, FEAT-01, FEAT-02
**Success Criteria** (what must be TRUE):
  1. Font sizing koristi binary search (O(log n)), ne while loop
  2. Rezultati se renderuju u LazyColumn i skaliraju do 100+ rolls bez lag-a
  3. RowData objekti su memoizovani za smanjene recompositions
  4. Scroll-to-results koristi LaunchedEffect bez hard-coded delay-a
  5. Korisnik može da klikne Reset dugme i clear-uje sva polja jednim klikom
  6. Korisnik može da share-uje rezultate preko email/messaging Intent-a
**Plans**: 2 plans

Plans:
- [x] 04-01-PLAN.md -- Performance optimizacije: binary search font sizing, LazyColumn rezultati, @Immutable annotations, scroll timing fix
- [x] 04-02-PLAN.md -- Nove funkcije: Reset button, Share/Podeli rezultate via Intent

### Phase 5: Test Coverage
**Goal**: Potpuna test pokrivenost Calculator logike i UI flow-a
**Depends on**: Phase 4
**Requirements**: TEST-01, TEST-02, TEST-03, TEST-04
**Success Criteria** (what must be TRUE):
  1. Unit tests pokrivaju sve Calculator funkcije (parseDouble, parseInt, computeAllResults, calculateOuterDiameter, getDensity, getRollWord)
  2. Integration tests pokrivaju kompletan UI flow (field navigation, validation, calculation, rendering)
  3. Material density tests verifikuju svih 6 materijala sa ispravnim gustinama
  4. Layout responsive tests pokrivaju compact vs regular screen i keyboard visibility
  5. Test suite se može pokrenuti i svi testovi prolaze
**Plans**: 2 plans

Plans:
- [ ] 05-01-PLAN.md -- Test dependencies (JUnit 4, Truth) + unit testovi za Calculator, Validation, Models (local JVM tests)
- [ ] 05-02-PLAN.md -- Compose test dependencies + integration testovi za CalculatorScreen UI flow (androidTest)

## Progress

**Execution Order:**
Phases execute in numeric order: 1 → 2 → 3 → 4 → 5

| Phase | Plans Complete | Status | Completed |
|-------|----------------|--------|-----------|
| 1. Foundation & Security | 1/1 | Complete | 2026-02-09 |
| 2. Critical Calculation Fixes | 1/1 | Complete | 2026-02-09 |
| 3. Architecture & Validation | 2/2 | Complete | 2026-02-09 |
| 4. Performance & Features | 2/2 | Complete | 2026-02-09 |
| 5. Test Coverage | 0/1 | Not started | - |
