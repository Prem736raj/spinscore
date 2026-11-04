# Quality and growth implementation — 12 October 2026

Implemented on `codex/app-quality-growth`, following the findings in `APP_REVIEW_2026-10-12.md`. The app identity and production version remain unchanged.

## Growth features

- **One-tap rematch:** Play Again preserves the roster, mode, difficulty, round limit and tournament settings, and resets scores, timer, challenge progress and completion bookkeeping. Couples rematches preserve both partners' agreed preferences and selected packs.
- **Game-night sharing:** Normal game results can share aggregate player, round and challenge counts with a Google Play link. Names, prompt text and photos are excluded. Sharing opens Android's chooser and requires the user to choose a recipient/send. Kids results do not offer sharing.
- **Invites and ratings:** Settings provides an invitation share action and a user-initiated Google Play rating action, with browser/unavailable-app fallback.
- **Shorter sessions:** Classic, Party and Challenge setup offers 10 rounds, 20 rounds or open-ended play. Completed and skipped turns both count. Tournament games retain their score target. Fixed-length games reach results automatically.

These changes support easier repeat play and word of mouth; they do not claim a measured increase in installs or retention. There is no new analytics, account system, advertising or backend.

## Audit resolutions

| Finding | Change |
| --- | --- |
| R01 Release JSON obfuscation | Keep persisted session DTOs, constructors, fields and enum members stable under R8. Add an independently installable minified QA variant and real storage round-trip instrumentation tests. |
| R02 Corrupt session startup | Catch parsing, validation and restoration together; reject invalid players, collections, enums and indexes; clear bad state safely. |
| R03 Discarded Couples setup | Persist the agreed preferences as a draft, pass them into pack selection and persist the selected pack IDs before gameplay. |
| R04 Missing Couples resume | Initialize the domain repository in MainActivity and start/synchronize the shared session for Home resume. |
| R05 Kids boundary bypass | Keep Kids completion/replay in Kids Mode, PIN-gate Home from results and system Back, and route cold starts into the active Kids game/results. Parent PIN verification does not grant adult content access. |
| R06 Adult selection bypasses | Add process-local PIN authorization and audience checks to built-in, custom and favorite selection. Date Night and Extreme packs require PIN; Date Night selects Easy/Medium Couples content and Spicy Night selects Hard/Extreme. Mixed excludes Extreme. Challenge is labeled and gated because it escalates to Extreme. Restored adult packs require unlocking again. |
| R07 Family fallback leak | Empty harder Family pools reuse eligible Family prompts rather than general fallback lists. Unknown legacy favorites cannot enter Family sessions. |
| R08 Couples summary/progress | Apply StartSession, use the dedicated Couples summary and count completed/skipped prompts accurately. Completion rewards remain once per session. |
| R09 Dead Couples camera | Remove the nonfunctional photo control from the consent-based Couples flow. Classic proof capture remains available. |
| R10 Challenge difficulty | Draw from the active challenge level and advance levels on both skips and completions. |
| R11 Quick Fire dialog timer | Pause countdown while the exit dialog is open and show results when ending. Reset persisted seconds when a new prompt starts. |
| R12 Camera recreation | Save pending URI, filename, prompt and player display metadata with rememberSaveable; capture callbacks no longer depend on an unsaveable Player object. |
| R13 Incomplete reset | Reset custom prompts, favorites, settings, theme progress, pack choices and PIN as well as the existing session/history/profile/photo data. Reload manager caches, perform photo I/O off the UI thread, and report failures. Play ownership is preserved. |
| R14 Selected pack fallback | Restrict the catalog to selected pack IDs before history/variety selection; exhausted selected packs repeat within their allowed pool. |
| R15 Classic system Back | Use the same end-game confirmation as the close/menu controls. |
| R16 UI test / retired CI runner | Scroll to Resume and allow its entrance animation. Replace macOS 13 CI with Ubuntu/KVM and API 34/36 plus minified QA coverage. |
| R17 Weak serialization tests/migration | Test the actual session snapshot, corruption, legacy payloads, all modes, rematches and round limits. Validate SHA-256 checksums on newly written envelopes and reject unsupported future versions; legacy snapshots without checksums remain readable. |
| R18 Billing retry | Retry product-details lookup as well as ownership restoration. |

Additional layout/accessibility changes make Quick Fire, Kids, PIN and results content scrollable, constrain long Couples player names, stop decorative infinite animations/confetti under Reduce Motion and make bottle selection immediate under that setting. Active Couples sessions are excluded from both backup formats.

## Validation

Verification completed locally on 12 October 2026. Build logs are under `audit/2026-10-12/`; Android reports are under `app/build/reports/`.

| Check | Result | Evidence |
| --- | --- | --- |
| JVM unit tests | 75 tests across 19 suites; zero failures/errors | `app/build/test-results/testDebugUnitTest/` |
| Debug device suite, API 34 | All 9 tests passed | `audit/2026-10-12/device-debug-tests.log` |
| Minified QA persistence/reset suite, API 34 | All 3 tests passed | `audit/2026-10-12/device-minified-tests.log` |
| Gameplay/Home at 150% font scale, API 34 | All 5 tests passed | `audit/2026-10-12/device-large-font-tests.log` |
| Debug/release lint | Zero errors; 48 warnings and 2 informational hints in each report | `app/build/reports/lint-results-debug.html` and `lint-results-release.html` |
| Debug APK, release APK/AAB, minified QA app/test APK | Build successful | `audit/2026-10-12/final-validation.log`; final QA bridge rebuild in `qa-final-build.log` |
| Patch whitespace validation | `git diff --check` passed | Local Git check |

The installable development build is `app/build/outputs/apk/debug/app-debug.apk`. Production outputs are `app/build/outputs/apk/release/app-release-unsigned.apk` and `app/build/outputs/bundle/release/app-release.aab`; they still need the developer's release signing configuration. Lint warnings remain, so this is not a warning-free build.

The minified test build can be reproduced with:

```powershell
.\gradlew.bat connectedQaAndroidTest -PminifiedTests=true
```

QA uses a separate `.qa` application ID and the debug signing key. It runs the three storage/reset tests in `PersistenceRegressionTest` against the minified app. QA-only rules retain tracing/Kotlin runtime entry points, Compose Color boxing APIs and public data APIs used across the test APK boundary; private Gson fields still depend on the production persistence rules. The full UI suite runs in debug. The production release still needs external signing.

## Publication work outside the local implementation

- The privacy-policy support address is `developeraisteps@gmail.com`, supplied by the developer. Publish the policy at the production privacy-policy URL.
- Configure and verify the existing Google Play product IDs, license testers, content rating, target audience and Data Safety disclosures in Play Console.
- Sign the production AAB/APK with the developer's release credentials and publish through the intended testing/production track. The invite/rating links use the production package's Play listing and require that listing to be available.
- Run API 36 CI and a representative physical-device/font-scale/performance pass before publication. Local API 34 evidence does not replace those checks.
