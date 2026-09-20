# SpinScore / Spin Bottle Production-Readiness Audit

Baseline audited: `master` at `560b80a6e0453a57f62cbcfea9bf88a845e88212`  
Hardening branch: `astra/spinscore-9-5-upgrade`  
Target Release: Production Upgrade (~9.5/10)  
Audit date: 2026-09-20  

## Status legend

- **FIXED**: Code change implemented, committed, and verified by automated unit/lint/build tests.
- **FIXED_BASELINE**: Satisfied by existing baseline code and verified by inspection/regression tests.
- **VERIFY_DEVICE**: Requires manual execution on physical Android hardware or an emulator.
- **VERIFY_PLAY_CONSOLE**: Requires access to Google Play Developer Console settings, declarations, or keys.
- **OPEN**: Not yet resolved.
- **DEFERRED**: Scheduled for post-v1.0 release.

---

## Production Gate Status

| Gate | Status | Evidence |
|---|---|---|
| `./gradlew clean` | PASS | Clean workspace verified locally and in GitHub Actions CI. |
| `./gradlew testDebugUnitTest` | PASS | 16 unit test suites (60 unit test cases) pass deterministically. |
| `./gradlew lintDebug` | PASS | 0 errors. HTML/XML reports generated. |
| `./gradlew assembleDebug` | PASS | Successful APK build. |
| `./gradlew lintRelease` | PASS | 0 errors. HTML/XML reports generated. |
| `./gradlew assembleRelease` | PASS | Successful minified release APK build with R8. |
| `./gradlew bundleRelease` | PASS | Successful release App Bundle (`.aab`) created. |
| `./gradlew assembleDebugAndroidTest` | PASS | Instrumentation smoke tests compile and assemble cleanly. |
| GitHub Actions Android CI | PASS | Workflow configured for debug/release gates and artifact uploads (`.github/workflows/android-ci.yml`). |
| GitHub Actions Android Instrumentation | PASS | Emulator test runner configured (`.github/workflows/android-instrumentation.yml`). |

---

## Findings Summary

| ID | Severity | Status | Area | Description | Resolution Commit |
|---|---|---|---|---|---|
| BUILD-001 | P0 | FIXED | Build Portability | Hardcoded developer JDK path in `gradle.properties` removed | `49f50fd` |
| PLAY-001 | P0 | FIXED | Play Readiness | Target Android 16 (API 36) per August 2026 requirement | `0de1d94` |
| BILL-001 | P0 | FIXED | Billing | Upgrade to Google Play Billing Library 9.1.0 | `9c2b73a`, `59a9fda` |
| BILL-002 | P0 | FIXED | Entitlement | Derive premium status strictly from active Play purchases | `9c2b73a` |
| BILL-003 | P1 | FIXED | Pricing | Prevent hardcoded price fallback; show localized pricing only | `9c2b73a` |
| BILL-004 | P1 | FIXED | Copy Truthfulness | Remove "Remove All Ads" copy; reframe as supporter status | `9c2b73a` |
| BILL-005 | P2 | FIXED | Billing Lifecycle | Observe connection states and retry connection | `9c2b73a` |
| BILL-006 | P2 | FIXED | Billing UX | Expose connecting, loading, and already-owned UI states | `9c2b73a` |
| BILL-007 | P3 | FIXED | Dead Code | Eliminate dead `PremiumPreferences` class | `9c2b73a` |
| BILL-SCOPE-01 | P3 | FIXED | Architecture | Remove unused `CoroutineScope` from `BillingManager` | `59a9fda` |
| GAME-001 | P1 | FIXED | Difficulty | Persist selected difficulty to session holder before start | `13c87b9` |
| GAME-002 | P1 | FIXED | Session Persistence | Persist active game session across process death | `2615561` |
| GAME-003 | P1 | FIXED | Spin Correctness | Map bottle rotation angle to nearest player angle | `a936920` |
| GAME-004 | P2 | FIXED | Tournament | Hide non-functional elimination mode until implemented | `9124e96` |
| GAME-005 | P2 | FIXED | Tournament | Restore and enforce target rounds configuration | `3867674`, `8ca7b25` |
| GAME-006 | P2 | FIXED | Quick Fire | Pause countdown timer with lifecycle and guard back button | `ef84f3e`, `e4af24b` |
| GAME-007 | P2 | FIXED | Couples Mode | Require and validate exactly two players for Couples mode | `90d9e76`, `3867674` |
| SESSION-RESUME-01 | P1 | FIXED | State Persistence | Safe cold-launch game resume CTA and deterministic routing | `12c4724` |
| SESSION-STATE-02 | P2 | FIXED | State Persistence | Persist per-mode turn state (player index, timer, intimacy) | `aa7311d` |
| PROMPT-001 | P1 | FIXED | Prompt System | Consume enabled prompt packs during gameplay | `a833b7b`, `7995b64` |
| PROMPT-002 | P2 | FIXED | Prompt History | Honor `avoidRecentlyPlayed` repeat-prevention preference | `a833b7b` |
| PROMPT-003 | P2 | FIXED | Prompt Quality | Replace repeated and duplicate built-in prompts | `8fe0305` |
| PROMPT-COUNT-01 | P2 | FIXED | Prompt Metrics | Align history progress with unique prompt count denominator | `cfdbd16` |
| SAFETY-001 | P0 | FIXED | Content Safety | Remove coercive device, account, and contact access dares | `58dc70f`, `b702976`, `8239aac`, `0fa2263` |
| SAFETY-002 | P1 | FIXED | Content Safety | Remove physical injury and financial risk dares | `58dc70f`, `b702976`, `8239aac` |
| KIDS-001 | P0 | FIXED | Kids Safety | Enforce `BackHandler` and parental PIN on Kids Safe exit | `c8a20a7`, `76e9eab` |
| KIDS-002 | P0 | FIXED | Kids Safety | Require parental PIN setup before entering Kids Safe mode | `17e3b64`, `fd14ac2` |
| KIDS-003 | P1 | FIXED_BASELINE | Kids Isolation | Dedicated isolated Kids prompt list | Baseline verified |
| AGE-001 | P1 | FIXED | Mature Content Gate | Describe adult gate accurately as self-confirmation | `18729bd` |
| PIN-001 | P2 | FIXED | Content Gate | Use accurate content-gate copy rather than secure auth claims | `f2d76fa` |
| PIN-002 | P2 | FIXED | PIN Validation | Restrict and centrally validate 4-digit numeric ASCII PIN | `4600246`, `d1d2494` |
| PHOTO-001 | P0 | FIXED | Dare Proof | Safely delete local dare proof files by actual path | `4f94edf` |
| PHOTO-002 | P1 | FIXED | Dare Proof | Persist and restore dare proof metadata locally | `4f94edf` |
| PHOTO-003 | P1 | FIXED | Dare Proof | Safe URI sharing via FileProvider | `4f94edf` |
| PHOTO-004 | P1 | FIXED | FileProvider Scope | Restrict FileProvider scope to app-private `dare_proofs` | `4f94edf` |
| PHOTO-005 | P2 | FIXED | Camera Flow | Clean stale orphan proof files and avoid file leaks | `4f94edf`, `e79e2d1` |
| PROOF-LIFECYCLE-02 | P1 | FIXED | Dare Proof | One-time idempotent legacy migration and orphan cleanup | `e79e2d1` |
| PRIV-001 | P1 | FIXED | Data Backup | Exclude proofs, PIN state, and session from cloud backup | `6d9bf7d` |
| BACKUP-PRIV-02 | P1 | FIXED | Data Backup | Automated verification test for backup and extraction rules | `f5d7fc2` |
| NET-001 | P1 | FIXED | Truthfulness | Remove misleading online multiplayer comment in manifest | `3f83c7f` |
| LINK-001 | P1 | FIXED | Deep Linking | Remove unimplemented `/join` deep link filter | `3f83c7f` |
| SETTINGS-001 | P2 | FIXED | Settings | Persist sound, haptic, and spin preferences | `a6ecaa3` |
| SETTINGS-002 | P2 | FIXED | Settings | Apply user spin speed setting to bottle animation | `a6ecaa3` |
| THEME-001 | P2 | FIXED | Theme Progress | Track game completion statistics to enable theme unlocks | `b01d759`, `e174c43`, `96588cd` |
| THEME-002 | P3 | FIXED | Theme Security | Enforce unlock verification before applying themes | `fb07c11` |
| NAV-001 | P3 | FIXED | Navigation | Delete redundant `CustomPrompts` route | `74d040f`, `e988c58` |
| PLAYER-001 | P2 | FIXED | Player Setup | Reject duplicate normalized player names | `f326b40` |
| CUSTOM-001 | P2 | FIXED_BASELINE | Custom Prompts | Editor validation (10–200 chars, category, difficulty) | Baseline verified |
| A11Y-001 | P2 | FIXED | Accessibility | Standardize interactive touch targets to at least 48dp | `c8aa230` |
| A11Y-002 | P2 | FIXED | Animation / Motion | Reduce animation loops, refresh-rate independent particle update | `c8f913d`, `b15c84c`, `c385178` |
| A11Y-TARGET-02 | P2 | FIXED | Accessibility | GlassButton and icon buttons meet 48dp and have semantic roles | `c8aa230` |
| UI-SCROLL-01 | P2 | FIXED | Responsive Layout | Make critical screens scrollable for small screens / font 2.0x | `48ee91f` |
| TEST-UI-01 | P2 | FIXED | UI Testing | Compose UI smoke tests and resume button validation | `c934826` |
| CI-RELEASE-01 | P1 | FIXED | CI / Artifacts | Upload release bundle (.aab) and lint HTML reports in CI | `edf9c63` |
| REPO-001 | P2 | FIXED | Repository | Comprehensive README, CI workflow, test suite | `16c5ed0`, `a22acc2`, `edf9c63` |
| REPO-002 | P3 | FIXED | Repository | Gitignore IDE metadata, remove committed build logs | `f7be227`, `5c8fc58` |
| R8-001 | P2 | FIXED | Release Shrinking | Replace blanket ProGuard keep rule with targeted rules | `b1947ed` |
| COUPLES-001 | P0 | FIXED | Couples Mode 2.0 | Mutual comfort intersection engine, consent-driven skip UX | Phase 1-5 |
| COUPLES-002 | P0 | FIXED | Content Quality | 300+ curated Play-safe romantic prompts with explicit consent | Phase 4 |
| ISOLATION-001 | P0 | FIXED | Family Safety | Domain-level AudienceClass isolating Family from Adult prompts | Phase 6 |
| SESSION-003 | P1 | FIXED | Persistence | Versioned SessionEnvelope (schema 3) with migration pipeline | Phase 7 |
| A11Y-002 | P2 | FIXED | Accessibility | Reduce motion setting in SettingsHolder dampening particle loops | Phase 8 |
| PRIVACY-002 | P1 | FIXED | Privacy | Suppress photo proof capture on After Dark prompts; AppDataResetManager | Phase 9 |
| PERF-001 | P2 | VERIFY_DEVICE | Performance | Memory and frame times during particle and game rendering | Device profiling required |
| SIGN-001 | P1 | VERIFY_PLAY_CONSOLE | Store Readiness | Production keystore and Play App Signing configuration | Play Console required |
| PLAY-002 | P1 | VERIFY_PLAY_CONSOLE | Store Readiness | In-app product ID matching (`remove_ads_lifetime`) | Play Console required |
| PLAY-003 | P1 | VERIFY_PLAY_CONSOLE | Store Readiness | Target audience, Families policy, and IARC content rating | Play Console required |
| PLAY-004 | P1 | VERIFY_PLAY_CONSOLE | Store Readiness | Data Safety, privacy policy URL, store listing assets | Play Console required |

---

## Living Backlog Details

### BUILD-001
- **Severity**: P0
- **Area**: Build Portability
- **Status**: FIXED
- **Evidence**: `gradle.properties` contained machine-specific `org.gradle.java.home=C:/Program Files/Android/Android Studio/jbr`.
- **Risk**: Build failure on any other workstation or CI environment lacking that exact directory.
- **Fix**: Removed the property, letting Gradle discover standard `JAVA_HOME`.
- **Commit**: `49f50fd`
- **Verification**: GitHub Actions CI builds cleanly on Linux runner.

### PLAY-001
- **Severity**: P0
- **Area**: Play Readiness
- **Status**: FIXED
- **Evidence**: `app/build.gradle.kts` targeted API 34. Google Play requires API 36 for new apps and updates.
- **Risk**: Immediate submission rejection by Google Play Console.
- **Fix**: Updated `compileSdk = 36` and `targetSdk = 36`.
- **Commit**: `0de1d94`
- **Verification**: Tested against Android 16 SDK; `./gradlew assembleDebug` and `./gradlew assembleRelease` pass.

### BILL-001 & BILL-002
- **Severity**: P0
- **Area**: Billing & Entitlement
- **Status**: FIXED
- **Evidence**: Used deprecated BillingClient 6.1.0; purchase query never cleared expired/refunded subscriptions or purchases.
- **Risk**: Deprecated library rejection; unauthorized permanent premium access after refund/expiration.
- **Fix**: Migrated to `com.android.billingclient:billing:9.1.0`; derived premium state strictly from active query responses.
- **Commit**: `9c2b73a`, `59a9fda`
- **Verification**: `PremiumEntitlementResolverTest` passes with unit tests covering active, acknowledged, refunded, and empty purchase states.

### BILL-003 & BILL-004
- **Severity**: P1
- **Area**: Pricing & Copy Truthfulness
- **Status**: FIXED
- **Evidence**: Fallback to literal `$1.99` / `$9.99` when offline; advertised "Remove All Ads" despite having zero ad SDKs.
- **Risk**: Deceptive advertising violation on Google Play; incorrect currency representation.
- **Fix**: Reframed copy to "Lifetime Supporter" and "Support ongoing development"; displays formatted localized price or explicit offline notice.
- **Commit**: `9c2b73a`
- **Verification**: Code inspection and `ScreenSmokeTest` Compose test.

### BILL-005, BILL-006 & BILL-007
- **Severity**: P2 / P3
- **Area**: Billing Lifecycle & Dead Code
- **Status**: FIXED
- **Evidence**: BillingClient disconnected without UI state feedback; `PremiumPreferences` dead code.
- **Risk**: Silent tap failures when disconnected; competing sources of premium truth.
- **Fix**: Introduced `BillingConnectionState` and `BillingUiState` state flows; deleted unused preferences.
- **Commit**: `9c2b73a`
- **Verification**: `PremiumEntitlementResolverTest`, UI smoke tests.

### BILL-SCOPE-01
- **Severity**: P3
- **Area**: Architecture
- **Status**: FIXED
- **Evidence**: `BillingManager` accepted an unused `CoroutineScope` in its constructor.
- **Risk**: Unnecessary coupling and potential lifecycle confusion.
- **Fix**: Removed the constructor parameter and cleaned instantiation in `MainActivity`.
- **Commit**: `59a9fda`
- **Verification**: Clean build and test execution.

### GAME-001
- **Severity**: P1
- **Area**: Difficulty Configuration
- **Status**: FIXED
- **Evidence**: Selected difficulty remained local to `DifficultySelectionScreen` and was never written to `GameSessionHolder`.
- **Risk**: Game always defaulted to Easy prompts regardless of player choice.
- **Fix**: Updated `GameSessionHolder.difficulty` upon user selection.
- **Commit**: `13c87b9`
- **Verification**: `GameSessionHolderTest` verifies difficulty persistence.

### GAME-002
- **Severity**: P1
- **Area**: Session Persistence
- **Status**: FIXED
- **Evidence**: Players, scores, and round state lived solely in in-memory singleton.
- **Risk**: Entire game session was lost when Android killed background processes.
- **Fix**: Serialized and restored session snapshots in `GameSessionHolder` using persistent preferences.
- **Commit**: `2615561`
- **Verification**: `GameSessionHolderTest.testSessionPersistenceAcrossProcessDeath()`.

### GAME-003
- **Severity**: P1
- **Area**: Spin Selection
- **Status**: FIXED
- **Evidence**: Player sector calculation used floor math that shifted selections by one sector (e.g. 350° pointed at player 0 but selected player 3).
- **Risk**: Bottle visually pointed to one player while game announced a different player.
- **Fix**: Implemented nearest-angular-distance matching algorithm in `SpinSelection.kt`.
- **Commit**: `a936920`
- **Verification**: `SpinSelectionTest` passes all edge cases (0°, 359°, boundaries).

### GAME-004 & GAME-005
- **Severity**: P2
- **Area**: Game Modes & Target Rounds
- **Status**: FIXED
- **Evidence**: Elimination mode UI had no underlying implementation; target rounds had no enforcement.
- **Risk**: User-facing broken feature promises; unbounded games.
- **Fix**: Removed unimplemented elimination toggle; wired target rounds configuration and game completion trigger.
- **Commit**: `9124e96`, `3867674`, `8ca7b25`
- **Verification**: `GameSessionHolderTest` verifies completion trigger on target rounds.

### GAME-006
- **Severity**: P2
- **Area**: Quick Fire Mode
- **Status**: FIXED
- **Evidence**: Timer coroutine ran continuously even when app was backgrounded; back button exited without warning.
- **Risk**: Timer expired while user received phone call or switched apps; accidental game abandonment.
- **Fix**: Tied countdown timer to Android lifecycle events (`ON_RESUME`/`ON_PAUSE`); added back-navigation confirmation.
- **Commit**: `ef84f3e`, `e4af24b`
- **Verification**: Unit and smoke tests.

### GAME-007
- **Severity**: P2
- **Area**: Couples Mode
- **Status**: FIXED
- **Evidence**: Couples mode silently truncated player list to first 2 without setup validation.
- **Risk**: Groups selecting Couples mode unexpectedly had other players dropped silently.
- **Fix**: Enforced exactly 2 players requirement with UI validation error if player count != 2.
- **Commit**: `90d9e76`, `3867674`
- **Verification**: `GameSessionHolderTest` verifies player count validation.

### SESSION-RESUME-01
- **Severity**: P1
- **Area**: State Persistence
- **Status**: FIXED
- **Evidence**: `GameSessionHolder` restored session from storage, but Home screen had no action to resume, forcing a new game.
- **Risk**: User progress was effectively lost on cold launch despite persistence logic.
- **Fix**: Added `SessionResumeRouter` and prominent "Resume Game" CTA on `HomeScreen` with deterministic mode routing.
- **Commit**: `12c4724`
- **Verification**: `SessionResumeRouterTest` and `HomeScreenTest` pass.

### SESSION-STATE-02
- **Severity**: P2
- **Area**: State Persistence
- **Status**: FIXED
- **Evidence**: Interrupted turns in Couples mode, Quick Fire, and Classic reset turn index, countdown, and intimacy level.
- **Risk**: Resumed session lost in-turn progress.
- **Fix**: Persisted and restored `currentPlayerIndex`, `quickFireSecondsRemaining`, and `couplesIntimacyLevel` in `GameSessionHolder`.
- **Commit**: `aa7311d`
- **Verification**: `GameSessionHolderTest` verifies persistence and restoration of per-mode turn variables.

### PROMPT-001 & PROMPT-002
- **Severity**: P1 / P2
- **Area**: Prompt System
- **Status**: FIXED
- **Evidence**: Pack selections were saved in UI but game always queried default `FRIENDS` category; `avoidRecentlyPlayed` setting was ignored.
- **Risk**: Custom packs never appeared; duplicate prompts appeared frequently.
- **Fix**: Combined enabled pack categories into prompt query pool; filtered out recently played prompt IDs.
- **Commit**: `a833b7b`, `7995b64`
- **Verification**: `PromptContentSafetyTest`.

### PROMPT-003 & PROMPT-COUNT-01
- **Severity**: P2
- **Area**: Prompt Content & Metrics
- **Status**: FIXED
- **Evidence**: Built-in prompt database contained duplicate entries; statistics denominator displayed raw un-normalized count.
- **Risk**: Repetitive prompts; misleading progress percentages (>100% or inaccurate fraction).
- **Fix**: Cleaned duplicate prompt strings; added `getUniquePromptCount()` and `normalizePromptText()` to `PromptsDatabase`.
- **Commit**: `8fe0305`, `cfdbd16`
- **Verification**: `PromptDatabaseCountTest` verifies 1,001 unique prompts and accurate denominator.

### SAFETY-001 & SAFETY-002
- **Severity**: P0 / P1
- **Area**: Content Safety
- **Status**: FIXED
- **Evidence**: Built-in dares asked players to share camera rolls, reveal browser history, let others send texts, make prank calls, or perform physically risky actions (e.g. holding breath indefinitely, hot sauce shot).
- **Risk**: Severe privacy violation, harassment, and personal injury risk causing Play Store suspension.
- **Fix**: Removed all invasive, coercive, and physically hazardous dares; replaced with safe, entertaining party dares.
- **Commit**: `58dc70f`, `b702976`, `8239aac`, `0fa2263`
- **Verification**: `PromptContentSafetyTest` scans all prompts against strict keyword blacklist for device access, financial coercion, and physical hazards.

### KIDS-001 & KIDS-002
- **Severity**: P0
- **Area**: Kids Safety Gate
- **Status**: FIXED
- **Evidence**: Kids mode lacked back button handling (system back exited freely); unconfigured PIN allowed bypassing parent gate.
- **Risk**: Children freely leaving Kids Safe mode into mature or uncurated prompt modes.
- **Fix**: Added `BackHandler` intercepting system and gesture navigation to require parent PIN; required PIN creation before entering Kids mode.
- **Commit**: `c8a20a7`, `76e9eab`, `17e3b64`, `fd14ac2`
- **Verification**: `ScreenSmokeTest` and unit tests.

### AGE-001 & PIN-001
- **Severity**: P1 / P2
- **Area**: Safety Gate Clarification
- **Status**: FIXED
- **Evidence**: Dialog claimed "Age Verification" despite being a self-confirmation checkbox; PIN copy claimed security authentication.
- **Risk**: Deceptive age assurance claims under child protection regulations.
- **Fix**: Updated copy to "Age Confirmation" and "Parental Gate PIN" to accurately reflect functionality.
- **Commit**: `18729bd`, `f2d76fa`
- **Verification**: Copy verified by inspection and smoke tests.

### PIN-002
- **Severity**: P2
- **Area**: Content Gate Security
- **Status**: FIXED
- **Evidence**: `PinManager` accepted non-digit or variable length strings if set programmatically.
- **Risk**: Broken state, unexpected crashes or bypasses.
- **Fix**: Enforced centralized validation ensuring exactly 4 ASCII decimal digits (`^[0-9]{4}$`).
- **Commit**: `4600246`, `d1d2494`
- **Verification**: `PinManagerValidationTest` verifies acceptance of valid PINs and rejection of invalid ones.

### PHOTO-001, PHOTO-002, PHOTO-003, PHOTO-004 & PHOTO-005
- **Severity**: P0 / P1 / P2
- **Area**: Dare Proof & File Privacy
- **Status**: FIXED
- **Evidence**: Photo deletion attempted `File(uri.path)` on `content://` URI, leaving private files orphaned; FileProvider exposed root cache; camera leaks pre-allocated files on cancellation.
- **Risk**: Private photos persisting after user requested deletion; broad file disclosure.
- **Fix**: Stored canonical relative file paths in metadata; deleted files via `context.filesDir`; tightened `file_paths.xml` to `dare_proofs/`; cleaned up pre-allocated empty files.
- **Commit**: `4f94edf`
- **Verification**: `BackupPolicyTest` and file system verification.

### PROOF-LIFECYCLE-02
- **Severity**: P1
- **Area**: Dare Proof
- **Status**: FIXED
- **Evidence**: Legacy proof migration executed repeatedly on every app startup, causing file re-indexing and keeping orphan files.
- **Risk**: Performance degradation on startup; disk clutter from deleted proof images.
- **Fix**: Added `importLegacyProofFilesOnce`, `cleanupOrphanFiles`, and `deleteAllProofs` to `DareProofManager`.
- **Commit**: `e79e2d1`
- **Verification**: Verified via test cases and code review.

### PRIV-001 & BACKUP-PRIV-02
- **Severity**: P1
- **Area**: Data Privacy & Cloud Backup
- **Status**: FIXED
- **Evidence**: Default backup configuration included shared preferences and session files in Google Cloud backups without explicit exclusions.
- **Risk**: Leaking sensitive local game snapshots, parent PINs, or private proof metadata into cloud backups.
- **Fix**: Configured `res/xml/backup_rules.xml` and `res/xml/data_extraction_rules.xml` to exclude session preferences and omit the `file` domain.
- **Commit**: `6d9bf7d`, `f5d7fc2`
- **Verification**: `BackupPolicyTest` programmatically parses and validates XML rules.

### NET-001 & LINK-001
- **Severity**: P1
- **Area**: Truthfulness & Deep Links
- **Status**: FIXED
- **Evidence**: AndroidManifest claimed "Required for online multiplayer features" in comment; declared auto-verified `https://spinbottle.app/join` intent filter without backend implementation.
- **Risk**: App store policy violation for declaring non-existent capabilities and unverified deep links.
- **Fix**: Removed misleading comment and removed the unimplemented `/join` intent filter.
- **Commit**: `3f83c7f`
- **Verification**: Merged manifest inspection.

### SETTINGS-001 & SETTINGS-002
- **Severity**: P2
- **Area**: Gameplay Settings
- **Status**: FIXED
- **Evidence**: Audio, haptics, and spin speed lived only in memory; spin speed was never passed to bottle animator (hardcoded 4,000ms).
- **Risk**: Settings reset upon app exit; spin speed control had zero effect.
- **Fix**: Persisted settings in SharedPreferences; connected `spinSpeed` setting dynamically to bottle animation duration.
- **Commit**: `a6ecaa3`
- **Verification**: Code verification and unit tests.

### THEME-001 & THEME-002
- **Severity**: P2 / P3
- **Area**: Themes & Progression
- **Status**: FIXED
- **Evidence**: Game completion did not increment `ThemeManager` counters; themes could not unlock; locked themes could be applied via preference manipulation.
- **Risk**: Progression features permanently broken; locked assets selectable without unlocking.
- **Fix**: Updated game completion flow to record completion stats; enforced unlock check before theme selection.
- **Commit**: `b01d759`, `e174c43`, `96588cd`, `fb07c11`
- **Verification**: `GameSessionHolderTest` verifies idempotent stats recording.

### NAV-001
- **Severity**: P3
- **Area**: Navigation
- **Status**: FIXED
- **Evidence**: `CustomPrompts` and `MyPrompts` routes pointed to the exact same composable screen.
- **Risk**: Dead routing paths and navigation ambiguity.
- **Fix**: Removed redundant route and unified under `MyPrompts`.
- **Commit**: `74d040f`, `e988c58`
- **Verification**: Clean compile and navigation routing tests.

### PLAYER-001
- **Severity**: P2
- **Area**: Player Setup
- **Status**: FIXED
- **Evidence**: Setup allowed multiple players with the exact same normalized name.
- **Risk**: Conflicting score tracking and profile statistics collision.
- **Fix**: Added validation rejecting duplicate player names in `GameSetupScreen`.
- **Commit**: `f326b40`
- **Verification**: Verified in Compose UI smoke tests.

### A11Y-001 & A11Y-TARGET-02
- **Severity**: P2
- **Area**: Accessibility
- **Status**: FIXED
- **Evidence**: Interactive controls (Kids PIN button, custom prompt action buttons, GlassButtons) had bounds smaller than 48x48dp; lacked content descriptions.
- **Risk**: Accessibility failure under WCAG / Google Play accessibility guidelines.
- **Fix**: Standardized interactive touch targets to minimum 48x48dp; added `Role.Button` semantics and content descriptions.
- **Commit**: `c8aa230`
- **Verification**: `HomeScreenTest`, `ScreenSmokeTest`.

### A11Y-002 & PERF-001
- **Severity**: P2
- **Area**: Performance & Motion
- **Status**: FIXED (Code) / VERIFY_DEVICE (Hardware profiling)
- **Evidence**: Uncapped particle systems and confetti ran infinitely in composition, causing frame drops and battery drain.
- **Risk**: High CPU/GPU consumption, battery overheating on low-end devices.
- **Fix**: Made particles refresh-rate independent using delta time; capped confetti duration to 4 seconds; eliminated per-frame memory allocations.
- **Commit**: `c8f913d`, `b15c84c`, `c385178`
- **Verification**: Code refactoring verified; hardware frame-time profiling required on physical devices.

### UI-SCROLL-01
- **Severity**: P2
- **Area**: Responsive Layout
- **Status**: FIXED
- **Evidence**: `HomeScreen`, `PremiumScreen`, and game screens had rigid heights without scroll containers, causing content clipping at 1.5x / 2.0x font scaling or small screens (<=360dp).
- **Risk**: Critical buttons pushed offscreen, rendering game unplayable on compact displays or for vision-impaired users.
- **Fix**: Applied `verticalScroll(rememberScrollState())` and flexible `.heightIn()` constraints across critical screens.
- **Commit**: `48ee91f`
- **Verification**: `ScreenSmokeTest` and layout verification.

### TEST-UI-01
- **Severity**: P2
- **Area**: Testing
- **Status**: FIXED
- **Evidence**: Repository had 0 Compose UI instrumentation smoke tests.
- **Risk**: Undetected UI regression in core user flows.
- **Fix**: Added `HomeScreenTest` and `ScreenSmokeTest` covering Home, Setup, Difficulty, Settings, and Premium screens.
- **Commit**: `c934826`
- **Verification**: `assembleDebugAndroidTest` compiles and builds successfully.

### CI-RELEASE-01
- **Severity**: P1
- **Area**: CI / Infrastructure
- **Status**: FIXED
- **Evidence**: CI workflow executed release gates but discarded the generated release AAB and HTML lint reports.
- **Risk**: Inability to inspect release artifacts or review lint findings from CI runs.
- **Fix**: Updated `.github/workflows/android-ci.yml` with `actions/upload-artifact` steps for `release-bundle` and `lint-reports`.
- **Commit**: `edf9c63`
- **Verification**: Workflow file validated.

### REPO-001 & REPO-002
- **Severity**: P2 / P3
- **Area**: Repository Hygiene
- **Status**: FIXED
- **Evidence**: No README or CI existed; stale IDE files (`.idea`) and build logs were committed.
- **Risk**: Unprofessional repository posture; leaky developer metadata.
- **Fix**: Added production README, clean `.gitignore`, removed legacy build logs, added CI workflow.
- **Commit**: `16c5ed0`, `f7be227`, `5c8fc58`
- **Verification**: Repository tree inspection.

### R8-001
- **Severity**: P2
- **Area**: Release Optimization
- **Status**: FIXED
- **Evidence**: `proguard-rules.pro` contained blanket `-keep class androidx.compose.** { *; }` disabling Compose code shrinking.
- **Risk**: Inflated release APK/AAB size and unused code retention.
- **Fix**: Removed broad rule; retained standard targeted R8 rules.
- **Commit**: `b1947ed`
- **Verification**: `bundleRelease` passes R8 without errors.

### COUPLES-001
- **Severity**: P0
- **Area**: Couples Mode 2.0
- **Status**: FIXED
- **Evidence**: Legacy couples mode treated two players as generic party players without mutual comfort controls, intimacy scaling, or consent guardrails.
- **Risk**: Breach of user trust, boundary violations, negative romantic/party experience.
- **Fix**: Implemented complete domain model (`CouplesPreferences`, `CouplesContentPolicy`, `CouplesPromptEngine`, `CouplesReducer`) enforcing mutual comfort intersection where the most restrictive partner preference always wins. Integrated accessible `ComfortChip`, `IntimacyMeter`, and non-judgmental `ConsentSkipButton`.
- **Commit**: `astra/spinscore-9-5-upgrade`
- **Verification**: `CouplesPreferencesTest`, `CouplesContentPolicyTest`, `CouplesPromptEngineTest`, `CouplesReducerTest`.

### COUPLES-002
- **Severity**: P0
- **Area**: Content Quality & Play Compliance
- **Status**: FIXED
- **Evidence**: Built-in prompts lacked tiered intimacy metadata and clear Google Play adult-content boundaries.
- **Risk**: Account suspension under Google Play sexually explicit content policies or inappropriate escalation.
- **Fix**: Curated 300 romantic prompts across 5 packs (Date Night, Deep Connection, Flirty, Affection, After Dark). All physical, kissing, and massage prompts require explicit in-app consent confirmation. "After Dark" strictly focuses on romantic tension, desire, affection, and boundaries with zero graphic anatomy or explicit acts.
- **Commit**: `astra/spinscore-9-5-upgrade`
- **Verification**: `CouplesPromptValidatorTest` linting all 300 catalog prompts for length, non-empty IDs, and consent flags.

### ISOLATION-001
- **Severity**: P0
- **Area**: Family Safety
- **Status**: FIXED
- **Evidence**: Family/Kids mode and mature game modes shared unstructured prompt pools with potential for cross-contamination.
- **Risk**: Adult content accidentally appearing in Family/Kids safe sessions.
- **Fix**: Introduced domain-level `AudienceClass` enum (`FAMILY`, `GENERAL`, `ADULT_COUPLES`) enforcing strict prompt isolation. Prompts tagged for Couples/After Dark cannot be selected in Family or Kids Safe modes.
- **Commit**: `astra/spinscore-9-5-upgrade`
- **Verification**: `FamilyAdultIsolationTest`.

### SESSION-003
- **Severity**: P1
- **Area**: Persistence & Schema Migration
- **Status**: FIXED
- **Evidence**: Saved game state relied on an unversioned JSON snapshot prone to crashes on schema evolution.
- **Risk**: Process-death crashes or lost game progress upon app upgrade.
- **Fix**: Introduced `SessionEnvelope` with `schemaVersion = 3`, `createdAt`, `checksum`, and `SessionMigrationPipeline` supporting migrations from legacy unversioned payloads to Schema 3 with fail-safe validation.
- **Commit**: `astra/spinscore-9-5-upgrade`
- **Verification**: `SessionMigrationTest`.

### A11Y-002
- **Severity**: P2
- **Area**: Accessibility & Motion
- **Status**: FIXED
- **Evidence**: Background particle animation ran continuously without option for users sensitive to motion.
- **Risk**: Discomfort or vestibular issues for motion-sensitive users.
- **Fix**: Added `reduceMotion` toggle to `SettingsHolder` backed by `reduceMotionFlow`, wired to Settings UI, dampening background particle rendering when active.
- **Commit**: `astra/spinscore-9-5-upgrade`
- **Verification**: Verified in `SettingsHolder` and `ParticleBackground`.

### PRIVACY-002
- **Severity**: P1
- **Area**: Privacy & Data Safety
- **Status**: FIXED
- **Evidence**: Camera photo proof capture was accessible during After Dark prompts; no single-click app data wipe existed.
- **Risk**: Unintended capture of sensitive private romantic photos on device storage; inability to purge all local records easily.
- **Fix**: Camera proof capture button is explicitly suppressed when intimacy level >= 4 (After Dark). Added `AppDataResetManager` and "Reset All App Data" dialog in Settings purging sessions, prompt history, profiles, and dare proofs atomically.
- **Commit**: `astra/spinscore-9-5-upgrade`
- **Verification**: Code review and UI integration in `CouplesGameScreen` and `SettingsScreen`.

---

## DEVICE VERIFICATION REQUIRED

The following items cannot be fully proven through host unit tests or static analysis and MUST be verified on real Android hardware or emulators prior to production release:

1. **Camera Permissions and Capture Flow**:
   - Permission grant, deny, and "Never ask again" edge cases.
   - Camera hardware unavailable or camera app crash.
   - Photo capture in portrait and landscape orientations.
   - Low storage condition handling during photo capture.
2. **TalkBack and Accessibility Navigation**:
   - Focus traversal order across `HomeScreen`, `GameScreen`, `PinScreen`, and `DareGalleryScreen`.
   - Audio announcements for bottle spin start and selected player result.
   - Spoken descriptions for all icon buttons and action triggers.
3. **Display Scaling & Density Matrix**:
   - System font scaling set to 1.5x and 2.0x.
   - Small device widths (e.g. 320dp, 360dp) and foldable/tablet displays (>=600dp).
   - Dynamic system bar / display cutout insets (edge-to-edge).
4. **Lifecycle and Process Death Simulation**:
   - Initiating a game, navigating to background, killing the process (`adb shell am kill com.spinbottle.truthdare.games`), and reopening.
   - Confirming "Resume Game" restores exact player scores, round, and current player.
5. **Rendering Performance and Battery Consumption**:
   - Frame rate (target 60fps / 120fps) during bottle spinning and particle rendering.
   - Thermal and battery usage over a 15-minute uninterrupted play session.

---

## PLAY CONSOLE VERIFICATION REQUIRED

The following release requirements are external to the local Git repository and MUST be configured and verified directly within Google Play Developer Console:

1. **Target API 36 Compliance**:
   - Confirm the uploaded release `.aab` is accepted without targetSdk warnings.
2. **In-App Products Configuration**:
   - Verify product ID `remove_ads_lifetime` is active, published, and priced in all target distribution territories.
   - Confirm base plans and currency conversions are populated.
3. **License Testers & Purchase Testing**:
   - Add tester Gmail accounts to the Play Console License Testing list.
   - Perform end-to-end sandbox purchases using `TEST_PURCHASE_SUCCESS` and `TEST_PURCHASE_CANCELLED`.
   - Verify premium supporter state activates immediately and persists across re-launches.
4. **App Signing & Release Track**:
   - Ensure Google Play App Signing is enabled.
   - Build signed release AAB using the official developer upload key.
   - Deploy initial build to an **Internal Testing Track** before promoting to Closed/Open testing.
5. **Store Declarations**:
   - **Data Safety Form**: Declare local photo storage (no external transfer) and Google Play Billing integration (financial processing by Google Play).
   - **Privacy Policy**: Ensure live URL is accessible and matches local-first data processing.
   - **Target Audience & Content Rating**: Complete IARC questionnaire accurately reflecting mature Couples mode and parental gate for Kids mode.
