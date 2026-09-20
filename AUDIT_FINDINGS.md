# SpinScore / Spin Bottle Production-Readiness Audit

Baseline audited: `master` at `560b80a6e0453a57f62cbcfea9bf88a845e88212`  
Hardening branch: `astra/spinscore-production-hardening`  
Audit date: 2026-09-20

## Status legend

- OPEN
- IN_PROGRESS
- FIXED
- DEFERRED
- VERIFY_DEVICE
- VERIFY_PLAY_CONSOLE

## Baseline build

| Gate | Status | Evidence |
|---|---|---|
| `./gradlew clean` | NOT RUN | No Android SDK/network-capable local runner is available in the audit environment. |
| `./gradlew assembleDebug` | NOT RUN | Same limitation. |
| `./gradlew testDebugUnitTest` | NOT RUN | Repository contains no unit tests at baseline. |
| `./gradlew lintDebug` | NOT RUN | Same runner limitation. |
| `assembleRelease` | NOT RUN | Deferred until hardening branch CI exists. |
| `lintRelease` | NOT RUN | Deferred until hardening branch CI exists. |
| `bundleRelease` | NOT RUN | Deferred until hardening branch CI exists. |

The committed `build_log.txt` records an older failed Windows build ending in `What went wrong: 25.0.1`. It is evidence of a historical failure, not proof that the current commit fails today. The current root cause will be established by clean CI rather than guessed.

## Architecture map

Single-module Android application using Kotlin, Jetpack Compose, Material 3 and Navigation Compose. State ownership is mixed:

- screen-local Compose `remember` state;
- process-global singleton holders/managers;
- SharedPreferences/Gson for profiles, custom prompts, favorites, prompt packs and theme data;
- DataStore for PIN/age state;
- no ViewModel layer;
- no database;
- no backend/network stack;
- Google Play Billing client held by `MainActivity`.

This architecture can remain single-module and local/offline. The main reliability problem is not absence of Clean Architecture or Hilt; it is that active game state is process memory only.

## Confirmed baseline findings

| ID | Priority | Status | Area | Evidence / current behavior | File |
|---|---|---|---|---|---|
| BUILD-001 | P0 | OPEN | Build portability | `org.gradle.java.home=C:/Program Files/Android/Android Studio/jbr` hard-codes one Windows machine path. | `gradle.properties` |
| PLAY-001 | P0 | OPEN | Play readiness | App targets API 34. As of 2026-08-31, new mobile apps/updates must target API 36 unless a temporary Play Console extension applies. | `app/build.gradle.kts` |
| BILL-001 | P0 | OPEN | Billing | Billing Client 6.1.0 is beyond its new-app/update deadline. | `app/build.gradle.kts` |
| BILL-002 | P0 | OPEN | Entitlement | Purchase queries process SUBS and INAPP independently and only ever set premium to true. Expiration/refund/revocation cannot clear an already-true in-process entitlement. | `billing/BillingManager.kt::queryPurchases/processPurchases` |
| BILL-003 | P1 | OPEN | Pricing | Premium UI falls back to literal `$1.99` / `$9.99` when Play product details are unavailable. | `screens/PremiumScreen.kt` |
| BILL-004 | P1 | OPEN | Premium copy | UI advertises “Remove All Ads”, but there is no advertising SDK or ad rendering code in the repository. | `screens/PremiumScreen.kt`, dependencies/search |
| BILL-005 | P2 | OPEN | Billing lifecycle | Billing disconnect logs “reconnecting” but performs no retry; no explicit lifecycle close. | `billing/BillingManager.kt` |
| BILL-006 | P2 | OPEN | Billing UX | Product loading/error/pending/cancel/already-owned states are not exposed; taps silently no-op when ProductDetails is absent. | `billing/BillingManager.kt`, `screens/PremiumScreen.kt` |
| BILL-007 | P3 | OPEN | Dead code | `PremiumPreferences` is unused and would create a second premium source of truth if adopted. | `data/PremiumPreferences.kt` |
| GAME-001 | P1 | OPEN | Difficulty | Selected difficulty is screen-local and is never written to `GameSessionHolder.difficulty` before starting. Gameplay therefore uses the holder default unless changed later in-game. | `screens/DifficultySelectionScreen.kt` |
| GAME-002 | P1 | OPEN | Process death | Players, scores, mode, difficulty, round/start time, tournament state and Kids-flow state live in `GameSessionHolder` only and are lost on process death. | `data/GameSessionHolder.kt` |
| GAME-003 | P1 | OPEN | Spin correctness | Bottle/player mapping floors sectors from each player angle instead of selecting the nearest player. Example with 4 players: a 350° final angle visually points near player 0 but logic returns player 3. | `screens/GameScreen.kt::getSelectedPlayerIndex`, `ui/components/SpinningBottle.kt` |
| GAME-004 | P2 | OPEN | Tournament | “Elimination Mode” is exposed in UI, but `eliminatePlayer()` has no call site. | `screens/DifficultySelectionScreen.kt`, `data/GameSessionHolder.kt` |
| GAME-005 | P2 | OPEN | Tournament | `targetRounds` exists but has no configuration/use path. | `data/GameSessionHolder.kt` |
| GAME-006 | P2 | OPEN | Quick Fire lifecycle | Timer is a composition coroutine and is not lifecycle-paused; it can continue while app is backgrounded. | `screens/QuickFireGameScreen.kt` |
| GAME-007 | P2 | OPEN | Couples | Couples mode silently truncates the session to `players.take(2)`; setup/mode UI does not communicate or enforce exactly two players. | `screens/CouplesGameScreen.kt` |
| PROMPT-001 | P1 | OPEN | Prompt packs | Pack selections are persisted/displayed, but normal gameplay always requests `PromptCategory.FRIENDS`; enabled packs are never consumed. | `data/GameState.kt::GamePrompts`, `data/PromptPackManager.kt` |
| PROMPT-002 | P2 | OPEN | History | `PromptHistoryManager.avoidRecentlyPlayed` is user-visible, but gameplay marks prompts as seen without consulting the setting/history. | `data/GameState.kt`, `data/FavoritesManager.kt::PromptHistoryManager` |
| PROMPT-003 | P2 | OPEN | Prompt data | Static analysis found 12 normalized duplicate prompt groups in the 1,013 built-in prompt strings. | `data/PromptsDatabase.kt` |
| SAFETY-001 | P0 | OPEN | Content safety | Built-in dares pressure device/account access or third-party contact, e.g. browser history, camera roll, letting others control/text from the phone, posting from social accounts, prank/random calls. | `data/PromptsDatabase.kt`, `data/GameState.kt` fallbacks |
| SAFETY-002 | P1 | OPEN | Content safety | Built-ins include avoidable physical/financial risk such as holding breath “as long as you can”, mystery food chosen by others, hot sauce shot, handstand attempts and purchasing an item selected by the group. | `data/PromptsDatabase.kt` |
| KIDS-001 | P0 | OPEN | Kids exit | Kids screen has no `BackHandler`; system/predictive Back can pop the route without parental PIN. | `screens/KidsSafeGameScreen.kt`, navigation |
| KIDS-002 | P0 | OPEN | Kids exit | If no PIN exists, opening the verify PIN screen can transition to PIN setup, so the restriction is not a reliable parent-only exit boundary. | `screens/KidsSafeGameScreen.kt`, `screens/PinScreen.kt` |
| KIDS-003 | P1 | FIXED_BASELINE | Prompt isolation | The dedicated Kids Safe screen does not consume custom/favorite/couples/general prompt pipelines; it uses its own local safe list. This is a good existing isolation property to preserve. | `screens/KidsSafeGameScreen.kt` |
| AGE-001 | P1 | OPEN | Mature gate | Current “age verification” is only a self-attestation checkbox. It must not be represented as strong age verification; a neutral age-screen design is preferable for a mixed-audience product. | `screens/AgeVerificationDialog.kt` |
| PIN-001 | P2 | OPEN | PIN | PIN is stored as plaintext in DataStore and lockout uses wall-clock time; suitable only as a lightweight content gate, not high-security authentication. | `data/PinManager.kt` |
| PIN-002 | P2 | OPEN | PIN | Manager setter does not independently validate exactly four digits even though UI does. | `data/PinManager.kt` |
| PHOTO-001 | P0 | OPEN | Proof deletion | Captured proofs store a FileProvider `content://` URI. Gallery deletion converts URI.path to `File`, which is not the underlying internal file path; metadata can disappear while private photo remains. | `data/DareProofManager.kt`, `screens/DareGalleryScreen.kt` |
| PHOTO-002 | P1 | OPEN | Proof persistence | Metadata is in memory only; loader reconstructs generic “Previous dare” / “Player” data and is not invoked at app initialization. | `data/DareProofManager.kt`, `MainActivity.kt` |
| PHOTO-003 | P1 | OPEN | Proof sharing | Sharing repeats the same incorrect `content:// URI.path -> File` conversion and can fail for newly captured photos. | `screens/DareGalleryScreen.kt` |
| PHOTO-004 | P1 | OPEN | FileProvider | Provider exposes the entire cache root (`<cache-path path="/">`) even though the proof flow uses only the internal proof directory. | `res/xml/file_paths.xml` |
| PHOTO-005 | P2 | OPEN | Camera | Capture pre-creates a file before permission/result success; denial/cancellation can leave orphan files. Availability/permanent-denial UX is incomplete. | `screens/GameScreen.kt` |
| PRIV-001 | P1 | OPEN | Backup | Cloud backup explicitly includes shared preferences/databases, while Android 12+ device transfer has no section and therefore defaults broadly. Sensitive local policy must be explicit, especially for proof photos/PIN state. | `res/xml/backup_rules.xml`, `data_extraction_rules.xml` |
| NET-001 | P1 | OPEN | Feature truthfulness | No WebSocket/Firebase/Supabase/REST/network multiplayer implementation exists despite the INTERNET comment claiming online multiplayer. | Manifest + repository search |
| LINK-001 | P1 | OPEN | Deep links | Manifest advertises auto-verified `https://spinbottle.app/join`, but there is no join-room route/parser/backend implementation. | `AndroidManifest.xml`, repository search |
| SETTINGS-001 | P2 | OPEN | Persistence | Sound, haptics, spin speed and default difficulty live only in `SettingsHolder`; restart/process death resets them. | `data/SettingsHolder.kt` |
| SETTINGS-002 | P2 | OPEN | Behavior | Spin-speed setting is not consumed by the bottle animation, which uses a fixed 4,000 ms duration. | `data/SettingsHolder.kt`, `ui/components/SpinningBottle.kt` |
| THEME-001 | P2 | OPEN | Unlocks | Theme unlock counters exist, but no game-completion call site updates `ThemeManager` statistics. Locked themes therefore cannot unlock through normal play. | `data/ThemeManager.kt`, repository search |
| THEME-002 | P3 | OPEN | Integrity | Selected theme setters do not enforce unlock status; UI blocks clicks, but stale/manipulated preference state can still select a locked item. | `data/ThemeManager.kt` |
| NAV-001 | P3 | OPEN | Navigation | `CustomPrompts` and `MyPrompts` routes render the same screen; one route is redundant unless retained for compatibility. | `navigation/AppNavigation.kt`, `Screen.kt` |
| PLAYER-001 | P2 | OPEN | Players | UI allows two players with the same normalized name; both map to the same persistent profile and completion stats aggregate ambiguously. | `screens/GameSetupScreen.kt`, `data/PlayerProfileManager.kt` |
| CUSTOM-001 | P2 | FIXED_BASELINE | Custom prompts | Editor trims input, enforces 10–200 characters, supports Unicode/multiline, and requires a valid type/difficulty/category. | `screens/AddEditPromptScreen.kt` |
| A11Y-001 | P2 | OPEN | Accessibility | Kids parent-control IconButton is explicitly sized 36 dp; custom prompt action IconButtons are 32 dp, below 48 dp layout guidance. | `screens/KidsSafeGameScreen.kt`, `screens/MyPromptsScreen.kt` |
| A11Y-002 | P2 | OPEN | Reduced motion | Home particles, pulsing controls, Kids floating/bounce effects, Couples hearts and completion confetti/trophy animations have no reduced-motion branch. | multiple Compose screens/components |
| PERF-001 | P2 | VERIFY_DEVICE | Rendering | Multiple infinite animations/particle systems are statically visible. Frame time, battery and low-end-device cost require Macrobenchmark/device profiling before numeric claims. | UI components/screens |
| REPO-001 | P2 | OPEN | Repository | No README, no CI and no meaningful test source directories at baseline. | repository tree |
| REPO-002 | P3 | OPEN | Repository | IDE metadata and stale build log are committed; local runner script hard-codes the developer SDK path. | `.idea/`, `build_log.txt`, `andrun.bat` |
| R8-001 | P2 | OPEN | Release | ProGuard keeps all `androidx.compose.**`, defeating much of release shrinking without demonstrated need. | `app/proguard-rules.pro` |
| SIGN-001 | P1 | VERIFY_PLAY_CONSOLE | Signing | No production signing config or key is committed (correct). Play App Signing / upload key status cannot be verified from repo. | repository / Play Console unavailable |
| PLAY-002 | P1 | VERIFY_PLAY_CONSOLE | Product IDs | Code uses `remove_ads_monthly` and `remove_ads_lifetime`; existence/configuration of those exact products cannot be confirmed from repo. | `billing/BillingManager.kt` |
| PLAY-003 | P1 | VERIFY_PLAY_CONSOLE | Audience/rating | Mixed Kids Safe + mature/couples content requires accurate target audience/content-rating declarations. Play Console state is unavailable. | product behavior |
| PLAY-004 | P1 | VERIFY_PLAY_CONSOLE | Store assets | Privacy policy URL, Data Safety, screenshots, feature graphic, listing copy, category, testing tracks and release declarations cannot be verified from repo. | Play Console unavailable |

## Keep as-is

- Application ID / namespace `com.spinbottle.truthdare.games`: do not rename during hardening.
- Single-module/local-first architecture: no evidence requires Hilt, Room, Firebase, Supabase or a backend.
- Dedicated Kids prompt source: keep its data isolation and add exit/gate tests.
- Camera files are under app-private `filesDir/dare_proofs`; keep private storage and tighten sharing.
- Player setup already caps names at 20 characters and total players at 16.
- Custom prompt editor already has meaningful length validation and destructive-delete confirmation.
- Haptic manager already respects the app-level disabled setting and Android-version vibration APIs.
- Release build already enables minification/resource shrinking; keep that direction while removing cargo-cult R8 rules.

## Remove / simplify candidates

1. Dead `/join` app link until room joining actually exists.
2. Misleading online-multiplayer INTERNET comment/permission if no remaining dependency requires it.
3. “Remove All Ads” premium benefit while the app contains no ad product.
4. Unused `PremiumPreferences`.
5. Non-functional tournament elimination UI until the mechanic is implemented and tested.
6. Duplicate `CustomPrompts` navigation route.
7. Committed `.idea` and stale build log; archive useful research under `docs/` instead of deleting it blindly.

## Device verification required

- Camera permission granted/denied/permanently denied, camera unavailable, cancellation, rotation/background and low-storage behavior.
- Visual bottle/player alignment across densities/orientations.
- TalkBack/switch/keyboard navigation.
- 100/130/150/200% font scale.
- Compact/tall phone, landscape, tablet/foldable behavior.
- Edge-to-edge on Android 15/16 and predictive Back.
- 10/50/100+ proof-photo scrolling/memory.
- Startup/rendering/frame-time/battery measurements on representative low/mid/high devices.

## Play Console verification required

- Whether target-SDK extension exists (hardening will target API 36 regardless).
- Exact billing products/base plans/offers/regions/prices.
- License testers and purchase test matrix.
- App signing/upload key.
- Target audience, Families applicability, IARC content rating.
- Data Safety declarations and privacy-policy URL.
- Store listing assets/copy and testing/release tracks.

## Planned dependency posture

- AGP 8.13.2 + Gradle 8.13: KEEP; official compatibility supports API 36.1 and JDK 17.
- Kotlin 1.9.22 + Compose compiler 1.5.8: KEEP for this hardening branch; they are an officially compatible pair.
- Older AndroidX/Compose BOM/DataStore/Coroutines/Gson/Coil: OUTDATED BUT SAFE based on repository evidence; upgrade only if a fix needs it.
- Play Billing 6.1.0: SECURITY/STORE-COMPLIANCE UPDATE REQUIRED; migrate to current supported Billing 9.1.x with API changes and tests.
- compileSdk/targetSdk 34: UPDATE REQUIRED to 36 for current Google Play submission.

## Initial scorecard (before fixes)

| Area | Score / 10 |
|---|---:|
| Build health | 2 |
| Core gameplay | 5 |
| Spin correctness | 3 |
| Game-state reliability | 2 |
| Prompt quality | 4 |
| Kids Safe | 4 |
| Mature-content controls | 4 |
| Camera/proof | 3 |
| Billing | 2 |
| Premium UX | 2 |
| UI/UX | 6 |
| Accessibility | 4 |
| Performance | 5 |
| Battery | 5 |
| Privacy | 4 |
| Security | 5 |
| Architecture | 5 |
| Maintainability | 4 |
| Testing | 0 |
| CI | 0 |
| Repository quality | 3 |
| Play readiness | 2 |
| Product differentiation | 5 |

Overall baseline production readiness: **3.5 / 10**. This is a release-hardening score, not a judgement of the product idea.

## Implementation order

1. Build portability + API 36 + branch CI.
2. Billing migration/entitlement correctness + truthful premium UI.
3. Dare-proof metadata/file lifecycle + FileProvider/backup tightening.
4. Kids exit boundary + mature-content gate terminology/flow.
5. Difficulty/session persistence + spin mapping + prompt packs/history.
6. Unsafe prompt replacement and prompt-safety regression tests.
7. Settings/theme/player correctness.
8. Quick Fire lifecycle, accessibility/reduced motion, repository/R8 cleanup.
9. Full unit/UI test matrix where host/device infrastructure permits.
10. Final release gates and PR; do not merge to `master`.
