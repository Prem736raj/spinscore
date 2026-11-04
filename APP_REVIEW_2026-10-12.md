# Spin Bottle / SpinScore app review — 12 October 2026

Reviewed local `master` at `3d80d73f52233fb61b0296c9d983d3886cd533e7`.

**Verdict: buildable, but not ready for release.** The most serious gaps are release session serialization, session restoration, Couples configuration and persistence, and the Kids Safe exit boundary. The previous `AUDIT_FINDINGS.md` and README should not be treated as current proof that these features are complete.

This review does not change application source. It examines the app's navigation, gameplay modes, content selection, persistence, camera/gallery storage, PIN handling, reset behavior, billing, accessibility, tests, and CI. It distinguishes executed checks from defects established through source inspection. A source finding does not imply that its complete scenario was exercised on hardware.

## Executed validation

| Check | Result |
|---|---|
| `testDebugUnitTest --rerun` | **PASS:** 60 tests in 16 suites, 0 failures, 0 errors; freshly executed |
| `lintDebug` | **PASS with warnings:** 0 errors, 45 warnings, 2 hints |
| `lintRelease` | **PASS with warnings:** 0 errors, 45 warnings, 2 hints |
| `assembleDebug` | **PASS** |
| `assembleRelease` | **PASS**, with R8/resource shrinking enabled |
| `bundleRelease` | **PASS** |
| `assembleDebugAndroidTest` | **PASS** |
| `connectedDebugAndroidTest` on Android 14/API 34, `audit34` emulator | **FAIL:** 3 tests executed, 1 failure (`HomeScreenTest.resumeVisibleWhenSessionExists`: component not displayed) |
| Release R8 mapping inspection | **FAIL:** persisted session envelope field names/schema are not preserved; see R01 |

Build command: `./gradlew.bat testDebugUnitTest lintDebug lintRelease assembleDebug assembleRelease bundleRelease assembleDebugAndroidTest --console=plain`.

Fresh test execution: `./gradlew.bat testDebugUnitTest --rerun connectedDebugAndroidTest --console=plain`.

The first build used Gradle's valid incremental outputs where applicable. The second command explicitly reran the unit-test task. No clean build, remote GitHub Actions execution, or Play Console validation was performed. Release compilation produces an unsigned release APK; a temporary copy was signed with the local debug key for emulator inspection, not for publication.

Local reports:

- `app/build/reports/tests/testDebugUnitTest/index.html`
- `app/build/reports/lint-results-debug.html`
- `app/build/reports/lint-results-release.html`
- `app/build/reports/androidTests/connected/debug/index.html`
- `app/build/outputs/mapping/release/mapping.txt`

## Findings

P1 means a high-priority correctness, privacy, content-boundary, or release issue. P2 means a functional, UX, or verification issue that should be addressed before calling its affected feature complete.

### R01 — P1: Release shrinking breaks persisted sessions

**Evidence:** `game/SessionEnvelope.kt`, `data/GameSessionHolder.kt:328`, `game/SessionMigration.kt:22`, and `app/proguard-rules.pro`.

The app writes a `SessionEnvelope` with Gson but the keep rules cover only the older snapshot/player models. The freshly generated release mapping shows `SessionEnvelope.payload -> a` and removal of `schemaVersion`. The loader explicitly looks for JSON keys `schemaVersion` and `payload`. A release-written envelope consequently does not match the loader's envelope path and is instead interpreted as a legacy snapshot. The active players/start time are then absent from that snapshot, losing the resumable game. Debug tests cannot detect this transformation.

**Fix:** use stable `@SerializedName` fields or explicit serialization, retain the schema field, and test save/relaunch in the actual minified release. Add the Couples persistence models to the same serialization review; their fields are also renamed by R8.

### R02 — P1: Malformed saved sessions can crash every startup

**Evidence:** `data/GameSessionHolder.kt:133–138`, `data/GameSessionHolder.kt:335`, `game/SessionMigration.kt:37`.

Only `gson.fromJson()` runs inside `runCatching`. `restore(snapshot)` runs inside `onSuccess`, outside the protected block. Valid JSON such as `null`, or a snapshot with `players: null` / `eliminatedPlayers: null`, can therefore throw during restoration without clearing the stored value. MainActivity invokes this restoration before rendering the app. `SessionValidator` exists but is never used by production code.

**Reproduction:** put such a value in `session_state_prefs.xml` under `active_session`, then launch. The parser accepts the JSON; restoration dereferences invalid state.

**Fix:** deserialize, validate, and restore inside one recovery boundary; reject unsupported schemas and malformed collections; clear invalid data and reset any partially restored in-memory state.

### R03 — P1: Couples comfort preferences and pack choices are discarded

**Evidence:** `navigation/AppNavigation.kt:134`, `:142`, `:144`, `:198`; `screens/CouplesGameScreen.kt:45`.

The comfort form computes the partners' intersection, but `onSetupComplete { prefs -> ... }` never saves/passes `prefs`. Pack selection receives a new default `CouplesPreferences()`. Its `packIds` result is also ignored. The gameplay screen consequently receives its default preferences and default four packs.

Disabling flirty conversation does not reach gameplay; enabling agreed physical affection does not enable those prompts; enabling After Dark cannot unlock its pack. This invalidates the user-visible promise that gameplay reflects both partners' choices.

**Fix:** preserve a session configuration through both setup routes, pass it into gameplay, and exercise the complete navigation flow in tests with restrictive and permissive choices.

### R04 — P1: Couples sessions are not persisted or discoverable for resume

**Evidence:** `couples/CouplesSessionRepository.kt:19`, `screens/couples/CouplesGameViewModel.kt:26`, `screens/CouplesGameScreen.kt:99`, `MainActivity.kt:31`, `data/GameSessionHolder.kt:142`.

There is no production call to `CouplesSessionRepository.init(context)`. Its nullable preferences make every save a silent no-op and every load return null. In addition, the new Couples screen never calls `GameSessionHolder.startGame()`, while Home requires its `gameStartTime > 0` to show Resume Game. A normal newly started Couples game therefore has neither durable domain progress nor a Home resume entry.

**Fix:** initialize storage, establish one active session identity/start time, restore prompt/preferences/blocked tags/turn state, and clear the Couples repository on new game and completion. Explicitly exclude `couples_session_prefs.xml` from backup before enabling that storage; the current shared-preference allowlist would otherwise include it.

### R05 — P1: Kids Safe can exit without the parent PIN

**Evidence:** `screens/KidsSafeGameScreen.kt:85`, `navigation/AppNavigation.kt:216`, `:226`, `:44`.

The back button and lock icon request a PIN, but automatic completion after five turns per player calls the ordinary GameCompletion route. That screen provides Home and Play Again without parental verification. Play Again clears `isKidsModeFlow`, allowing the ordinary mode-selection flow. A cold launch also goes through Splash to unrestricted Home even when the saved active session is Kids Safe.

**Reproduction:** finish a Kids Safe game and choose Home/Play Again, or stop/reopen the app during Kids Safe. Neither route checks the PIN. This concerns the in-app boundary, not Android device kiosk security.

**Fix:** enforce the boundary at navigation/session level, including completion, restored sessions, and app entry. Keep new Kids games in the Kids flow until a verified parent explicitly exits.

### R06 — P1: Adult pack locking is bypassable through shared categories

**Evidence:** `data/PromptPackManager.kt:40–57`, `data/GameState.kt:124–138`, `data/PromptsDatabase.kt:1360`.

Date Night has no PIN requirement; Spicy Night requires one. Both resolve to exactly the same `COUPLES` database category. Gameplay filters only by the union of categories, so it cannot distinguish which of those two packs was enabled. Selecting the ungated pack makes the same category available. Mixed difficulty also includes all database difficulties and does not itself require a PIN. Custom and Favorites difficulty paths ignore pack/category restrictions entirely.

**Fix:** represent pack membership and audience on individual prompts, enforce the active audience/authorization in the selector, and test all selection entry points. The `AudienceClass` enum currently has no production consumers and does not enforce the isolation claimed in the README.

### R07 — P1: Family-only selection falls back to general prompts

**Evidence:** `data/GameState.kt:174–183`, `:209–218`; `data/PromptsDatabase.kt:794`, `:870`.

Kids/Family database prompts are Easy. Selecting only Family Game Night with Medium or Hard yields no database candidates. `getRandomTruth()`/`getRandomDare()` then return generic fallback lists without the selected categories. The Family label therefore does not establish a content boundary. Kids Safe's separate hardcoded prompt function is unaffected by this particular fallback.

**Fix:** keep fallback selection within the same audience/packs, select an allowed difficulty or show a clear empty state. Include empty filtered pools in family-isolation tests.

### R08 — P2: Couples completion reports zero rounds/scores

**Evidence:** `screens/couples/CouplesGameViewModel.kt:35`, `couples/CouplesReducer.kt:24`, `navigation/AppNavigation.kt:200`, `:226`.

The reducer updates only `CouplesSessionState`. Completing/skipping never updates `GameSessionHolder.players`, `totalRounds`, or its start time. Navigation nevertheless uses the generic results screen, which reads those fields. The UI advertises `Complete (+1)` while the resulting scores and lifetime statistics do not reflect Couples activity. `CouplesSessionSummaryScreen` exists but is never routed to. The initial round also starts at zero because `StartSession` is never dispatched.

**Fix:** route to the Couples summary and persist its stats deliberately, or bridge updates to the shared scoring model. Start the domain session through its reducer.

### R09 — P2: Couples camera control does nothing

**Evidence:** `screens/CouplesGameScreen.kt:252`.

The visible Capture dare photo button has an empty callback containing only a comment. It cannot capture a proof. The After Dark visibility check does not make this a working camera feature.

**Fix:** connect the proof capture workflow with the intended privacy policy, or remove the button until it is implemented.

### R10 — P2: Challenge levels change the banner, not prompt difficulty

**Evidence:** `screens/GameScreen.kt:253`, `:274`, `:470`; `data/GameSessionHolder.kt:144`.

Challenge completion advances `currentChallengeLevel`, but prompt draws always use `gameState.difficulty`. `getChallengeDifficulty()` is never called. A player can reach Level 4 while continuing to receive the originally selected difficulty. Skips do not advance challenge rounds despite the mode description saying it escalates every five rounds.

**Fix:** derive the effective draw difficulty from the current Challenge level and define whether skips count as rounds.

### R11 — P2: Quick Fire keeps timing out behind the exit dialog

**Evidence:** `screens/QuickFireGameScreen.kt:93`, `:152`.

Opening End Quick Fire changes `showExitDialog`, but the countdown effect depends only on running/player/lifecycle state. The Activity stays resumed while the dialog is open, so time continues to decrement and can automatically record a skip/advance the player. Continuing play can unexpectedly return to a different turn.

**Fix:** pause the timer while a blocking modal is open and resume only when the user continues.

### R12 — P2: Camera result metadata is lost on Activity/process recreation

**Evidence:** `screens/GameScreen.kt:74–88`.

Pending proof filename, player, and dare text are held only in `remember`. If Android recreates the app while the external camera is active, the returning success callback has none of that metadata and cannot add the captured image to the gallery. The file can remain untracked until orphan cleanup. This scenario was established from the state lifetime, not a completed hardware-camera test.

**Fix:** save the small pending-capture record in saved state or durable storage before launching the camera, then consume it once on return. Android explicitly requires saving additional result-handling state separately: [Activity Result guidance](https://developer.android.com/training/basics/intents/result).

### R13 — P2: Reset All App Data leaves user data behind and hides failures

**Evidence:** `data/AppDataResetManager.kt:24–29`, `screens/SettingsScreen.kt:435`.

The reset clears sessions, history, proof photos, and profiles, but never clears custom prompts, favorites, settings, theme/achievement state, selected packs, or PIN/adult confirmation state. The button says Reset All App Data / Reset Everything. The proof-deletion Boolean is ignored, so a failed photo deletion silently closes the dialog after a partial reset. The operations are sequential, not atomic as described in the README.

**Fix:** define the full reset contract, clear all intended stores, preserve Play purchases, and show/retry deletion failures. If preserving settings/PIN is intentional, give the action an accurate narrower name.

### R14 — P2: Couples engine can draw outside selected packs

**Evidence:** `couples/CouplesPromptEngine.kt:30–40`, `screens/couples/CouplesGameViewModel.kt:66–76`.

Pack IDs are converted to preferred tags rather than a hard allowed pool. The engine removes recently seen prompts before choosing packs; when the remaining unseen prompts do not match selected tags, it falls back to all allowed packs instead of repeating within the selected pack. This remains a separate selection bug after the navigation wiring in R03 is repaired.

**Fix:** restrict the catalog by selected pack membership first, then perform repeat avoidance within that pool.

### R15 — P2: System Back bypasses the Classic game's exit/results flow

**Evidence:** `screens/GameScreen.kt:50`, `navigation/AppNavigation.kt:176`.

Classic/Party/Challenge gameplay shows an End Game confirmation through its close icon but has no `BackHandler`. Android Back pops the game route directly to setup/difficulty or Home. Completion statistics and theme progress are recorded only on the results screen, so this route also bypasses those side effects.

**Fix:** handle system Back through the same exit/pause decision as the close icon, with consistent resume/end behavior.

### R16 — P2: UI verification gate fails and CI uses a retired runner

**Evidence:** executed connected test report; `.github/workflows/android-instrumentation.yml:18`.

One of the three UI tests failed on the available API 34 emulator. Its assertion does not establish whether the cause is layout, scrolling, animation synchronization, or test setup; diagnose it before labeling the UI gate green. The tests cover two Home button cases and the invalid-player Couples screen, not successful end-to-end gameplay, consent navigation, PIN escape paths, camera recovery, or release serialization.

Separately, the workflow requests `macos-13`, which GitHub retired in December 2025. Configuration in the repository is not evidence of a passing CI run. [GitHub retirement announcement](https://github.blog/changelog/2025-09-19-github-actions-macos-13-runner-image-is-closing-down/).

**Fix:** move to an available runner with compatible emulator architecture, repair the failing UI check, and add a small set of end-to-end tests for the P1 paths.

### R17 — P2: Session tests do not exercise the actual serializer/restore path

**Evidence:** `src/test/.../data/SessionSerializationTest.kt:64`, `src/test/.../game/SessionMigrationTest.kt`, `game/SessionMigration.kt:27`.

The serialization tests round-trip a handwritten JSON string through `Map`, rather than the real private snapshot/envelope models or `GameSessionHolder.init()`. They cannot detect missing Gson keep rules, invalid snapshot restoration, or release-only data loss. The migration pipeline also accepts every unknown schema version as though it were current and has no checksum, despite the README claiming checksum validation.

**Fix:** exercise the actual persistence boundary with full, legacy, corrupted, and future-version snapshots. Add a minified-release relaunch check. Document the implemented recovery semantics accurately.

### R18 — P2: Purchase retry does not reload failed product details

**Evidence:** `billing/BillingManager.kt:133`, `:173`; `screens/PremiumScreen.kt` Restore purchases callback.

Product details load on connection setup. If that request fails while ownership queries succeed, the purchase screen has no product/price. Restore purchases calls only `refreshPurchases()`, which, when the client is already ready, queries ownership but does not retry product details. Recovering connectivity and tapping Restore can therefore leave the purchase option unavailable until a new BillingManager is created.

**Fix:** provide a retry that refreshes both product details and ownership. Verify actual purchases, refunds, pending purchases, restore, and acknowledgement with Play license testers; no real purchase was made in this review.

## Additional completeness gaps

- `targetRounds` is stored and tested but never read by gameplay or configured by the active UI. The README's claim that configurable round limits are enforced does not match the current implementation.
- Reduce Motion changes only particle count; the particles still move/twinkle and the other infinite bounce/glow/confetti animations remain active. Verify the intended accessibility contract before advertising broad motion reduction.
- Quick Fire and Kids gameplay use fixed sizes/non-scrollable layouts; the two-player header has no width allocation for long names. Small screens and 1.5x/2x font scale need actual device checks; no blanket responsive-layout pass is warranted.
- Mid-game Add Player does not apply setup's normalized duplicate-name check or 16-player limit.
- The production privacy document still contains a contact-address placeholder. A signed release build, Play App Signing, product configuration, rating/audience declarations, and a published privacy URL remain external release gates.

## What is working or sensibly designed

- The project builds debug and minified release artifacts and has a useful foundation of pure domain tests.
- The comfort-intersection function itself uses restrictive Boolean intersection; the current defect is integration with navigation/gameplay.
- Kids Safe uses a separate hardcoded prompt source, protecting it from the general selector's fallback bug while the Kids screen remains active.
- Dare-proof paths are canonicalized, constrained to app-private storage, and shared through a narrowly scoped FileProvider.
- Current backup rules exclude the main active session and, through their shared-preference-only allowlist, exclude the PIN DataStore and proof files/metadata.
- Billing distinguishes pending/confirmed purchases, does not invent fallback prices, and avoids clearing an already verified entitlement after a partial ownership-query failure.

## Recommended repair order

1. Repair release JSON serialization and safe restoration (R01–R02).
2. Enforce Couples configuration and persistence and navigation-level Kids boundaries (R03–R05).
3. Make audience/pack filtering authoritative, including fallback, Custom, and Favorites (R06–R07, R14).
4. Fix results, Challenge progression, Quick Fire modal pause, and camera recovery (R08–R12, R15).
5. Complete reset/retry behavior and repair the verification pipeline (R13, R16–R18).
6. Run the corrected end-to-end flows on minified release, small displays/large fonts, and physical devices; then complete the Play Console checks.

## Verification limits

No live Play Console access, real billing transaction, physical camera/permission matrix, TalkBack traversal, battery/frame-time profiling, or broad Android-version/device matrix was completed. The available emulator is API 34; target API 36 behavior still needs device coverage. The emulator became unresponsive during exploratory UI work and was restarted with a cold boot and a lower memory allocation. Do not treat incomplete exploratory screenshots or a source-inspected scenario as a completed runtime test.
